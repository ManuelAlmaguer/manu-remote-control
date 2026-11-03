#!/usr/bin/env python3
"""
Generador del catálogo extendido de Manu Remote Control.

Lee la base de datos abierta Flipper-IRDB (licencia CC0 1.0) y produce el asset
comprimido que la app importa a Room al arrancar:

    app/src/main/assets/catalog/manu_catalog.tsv.gz

Uso:
    python3 tools/ir_catalog/build_catalog.py --irdb /ruta/a/Flipper-IRDB [--out <ruta>]

Formato del asset (UTF-8, gzip, líneas separadas por \\n, campos por TAB):

    MU1                                  cabecera de versión del formato
    D  key name brand model type freq description source
    B  key label role section protocol freq address command bits toggle repeats raw

`protocol` es el nombre del enum IrProtocolId de la app: NEC, NEC_EXTENDED,
SAMSUNG, SONY_SIRC, RC5, KASEIKYO, PIONEER, RAW. Para RAW el campo `raw` lleva el
patrón en microsegundos separado por comas.

El script es idempotente y determinista: mismo IRDB -> mismo asset.
"""

from __future__ import annotations

import argparse
import gzip
import os
import re
import sys
from collections import OrderedDict, defaultdict

FORMAT_VERSION = "MU1"
SOURCE = "Flipper-IRDB · CC0 1.0"

# --------------------------------------------------------------------------------------
# Categorías de IRDB -> DeviceType de la app
# --------------------------------------------------------------------------------------
CATEGORY_TYPE = {
    "TVs": "TV",
    "TV_Tuner": "TV",
    "Universal_TV_Remotes": "TV",
    "ACs": "AC",
    "Air_Purifiers": "OTHER",
    "Humidifiers": "OTHER",
    "Heaters": "OTHER",
    "Fireplaces": "OTHER",
    "DVD_Players": "DVD",
    "Blu-Ray": "DVD",
    "Laserdisc": "DVD",
    "VCR": "DVD",
    "Projectors": "PROJECTOR",
    "SoundBars": "AUDIO",
    "Audio_and_Video_Receivers": "AUDIO",
    "Speakers": "AUDIO",
    "CD_Players": "AUDIO",
    "MiniDisc": "AUDIO",
    "Head_Units": "AUDIO",
    "Car_Multimedia": "AUDIO",
    "Fans": "FAN",
    "LED_Lighting": "LIGHT",
    "Cable_Boxes": "OTHER",
    "Streaming_Devices": "OTHER",
    "DVB-T": "OTHER",
    "Converters": "OTHER",
    "Multimedia": "OTHER",
    "Monitors": "OTHER",
    "Cameras": "OTHER",
    "CCTV": "OTHER",
    "Videoconferencing": "OTHER",
    "Whiteboards": "OTHER",
    "Digital_Signs": "OTHER",
    "Toys": "OTHER",
    "Vacuum_Cleaners": "OTHER",
    "Clocks": "OTHER",
    "Bidet": "OTHER",
    "Miscellaneous": "OTHER",
    "Picture_Frames": "OTHER",
    "Touchscreen_Displays": "OTHER",
    "Dust_Collectors": "OTHER",
    "Window_cleaners": "OTHER",
    "Handicap_Ceiling_Lifts": "OTHER",
    "KVM": "OTHER",
    "Computers": "OTHER",
    "Consoles": "OTHER",
    "Intercoms": "OTHER",
}

# Presupuesto de dispositivos por marca y categoría (mantiene el asset ligero y útil).
BRAND_LIMIT = {
    "TV": 3,
    "AC": 2,
    "DVD": 2,
    "PROJECTOR": 2,
    "AUDIO": 2,
    "FAN": 2,
    "LIGHT": 2,
    "OTHER": 1,
}

AC_BUTTON_LIMIT = 26          # los mandos de aire llevan tramas RAW enormes
MAX_RAW_SAMPLES = 2400        # descarta tramas absurdas
MAX_BUTTONS_PER_DEVICE = 90

# Marcas prioritarias (se añaden primero y sin recortes, ver priority_profiles.py)
PRIORITY_BRANDS = ("Haitech", "ATEC", "Panda", "HELECT")

# --------------------------------------------------------------------------------------
# Nombres de botones IRDB -> (etiqueta ES, rol, sección)
# --------------------------------------------------------------------------------------
ROLE_SECTION = {
    "POWER": "POWER", "POWER_ON": "POWER", "POWER_OFF": "POWER",
    "UP": "NAVIGATION", "DOWN": "NAVIGATION", "LEFT": "NAVIGATION", "RIGHT": "NAVIGATION",
    "OK": "NAVIGATION", "BACK": "NAVIGATION", "HOME": "NAVIGATION", "MENU": "NAVIGATION",
    "EXIT": "NAVIGATION", "INFO": "NAVIGATION", "INPUT": "NAVIGATION",
    "MUTE": "VOLUME", "VOL_UP": "VOLUME", "VOL_DOWN": "VOLUME",
    "CH_UP": "CHANNEL", "CH_DOWN": "CHANNEL",
    "PLAY": "MEDIA", "PAUSE": "MEDIA", "STOP": "MEDIA", "NEXT": "MEDIA",
    "PREVIOUS": "MEDIA", "REWIND": "MEDIA", "FORWARD": "MEDIA", "RECORD": "MEDIA",
    "NETFLIX": "APPS", "YOUTUBE": "APPS",
    "TEMP_UP": "CLIMATE", "TEMP_DOWN": "CLIMATE", "MODE": "CLIMATE", "FAN": "CLIMATE",
    "SWING": "CLIMATE", "TIMER": "CLIMATE", "TURBO": "CLIMATE", "SLEEP": "CLIMATE",
    "CUSTOM": "CUSTOM",
}

# (patrón regex, etiqueta ES, rol). El orden importa: la primera coincidencia gana.
BUTTON_RULES = [
    (r"^(power|pwr|pow)([\s_-]*(on|toggle))?$", "Encendido", "POWER"),
    (r"^(power[\s_-]*off|pwr[\s_-]*off|standby)$", "Apagar", "POWER_OFF"),
    (r"^(power[\s_-]*on)$", "Encender", "POWER_ON"),
    (r"^(vol[\s_.-]*(up|\+|plus)|volume[\s_.-]*(up|\+)|vup|vol\+)$", "Volumen +", "VOL_UP"),
    (r"^(vol[\s_.-]*(dn|down|-|minus)|volume[\s_.-]*(dn|down|-)|vdown|vol-)$", "Volumen −", "VOL_DOWN"),
    (r"^(mute|mut|muting|sound[\s_-]*off)$", "Silencio", "MUTE"),
    (r"^(ch[\s_.-]*(up|\+|next|plus)|channel[\s_.-]*(up|\+|next)|ch\+)$", "Canal +", "CH_UP"),
    (r"^(ch[\s_.-]*(dn|down|-|prev|minus)|channel[\s_.-]*(down|prev)|ch-)$", "Canal −", "CH_DOWN"),
    (r"^(up|arrow[\s_-]*up|nav[\s_-]*up|cursor[\s_-]*up)$", "Arriba", "UP"),
    (r"^(down|arrow[\s_-]*down|nav[\s_-]*down|cursor[\s_-]*down)$", "Abajo", "DOWN"),
    (r"^(left|arrow[\s_-]*left|nav[\s_-]*left|cursor[\s_-]*left)$", "Izquierda", "LEFT"),
    (r"^(right|arrow[\s_-]*right|nav[\s_-]*right|cursor[\s_-]*right)$", "Derecha", "RIGHT"),
    (r"^(ok|enter|select|confirm|okay)$", "OK", "OK"),
    (r"^(back|return|previous[\s_-]*menu)$", "Atrás", "BACK"),
    (r"^(home|smart[\s_-]*hub|inicio)$", "Inicio", "HOME"),
    (r"^(menu|setup|settings|ajustes|tools)$", "Menú", "MENU"),
    (r"^(exit|salir|clear|escape)$", "Salir", "EXIT"),
    (r"^(info|display|status|i)$", "Info", "INFO"),
    (r"^(input|source|tv[\s_/]*(av|video)|av|ext)$", "Fuente", "INPUT"),
    (r"^(play|playpause|play[\s_-]*pause|pause[\s_-]*play)$", "Reproducir", "PLAY"),
    (r"^(pause|freeze)$", "Pausa", "PAUSE"),
    (r"^(stop)$", "Detener", "STOP"),
    (r"^(next|skip[\s_-]*forward|track[\s_-]*up|fwd[\s_-]*skip)$", "Siguiente", "NEXT"),
    (r"^(prev|previous|skip[\s_-]*back|track[\s_-]*down)$", "Anterior", "PREVIOUS"),
    (r"^(rew|rewind|rev|<<)$", "Retroceder", "REWIND"),
    (r"^(ff|fwd|forward|fast[\s_-]*forward|>>)$", "Avanzar", "FORWARD"),
    (r"^(rec|record)$", "Grabar", "RECORD"),
    (r"^(netflix)$", "Netflix", "NETFLIX"),
    (r"^(youtube|you[\s_-]*tube)$", "YouTube", "YOUTUBE"),
    (r"^(temp[\s_.-]*(up|\+|plus)|t[\s_.-]*up)$", "Temperatura +", "TEMP_UP"),
    (r"^(temp[\s_.-]*(down|dn|-|minus)|t[\s_.-]*down)$", "Temperatura −", "TEMP_DOWN"),
    (r"^(mode|auto|modo)$", "Modo", "MODE"),
    (r"^(fan|fan[\s_-]*speed|wind|speed)$", "Ventilador", "FAN"),
    (r"^(swing|swing[\s_-]*(v|h)|louver)$", "Oscilación", "SWING"),
    (r"^(timer|sleep[\s_-]*timer|time)$", "Temporizador", "TIMER"),
    (r"^(turbo|powerful|boost)$", "Turbo", "TURBO"),
    (r"^(sleep|quiet|eco|night)$", "Sleep", "SLEEP"),
    (r"^([0-9])$", None, "DIGIT"),
]

DIGIT_LABELS = {str(d): f"D{i}" for i, d in enumerate("0123456789")}


def map_button(name: str):
    """Devuelve (etiqueta, rol) para un nombre de botón de IRDB, o (nombre limpio, CUSTOM)."""
    raw = name.strip()
    low = raw.lower().strip()
    for pattern, label, role in BUTTON_RULES:
        if re.match(pattern, low):
            if role == "DIGIT":
                digit = int(low)
                return str(digit), f"DIGIT_{digit}"
            return label, role
    # Nombres tipo "temp_16" / "16C" de aires acondicionados.
    m = re.match(r"^(?:temp[\s_.-]*)?(-?\d{1,2})\s*(c|°c|f)?$", low)
    if m and 16 <= int(m.group(1)) <= 32:
        return f"{m.group(1)} °C", "CUSTOM"
    clean = re.sub(r"[_\s]+", " ", raw).strip()
    clean = clean[:1].upper() + clean[1:] if clean else raw
    return clean, "CUSTOM"


def default_frequency(protocol: str) -> int:
    return {
        "SONY_SIRC": 40_000,
        "RC5": 36_000,
        "KASEIKYO": 37_000,
        "PIONEER": 40_000,
    }.get(protocol, 38_000)


def hex_bytes(value: str) -> list[int]:
    return [int(tok, 16) for tok in value.split()] or [0]


# --------------------------------------------------------------------------------------
# Lectura de un archivo .ir de Flipper-IRDB
# --------------------------------------------------------------------------------------
class Signal:
    __slots__ = ("name", "kind", "protocol", "address", "command", "frequency", "data")

    def __init__(self):
        self.name = ""
        self.kind = "parsed"
        self.protocol = ""
        self.address = [0, 0, 0, 0]
        self.command = [0, 0, 0, 0]
        self.frequency = 38_000
        self.data = []


def parse_ir_file(path: str) -> list[Signal]:
    signals: list[Signal] = []
    current = Signal()
    for line in open(path, "r", encoding="utf-8", errors="ignore"):
        line = line.rstrip("\n")
        if line.startswith("#"):
            if current.name or current.protocol or current.data:
                # un comentario cierra la señal solo si ya había datos; no hace nada si no
                pass
            continue
        if not line.strip():
            continue
        key, _, value = line.partition(":")
        key = key.strip()
        value = value.strip()
        if key == "name":
            if current.name:
                signals.append(current)
                current = Signal()
            current.name = value
        elif key == "type":
            current.kind = value
        elif key == "protocol":
            current.protocol = value
        elif key == "address":
            try:
                current.address = (hex_bytes(value) + [0, 0, 0, 0])[:4]
            except ValueError:
                pass
        elif key == "command":
            try:
                current.command = (hex_bytes(value) + [0, 0, 0, 0])[:4]
            except ValueError:
                pass
        elif key == "frequency":
            try:
                current.frequency = int(float(value))
            except ValueError:
                pass
        elif key == "data":
            try:
                current.data = [int(tok) for tok in value.split()]
            except ValueError:
                current.data = []
    if current.name:
        signals.append(current)
    return signals


# --------------------------------------------------------------------------------------
# Conversión de una señal a la fila de botón de la app
# --------------------------------------------------------------------------------------
def to_button_row(sig: Signal) -> tuple[str, int, int, int, int, int, int, str] | None:
    """Devuelve (protocolo, freq, address, command, bits, toggle, repeats, raw) o None."""
    proto = sig.protocol.strip()
    a, c = sig.address, sig.command

    if sig.kind == "raw" or proto == "":
        if not sig.data or len(sig.data) < 4 or len(sig.data) > MAX_RAW_SAMPLES:
            return None
        freq = sig.frequency or 38_000
        return ("RAW", freq, 0, 0, 12, 0, 1, ",".join(str(v) for v in sig.data))

    if proto == "NEC":
        addr = a[0] | (a[1] << 8)
        if addr > 0xFF:
            return ("NEC_EXTENDED", 38_000, addr, c[0], 12, 0, 1, "")
        return ("NEC", 38_000, addr, c[0], 12, 0, 1, "")
    if proto == "NECext":
        return ("NEC_EXTENDED", 38_000, a[0] | (a[1] << 8), c[0], 12, 0, 1, "")
    if proto == "Samsung32":
        return ("SAMSUNG", 38_000, a[0], c[0], 12, 0, 1, "")
    if proto == "SIRC":
        return ("SONY_SIRC", 40_000, a[0], c[0], 12, 0, 2, "")
    if proto == "SIRC15":
        return ("SONY_SIRC", 40_000, a[0], c[0], 15, 0, 2, "")
    if proto == "SIRC20":
        return ("SONY_SIRC", 40_000, a[0] | (a[1] << 8), c[0], 20, 0, 2, "")
    if proto == "RC5":
        return ("RC5", 36_000, a[0] & 0x1F, c[0] & 0x3F, 12, 0, 1, "")
    if proto == "Kaseikyo":
        addr = a[0] | (a[1] << 8) | (a[2] << 16) | (a[3] << 24)
        cmd = (c[0] | (c[1] << 8)) & 0x3FF
        return ("KASEIKYO", 37_000, addr, cmd, 12, 0, 1, "")
    if proto == "Pioneer":
        return ("PIONEER", 40_000, a[0], c[0], 12, 0, 2, "")
    return None


# --------------------------------------------------------------------------------------
# Descubrimiento de dispositivos
# --------------------------------------------------------------------------------------
class Device:
    __slots__ = ("key", "name", "brand", "model", "type", "frequency", "description",
                 "source", "buttons", "priority")

    def __init__(self, key, name, brand, model, dtype, frequency, description, buttons, priority=0):
        self.key = key
        self.name = name
        self.brand = brand
        self.model = model
        self.type = dtype
        self.frequency = frequency
        self.description = description
        self.source = SOURCE
        self.buttons = buttons
        self.priority = priority

    def fingerprint(self):
        return tuple(sorted(b[3] for b in self.buttons))


def slug(text: str) -> str:
    out = re.sub(r"[^a-z0-9]+", "_", text.lower()).strip("_")
    return out or "dev"


def pretty(text: str) -> str:
    return re.sub(r"[_]+", " ", text).strip()


def build_device(row_dir, file_path, brand, category_type, key_seed) -> Device | None:
    signals = parse_ir_file(file_path)
    if not signals:
        return None

    base = os.path.basename(file_path)[:-3]
    model = pretty(base)
    if not re.search(r"[A-Za-z]", model):
        # Restos de la conversión irdb.tk ("0,-1", "128,0"...): se usa el nombre del mando
        model = "Genérico"
    if model.lower().startswith(brand.lower()):
        model = pretty(model[len(brand):]).lstrip(" -_") or pretty(base)

    buttons = []
    seen_labels = set()
    for sig in signals:
        if len(buttons) >= MAX_BUTTONS_PER_DEVICE:
            break
        row = to_button_row(sig)
        if row is None:
            continue
        label, role = map_button(sig.name)
        if label in seen_labels:
            continue
        seen_labels.add(label)
        section = ROLE_SECTION.get(role, "DIGITS" if role.startswith("DIGIT_") else "CUSTOM")
        # (label, role, section, protocol, freq, address, command, bits, toggle, repeats, raw)
        buttons.append((label, role, section) + row)

    if not buttons:
        return None

    # Los aires acondicionados con demasiadas tramas RAW se recortan a lo esencial.
    if category_type == "AC" and len(buttons) > AC_BUTTON_LIMIT:
        buttons = buttons[:AC_BUTTON_LIMIT]

    freq = buttons[0][4]
    desc = f"{pretty(brand)} {model} · {len(buttons)} botones · datos abiertos Flipper-IRDB"
    return Device(
        key=f"{slug(key_seed)}",
        name=f"{pretty(brand)} {model}".strip(),
        brand=pretty(brand),
        model=model,
        dtype=category_type,
        frequency=freq,
        description=desc,
        buttons=buttons,
    )


def collect(irdb_root: str) -> list[Device]:
    devices: list[Device] = []
    used_keys: set[str] = set()
    seen_fingerprints: set[tuple] = set()

    def add(dev: Device | None):
        if dev is None:
            return
        fingerprint = (dev.brand.lower(), dev.type, dev.fingerprint())
        if fingerprint in seen_fingerprints:
            return
        seen_fingerprints.add(fingerprint)
        key = dev.key
        n = 2
        while key in used_keys:
            key = f"{dev.key}_{n}"
            n += 1
        dev.key = key
        used_keys.add(key)
        devices.append(dev)

    # 1) Carpetas principales de IRDB (curadas por la comunidad)
    for category in sorted(os.listdir(irdb_root)):
        cat_path = os.path.join(irdb_root, category)
        if not os.path.isdir(cat_path) or category.startswith("_") or category.startswith("."):
            continue
        dtype = CATEGORY_TYPE.get(category)
        if dtype is None:
            continue
        for brand in sorted(os.listdir(cat_path)):
            brand_path = os.path.join(cat_path, brand)
            files = []
            if os.path.isdir(brand_path):
                for name in sorted(os.listdir(brand_path)):
                    if name.endswith(".ir"):
                        files.append(os.path.join(brand_path, name))
                    elif os.path.isdir(os.path.join(brand_path, name)):
                        for inner in sorted(os.listdir(os.path.join(brand_path, name))):
                            if inner.endswith(".ir"):
                                files.append(os.path.join(brand_path, name, inner))
            if not files:
                continue
            candidates = []
            for path in files:
                dev = build_device(cat_path, path, brand, dtype, f"{category[:3]}_{brand}_{os.path.basename(path)[:-3]}")
                if dev:
                    candidates.append(dev)
            candidates.sort(key=lambda d: (-len(d.buttons), d.model))
            for dev in candidates[: BRAND_LIMIT.get(dtype, 1)]:
                add(dev)

    # 2) _Converted_/CSV (volcado de irdb.tk): aporta marcas que no están en las carpetas
    csv_root = os.path.join(irdb_root, "_Converted_", "CSV")
    if os.path.isdir(csv_root):
        for letter in sorted(os.listdir(csv_root)):
            letter_path = os.path.join(csv_root, letter)
            if not os.path.isdir(letter_path):
                continue
            for brand in sorted(os.listdir(letter_path)):
                brand_path = os.path.join(letter_path, brand)
                if not os.path.isdir(brand_path):
                    continue
                for model_dir in sorted(os.listdir(brand_path)):
                    model_path = os.path.join(brand_path, model_dir)
                    if not os.path.isdir(model_path):
                        continue
                    dtype = infer_type_from_name(model_dir) or infer_type_from_name(brand)
                    if dtype is None:
                        continue
                    for name in sorted(os.listdir(model_path)):
                        if not name.endswith(".ir"):
                            continue
                        dev = build_device(
                            model_path,
                            os.path.join(model_path, name),
                            brand,
                            dtype,
                            f"cv_{brand}_{model_dir}_{name[:-3]}",
                        )
                        if dev:
                            dev.name = f"{pretty(brand)} {pretty(model_dir)}".strip()
                            add(dev)
    return devices


TYPE_HINTS = [
    ("tv", "TV"), ("television", "TV"), ("dvd", "DVD"), ("blu", "DVD"),
    ("ac", "AC"), ("air", "AC"), ("conditioner", "AC"), ("projector", "PROJECTOR"),
    ("soundbar", "AUDIO"), ("receiver", "AUDIO"), ("fan", "FAN"), ("led", "LIGHT"),
]


def infer_type_from_name(text: str) -> str | None:
    low = text.lower()
    for hint, dtype in TYPE_HINTS:
        if hint in low:
            return dtype
    return None


# --------------------------------------------------------------------------------------
# Salida
# --------------------------------------------------------------------------------------
def write_asset(devices: list[Device], out_path: str) -> tuple[int, int, int]:
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    n_dev = n_btn = 0
    with gzip.open(out_path, "wt", encoding="utf-8", compresslevel=9) as fh:
        fh.write(FORMAT_VERSION + "\n")
        for dev in devices:
            fh.write("\t".join([
                "D", dev.key, dev.name, dev.brand, dev.model, dev.type,
                str(dev.frequency), dev.description, dev.source,
            ]) + "\n")
            n_dev += 1
            for (label, role, section, proto, freq, address, command, bits, toggle, repeats, raw) in dev.buttons:
                fh.write("\t".join([
                    "B", dev.key, label, role, section, proto, str(freq), str(address),
                    str(command), str(bits), "1" if toggle else "0", str(repeats), raw,
                ]) + "\n")
                n_btn += 1
    return n_dev, n_btn, os.path.getsize(out_path)


def main() -> int:
    ap = argparse.ArgumentParser(description="Genera el catálogo extendido desde Flipper-IRDB")
    ap.add_argument("--irdb", required=True, help="Ruta local del repositorio Flipper-IRDB")
    ap.add_argument(
        "--out",
        default=os.path.join(
            os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))),
            "app", "src", "main", "assets", "catalog", "manu_catalog.tsv.gz",
        ),
    )
    ap.add_argument("--report", default=None, help="Ruta del informe de marcas/modelos (TSV)")
    args = ap.parse_args()

    if not os.path.isdir(args.irdb):
        print(f"No existe la ruta IRDB: {args.irdb}", file=sys.stderr)
        return 2

    devices = collect(args.irdb)

    # Perfiles de las marcas prioritarias (Haitech, ATEC, Panda, HELECT).
    sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
    try:
        from priority_profiles import build_priority_devices  # type: ignore
        priority = build_priority_devices(devices)
    except ImportError:
        priority = []
    if priority:
        existing = {d.key for d in devices}
        for dev in priority:
            if dev.key not in existing:
                devices.insert(0, dev)
                existing.add(dev.key)

    devices.sort(key=lambda d: (d.brand.lower(), d.type, d.name.lower()))
    n_dev, n_btn, size = write_asset(devices, args.out)

    brands = OrderedDict()
    for dev in devices:
        brands.setdefault((dev.brand, dev.type), []).append(dev)
    print(f"Dispositivos: {n_dev} | Botones: {n_btn} | Asset: {size/1024/1024:.2f} MiB -> {args.out}")
    print(f"Marcas: {len({b for b, _ in brands})} | Combinaciones marca/tipo: {len(brands)}")

    if args.report:
        os.makedirs(os.path.dirname(args.report), exist_ok=True)
        with open(args.report, "w", encoding="utf-8") as fh:
            fh.write("marca\ttipo\tmodelos\tbotones\n")
            for (brand, dtype), devs in sorted(brands.items(), key=lambda kv: (kv[0][0].lower(), kv[0][1])):
                fh.write("\t".join([
                    brand, dtype, "; ".join(d.model for d in devs), str(sum(len(d.buttons) for d in devs)),
                ]) + "\n")
        print(f"Informe: {args.report}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

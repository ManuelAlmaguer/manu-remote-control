#!/usr/bin/env python3
"""
Perfiles de compatibilidad para marcas sin datos públicos en Flipper-IRDB.

Las marcas Haitech, ATEC, Panda y HELECT no publican códigos IR y no aparecen en
ninguna base de datos libre consultada (Flipper-IRDB/irdb.tk, LIRC, SmartIR).

Como en cualquier mando universal, para estas marcas se entregan varios "juegos de
códigos candidatos": cada juego copia el mando real de un televisor/split chino que
usa la misma placa (ODM) que suele ir dentro de estos equipos. El usuario prueba del
1 al N hasta que su equipo responde.

Todo se marca explícitamente como CANDIDATO en el nombre y la descripción del
dispositivo, para no confundirlo con los códigos verificados de IRDB.
"""

from __future__ import annotations

import re

# Marcas que comparten placa con los equipos vendidos bajo las marcas solicitadas.
TV_ODM_BRANDS = ["Changhong", "Konka", "Skyworth", "Haier", "TCL", "AOC", "Sanyo", "Rowa"]
AC_ODM_BRANDS = ["Gree", "Midea", "Chigo", "Kelon", "TCL", "Haier", "Hisense"]

REQUESTED = [
    # (marca, tipos, notas para la descripción)
    ("Haitech", ("TV", "AC"),
     "Marca china con más de 20 años en Cuba (TV LED y splits; placas chinas tipo TP.MS/V56)"),
    ("ATEC", ("TV",),
     "Marca de TV LED comercializada en Latinoamérica sobre placa china genérica"),
    ("Panda", ("TV", "DVD"),
     "Panda Electronics (China): TV LED y reproductores; el DVD sí tiene datos reales en IRDB"),
    ("HELECT", ("TV",),
     "Marca de TV LED de importación (placa china genérica)"),
]


def _candidates(devices, brands, dtype, limit, offset=0):
    """Selecciona mandos base distintos para cada marca (rotación del grupo de ODM)."""
    pool = [d for d in devices if d.brand in brands and d.type == dtype and len(d.buttons) >= 12]
    pool.sort(key=lambda d: (d.brand, -len(d.buttons), d.name))
    if pool and offset:
        pool = pool[offset % len(pool):] + pool[: offset % len(pool)]
    picked = []
    seen = set()
    for dev in pool:
        if dev.brand in seen:
            continue
        if dev.fingerprint() in {p.fingerprint() for p in picked}:
            continue
        picked.append(dev)
        seen.add(dev.brand)
        if len(picked) >= limit:
            break
    return picked


def build_priority_devices(devices):
    """Crea los dispositivos de las marcas solicitadas a partir de los mandos ya cargados."""
    from build_catalog import Device  # import local para evitar dependencias circulares

    priority = []
    letters = "ABCDEF"

    for brand_index, (brand, types, note) in enumerate(REQUESTED):
        for dtype in types:
            source_brands = TV_ODM_BRANDS if dtype == "TV" else AC_ODM_BRANDS
            cands = _candidates(devices, source_brands, dtype, 4, offset=brand_index * 2)
            for index, base in enumerate(cands):
                letter = letters[index]
                key = f"prio_{re.sub(r'[^a-z0-9]+', '_', brand.lower())}_{dtype.lower()}_{letter.lower()}"
                buttons = list(base.buttons)
                priority.append(
                    Device(
                        key=key,
                        name=f"{brand} {dtype} · candidato {letter}",
                        brand=brand,
                        model=f"Candidato {letter} (base {base.brand} {base.model})",
                        dtype=dtype,
                        frequency=base.frequency,
                        description=(
                            f"Perfil candidato {letter}/{len(cands)} para {brand} {dtype}. {note}. "
                            f"Juego de códigos copiado del mando real {base.brand} {base.model} "
                            f"(misma familia de placa). No verificado con equipo físico: "
                            f"prueba los candidatos en orden hasta que el equipo responda."
                        ),
                        buttons=buttons,
                    )
                )
    return priority

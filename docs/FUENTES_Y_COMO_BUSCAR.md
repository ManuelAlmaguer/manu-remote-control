# Fuentes, método y cómo seguir ampliando el catálogo

Documento de trazabilidad de los códigos IR añadidos en la versión 1.0.2 de Manu Remote Control.

## 1. Qué se añadió

| Concepto | Antes (1.0.1) | Ahora (1.0.2) |
|---|---|---|
| Marcas | 7 (Samsung, LG, Sony, Philips, TCL, Toshiba, Hisense, Daikin, Gree) | **952** |
| Mandos (marca+modelo) | 13 | **1.890** |
| Códigos IR | ~250 | **43.125** |
| Protocolos del motor | NEC, NECext, Samsung32, SIRC12/15/20, RC-5, RAW | + **Kaseikyo/Panasonic 48** y **Pioneer 33** |
| Mandos universales | — | Sección nueva con **KT1440** (manual + 22 códigos) |

## 2. De dónde salieron los códigos

### 2.1 Fuente principal: Flipper-IRDB (CC0 1.0)

- Repo: <https://github.com/Lucaslhm/Flipper-IRDB> (8.901 archivos `.ir`, 170.473 señales decodificadas y 131.442 tramas RAW).
- Licencia **CC0 1.0** (dominio público), por eso se puede redistribuir dentro del APK.
- Cómo se usó:
  ```bash
  git clone --depth 1 https://github.com/Lucaslhm/Flipper-IRDB.git /tmp/irdb
  python3 tools/ir_catalog/build_catalog.py --irdb /tmp/irdb --report docs/marcas_modelos.tsv
  ```
- Cada archivo `.ir` describe un mando real: `protocol` + `address` + `command` para señales decodificadas, o `data` con la trama RAW en microsegundos.
- Los aires acondicionados se guardan como RAW porque envían el estado completo (modo, temperatura, ventilador) en cada pulsación.

### 2.2 Verificación de los protocolos nuevos

Los tiempos de Kaseikyo y Pioneer **no se copiaron de memoria**: se leyeron del firmware oficial de Flipper Zero.

```bash
curl -sSL https://raw.githubusercontent.com/flipperdevices/flipperzero-firmware/dev/\
lib/infrared/encoder_decoder/kaseikyo/infrared_encoder_kaseikyo.c
curl -sSL https://raw.githubusercontent.com/flipperdevices/flipperzero-firmware/dev/\
lib/infrared/encoder_decoder/pioneer/infrared_protocol_pioneer_i.h
```

De ahí salen: unidad 432 µs y cabecera 3456/1728 µs (Kaseikyo, 37 kHz, 48 bits con paridad de vendor),
y cabecera 8500/4225 µs con bits 500/500 y 500/1500 µs (Pioneer, 40 kHz, 33 bits), además del
empaquetado exacto de bytes y paridades.

### 2.3 Las 4 marcas solicitadas: Haitech, ATEC, Panda, HELECT

Búsquedas realizadas y resultado:

| Marca | Fuente consultada | Resultado |
|---|---|---|
| Haitech | IRDB, irdb.tk, LIRC, GitHub code search | Sin códigos. Es una empresa china con 20+ años en Cuba (TV y splits; tienda Mallhaitech con COPEXTEL). Usa placas chinas genéricas. |
| ATEC | IRDB, irdb.tk, LIRC | Sin códigos. Marca de TV LED vendida en Latinoamérica sobre placa china. |
| Panda | IRDB/irdb.tk `_Converted_/CSV/P/Panda/Unknown_DVD-6838` | **Sí hay datos reales** (DVD, NECext) → incluido tal cual. El resto de modelos se cubren con perfiles candidato. |
| HELECT | IRDB, irdb.tk, LIRC, GitHub | Sin códigos. Marca de importación con placa genérica. |

Cómo se resolvió: perfiles **candidato A–D** por marca, copiando mandos reales de equipos de la misma
familia de placa (Haier, Sanyo, AOC, TCL, Konka, Changhong, Skyworth, Gree, Midea, Chigo, Kelon).
Están marcados como «candidato» en el nombre y en la descripción del dispositivo dentro de la app.

### 2.4 KT1440 (mando universal)

- Lista de códigos y manual: <https://radio.cantidubi.com/manual-mando-gssc-kt1440>
- Manual del fabricante (WINFLIKE KT1440 / WL-RC280): <https://manuals.plus/asin/B08NDPX1MT> y <https://manuals.plus/asin/B08VN2W7L7>
- Listado ampliado (Scribd): <https://es.scribd.com/document/957030599/Control-Remoto-Kt1440>
- Contenido incluido en `app/src/main/assets/catalog/universal_remotes.json`: 4 bloques de manual
  (programar TV, programar caja/DVD, búsqueda manual, teclas útiles) y los 22 códigos (TV 001-010,
  cajas 101-109, DVD 201-202).

## 3. Qué se descartó (y por qué)

Del total de IRDB se quedaron fuera ~1.500 señales:

| Protocolo IRDB | Señales | Motivo |
|---|---|---|
| RC6 | 908 | No implementado aún (Manchester con bit de alternancia de doble duración) |
| RCA | 279 | No implementado aún |
| RC5X | 212 | Variante de 7 bits de RC-5, pendiente |
| NEC42 | 124 | No implementado aún |

El resto del volumen descartado son duplicados exactos (el mismo mando convertido varias veces en
`_Converted_/CSV`) y el recorte por marca (máximo 2-3 modelos por marca y tipo) para que el primer
arranque siga siendo rápido.

## 4. Dónde más puedes buscar (y cómo)

### Bases de datos de códigos IR

| Fuente | Qué aporta | Cómo se usa |
|---|---|---|
| **Flipper-IRDB** <https://github.com/Lucaslhm/Flipper-IRDB> | La mayor colección libre (CC0): `protocol/address/command` o RAW | Ya integrada; el script la consume directamente |
| **irdb.tk (InfraredDB)** <https://github.com/probonopd/irdb> | Base histórica con miles de mandos en CSV (`functionname,protocol,device,subdevice,function`) | Se puede añadir un lector CSV al script |
| **LIRC remotes** <https://github.com/lirc/lirc-remotes> | Mandos de Linux, con `lircd.conf` (RAW y protocolos) | Parsear `begin codes` / `begin raw_codes` |
| **SmartIR** <https://github.com/smartHomeHub/SmartIR> | Aires acondicionados y TVs en JSON (Home Assistant), por marca y modelo | Ideal para climas: los `commands` son RAW listos |
| **irplus** <https://github.com/irplus-remote/irplus-remote.github.io> | Miles de mandos en JSON (`irplus` de Android) | Estructura `codes[]` con `protocol`/`code` |
| **IRremoteESP8266** <https://github.com/crankyoldgit/IRremoteESP8266> | Protocolos de aire acondicionado implementados en C++ (Gree, Midea, Daikin…), listos para portar al motor | Fuente de referencia para nuevos protocolos |
| **RemoteCentral** <https://www.remotecentral.com/cgi-bin/codes/> | Pronto HEX profesional (AV, proyectores, iluminación) | Convertir Pronto a RAW (el decoder Pronto son ~40 líneas) |
| **Universal Remote Code lists** (One For All, GE, RCA, eliztech, remotecodelist) | Tablas de códigos numéricos por marca | Solo sirven como índice de marcas/modelos, no dan IR |

### Cómo buscar una marca concreta que no aparece

1. Buscar el **modelo del mando original** (no el de la TV) en Google: `"<marca> remote code list"` o
   `"<modelo del mando> manual codes"`. Los folletos de mandos universales traen la tabla por marca.
2. Buscar en GitHub: `gh search code "<marca>" --limit 20` (a veces aparece en repos de Home Assistant,
   Flipper, irplus o proyectos de ESP32).
3. Buscar el **chasis/placa** del equipo (`TP.MS358`, `CV338H`, `V56`, `LS28`) en vez de la marca: dos
   marcas distintas con la misma placa usan el mismo mando.
4. Probar el mando con la **cámara del teléfono** apuntando al LED IR: si el mando original funciona,
   se puede **capturar** la señal y guardarla como RAW en la app.

### Cómo añadir marcas nuevas al proyecto

```bash
# 1. Actualizar la base de datos
git -C /tmp/irdb pull

# 2. Regenerar el asset (2.000+ mandos en ~6 s)
python3 tools/ir_catalog/build_catalog.py --irdb /tmp/irdb --report docs/marcas_modelos.tsv

# 3. Subir la versión para que los móviles que ya tienen la app reimporten el catálogo
#    app/src/main/java/com/manu/remotecontrol/data/local/seed/CatalogAssetLoader.kt -> AssetCatalog.VERSION

# 4. Compilar
./gradlew assembleRelease
```

Para **añadir un protocolo** (RC6, RCA, RC5X, NEC42…): crear la clase en `domain/ir/protocols/`,
registrarla en `IrEncoders`, añadir su entrada en `IrProtocolId`, `IrCode`, `Mappers` y
`IrCodeCodec` (el compilador marca todos los `when` que falten). Después, en
`tools/ir_catalog/build_catalog.py`, mapear el nombre del protocolo dentro de `to_button_row()`.

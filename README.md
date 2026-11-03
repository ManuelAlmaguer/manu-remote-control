# Manu Remote Control

App Android nativa de control remoto por infrarrojos (Kotlin + Jetpack Compose + Room + Hilt).

- **Todo offline**: el catálogo de mandos viaja dentro del APK, no se descarga nada.
- **Motor IR extensible**: NEC, NEC extendido, Samsung 32, Sony SIRC 12/15/20, Philips RC-5,
  Kaseikyo/Panasonic 48 y Pioneer 33, además de RAW en microsegundos.
- **Catálogo de 952 marcas y 1.890 mandos** (43.125 códigos) generado desde Flipper-IRDB (CC0 1.0).
- **Editor de mandos**: crear botones propios, cambiar etiqueta, rol, sección y código IR.
- **Sección de mandos universales**: manual de programación y tabla completa de códigos (KT1440).

## Estructura

```
app/src/main/java/com/manu/remotecontrol/
  data/local/seed/          Catálogo semilla en Kotlin + importador del asset
  data/local/universal/     Mandos universales físicos (KT1440…)
  domain/ir/                Motor de protocolos y codificadores
  presentation/             Pantallas Compose (home, remoto, editor, ajustes, universales)
tools/ir_catalog/           Generador del catálogo desde Flipper-IRDB
docs/                       Marcas implementadas y fuentes de datos
```

## Compilar

Requiere JDK 17+ y el Android SDK (platform 35 y build-tools 35).

```bash
echo "sdk.dir=/ruta/al/android-sdk" > local.properties
./gradlew assembleRelease        # APK firmado en app/build/outputs/apk/release/
./gradlew testReleaseUnitTest    # tests del motor IR
```

## Regenerar el catálogo

```bash
git clone --depth 1 https://github.com/Lucaslhm/Flipper-IRDB.git /tmp/irdb
python3 tools/ir_catalog/build_catalog.py --irdb /tmp/irdb --report docs/marcas_modelos.tsv
```

Después sube `AssetCatalog.VERSION` en `CatalogAssetLoader.kt` para que los móviles que ya tienen la
app reimporten el catálogo actualizado.

## Documentación

- [`docs/MARCAS_Y_MODELOS.md`](docs/MARCAS_Y_MODELOS.md) — todas las marcas y modelos incluidos.
- [`docs/FUENTES_Y_COMO_BUSCAR.md`](docs/FUENTES_Y_COMO_BUSCAR.md) — de dónde salió cada código, qué
  se descartó y dónde más se puede buscar.

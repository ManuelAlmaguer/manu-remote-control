package com.manu.remotecontrol.data.local.seed

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.DeviceType

/**
 * Semilla de televisores (Samsung, LG, Sony, Philips, TCL, Toshiba y Hisense).
 *
 * Los códigos provienen de la base de datos abierta Flipper-IRDB (licencia CC0 1.0) y se
 * han transcrito respetando protocolo, dirección y comando originales. Los mandos de aire
 * acondicionado se guardan como patrones RAW porque envían el estado completo en cada trama.
 */
internal object SeedTv {

    val devices: List<SeedDevice> = listOf(
            SeedDevice(
                key = "tv_samsung",
                name = "Samsung TV",
                brand = "Samsung",
                model = "Serie TU / genérica",
                type = DeviceType.TV,
                frequencyHz = 38000,
                description = "TELEVISOR LED de referencia de la base IRDB (protocolo Samsung 32).",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Samsung(address = 0x07, command = 0x02)),
                    btn("Fuente", ButtonRole.INPUT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x01)),
                    btn("Menú", ButtonRole.MENU, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x1A)),
                    btn("Atrás", ButtonRole.BACK, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x58)),
                    btn("Salir", ButtonRole.EXIT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x2D)),
                    btn("Info", ButtonRole.INFO, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x1F)),
                    btn("Smart Hub", ButtonRole.HOME, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x79)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x60)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x61)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x65)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x62)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x07, command = 0x68)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Samsung(address = 0x07, command = 0x07)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Samsung(address = 0x07, command = 0x0B)),
                    btn("Silencio", ButtonRole.MUTE, ButtonSection.VOLUME, IrCode.Samsung(address = 0x07, command = 0x0F)),
                    btn("Canal +", ButtonRole.CH_UP, ButtonSection.CHANNEL, IrCode.Samsung(address = 0x07, command = 0x10)),
                    btn("Canal −", ButtonRole.CH_DOWN, ButtonSection.CHANNEL, IrCode.Samsung(address = 0x07, command = 0x12)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x11)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x04)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x05)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x06)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x08)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x09)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x0A)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x0C)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x0D)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Samsung(address = 0x07, command = 0x0E))
                )
            ),

            SeedDevice(
                key = "tv_lg",
                name = "LG TV",
                brand = "LG",
                model = "Serie LJ / UK / genérica",
                type = DeviceType.TV,
                frequencyHz = 38000,
                description = "Códigos NEC del mando genérico de LG, incluye teclado numérico.",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Nec(address = 0x04, command = 0x08)),
                    btn("Silencio", ButtonRole.MUTE, ButtonSection.VOLUME, IrCode.Nec(address = 0x04, command = 0x09)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Nec(address = 0x04, command = 0x02)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Nec(address = 0x04, command = 0x03)),
                    btn("Canal +", ButtonRole.CH_UP, ButtonSection.CHANNEL, IrCode.Nec(address = 0x04, command = 0x00)),
                    btn("Canal −", ButtonRole.CH_DOWN, ButtonSection.CHANNEL, IrCode.Nec(address = 0x04, command = 0x01)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x40)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x41)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x07)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x06)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x44)),
                    btn("Atrás", ButtonRole.BACK, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x28)),
                    btn("Ajustes", ButtonRole.MENU, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x43)),
                    btn("Entrada", ButtonRole.INPUT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x0B)),
                    btn("Inicio", ButtonRole.HOME, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x04, command = 0x3B)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x10)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x11)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x12)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x13)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x14)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x15)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x16)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x17)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x18)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Nec(address = 0x04, command = 0x19))
                )
            ),

            SeedDevice(
                key = "tv_sony",
                name = "Sony Bravia",
                brand = "Sony",
                model = "Bravia / SIRC",
                type = DeviceType.TV,
                frequencyHz = 40000,
                description = "Sony SIRC de 12 bits (dirección 0x01) con Netflix en SIRC15.",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Sony(device = 0x01, command = 0x15, bits = 12)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x74, bits = 12)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x75, bits = 12)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x34, bits = 12)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x33, bits = 12)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x65, bits = 12)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Sony(device = 0x01, command = 0x12, bits = 12)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Sony(device = 0x01, command = 0x13, bits = 12)),
                    btn("Silencio", ButtonRole.MUTE, ButtonSection.VOLUME, IrCode.Sony(device = 0x01, command = 0x14, bits = 12)),
                    btn("Inicio", ButtonRole.HOME, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x60, bits = 12)),
                    btn("HDMI", ButtonRole.INPUT, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x01, command = 0x25, bits = 12)),
                    btn("Netflix", ButtonRole.NETFLIX, ButtonSection.APPS, IrCode.Sony(device = 0x1A, command = 0x7C, bits = 15)),
                    btn("Canal +", ButtonRole.CH_UP, ButtonSection.CHANNEL, IrCode.Sony(device = 0x01, command = 0x10, bits = 12)),
                    btn("Canal −", ButtonRole.CH_DOWN, ButtonSection.CHANNEL, IrCode.Sony(device = 0x01, command = 0x11, bits = 12)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x09, bits = 12)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x00, bits = 12)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x01, bits = 12)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x02, bits = 12)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x03, bits = 12)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x04, bits = 12)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x05, bits = 12)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x06, bits = 12)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x07, bits = 12)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Sony(device = 0x01, command = 0x08, bits = 12))
                )
            ),

            SeedDevice(
                key = "tv_philips",
                name = "Philips TV",
                brand = "Philips",
                model = "RC-5 sistema 0",
                type = DeviceType.TV,
                frequencyHz = 36000,
                description = "Philips RC-5 clásico: 36 kHz y codificación Manchester.",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Rc5(system = 0x00, command = 0x0C)),
                    btn("Silencio", ButtonRole.MUTE, ButtonSection.VOLUME, IrCode.Rc5(system = 0x00, command = 0x0D)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Rc5(system = 0x00, command = 0x10)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Rc5(system = 0x00, command = 0x11)),
                    btn("Canal +", ButtonRole.CH_UP, ButtonSection.CHANNEL, IrCode.Rc5(system = 0x00, command = 0x20)),
                    btn("Canal −", ButtonRole.CH_DOWN, ButtonSection.CHANNEL, IrCode.Rc5(system = 0x00, command = 0x21)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Rc5(system = 0x00, command = 0x50)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Rc5(system = 0x00, command = 0x51)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Rc5(system = 0x00, command = 0x55)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Rc5(system = 0x00, command = 0x56)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Rc5(system = 0x00, command = 0x57)),
                    btn("Atrás", ButtonRole.BACK, ButtonSection.NAVIGATION, IrCode.Rc5(system = 0x00, command = 0x0A)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x00)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x01)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x02)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x03)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x04)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x05)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x06)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x07)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x08)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Rc5(system = 0x00, command = 0x09))
                )
            ),

            SeedDevice(
                key = "tv_tcl",
                name = "TCL",
                brand = "TCL",
                model = "32S327 y similares",
                type = DeviceType.TV,
                frequencyHz = 38000,
                description = "NEC extendido (dirección de 16 bits 0xC7EA).",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Nec(address = 0xC7EA, command = 0x17, extended = true)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Nec(address = 0xC7EA, command = 0x0F, extended = true)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Nec(address = 0xC7EA, command = 0x10, extended = true)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x2A, extended = true)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x19, extended = true)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x33, extended = true)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x1E, extended = true)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x2D, extended = true)),
                    btn("Inicio", ButtonRole.HOME, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x03, extended = true)),
                    btn("Salir", ButtonRole.EXIT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xC7EA, command = 0x66, extended = true))
                )
            ),

            SeedDevice(
                key = "tv_toshiba",
                name = "Toshiba",
                brand = "Toshiba",
                model = "CT-90325",
                type = DeviceType.TV,
                frequencyHz = 38000,
                description = "Mando real Toshiba CT-90325, protocolo NEC con dirección 0x40.",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Nec(address = 0x40, command = 0x12)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Nec(address = 0x40, command = 0x1A)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Nec(address = 0x40, command = 0x1E)),
                    btn("Canal +", ButtonRole.CH_UP, ButtonSection.CHANNEL, IrCode.Nec(address = 0x40, command = 0x1B)),
                    btn("Canal −", ButtonRole.CH_DOWN, ButtonSection.CHANNEL, IrCode.Nec(address = 0x40, command = 0x1F)),
                    btn("Entrada", ButtonRole.INPUT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x40, command = 0x0F)),
                    btn("Silencio", ButtonRole.MUTE, ButtonSection.VOLUME, IrCode.Nec(address = 0x40, command = 0x10)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x40, command = 0x89)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x40, command = 0x82)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x40, command = 0x83)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x40, command = 0x1D)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0x40, command = 0x19)),
                    btn("Reproducir", ButtonRole.PLAY, ButtonSection.MEDIA, IrCode.Nec(address = 0x40, command = 0x30)),
                    btn("Detener", ButtonRole.STOP, ButtonSection.MEDIA, IrCode.Nec(address = 0x40, command = 0x31)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x00)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x01)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x02)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x03)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x04)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x05)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x06)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x07)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x08)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Nec(address = 0x40, command = 0x09))
                )
            ),

            SeedDevice(
                key = "tv_hisense",
                name = "Hisense",
                brand = "Hisense",
                model = "EN3AH39H",
                type = DeviceType.TV,
                frequencyHz = 38000,
                description = "Mando Hisense EN3AH39H, NEC extendido con dirección 0xBF00.",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Nec(address = 0xBF00, command = 0x0D, extended = true)),
                    btn("Fuente", ButtonRole.INPUT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x12, extended = true)),
                    btn("Silencio", ButtonRole.MUTE, ButtonSection.VOLUME, IrCode.Nec(address = 0xBF00, command = 0x0E, extended = true)),
                    btn("Volumen +", ButtonRole.VOL_UP, ButtonSection.VOLUME, IrCode.Nec(address = 0xBF00, command = 0x44, extended = true)),
                    btn("Volumen −", ButtonRole.VOL_DOWN, ButtonSection.VOLUME, IrCode.Nec(address = 0xBF00, command = 0x43, extended = true)),
                    btn("Canal +", ButtonRole.CH_UP, ButtonSection.CHANNEL, IrCode.Nec(address = 0xBF00, command = 0x4A, extended = true)),
                    btn("Canal −", ButtonRole.CH_DOWN, ButtonSection.CHANNEL, IrCode.Nec(address = 0xBF00, command = 0x4B, extended = true)),
                    btn("Menú", ButtonRole.MENU, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x4F, extended = true)),
                    btn("Atrás", ButtonRole.BACK, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x48, extended = true)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x15, extended = true)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x16, extended = true)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x17, extended = true)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x19, extended = true)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Nec(address = 0xBF00, command = 0x18, extended = true)),
                    btn("Reproducir", ButtonRole.PLAY, ButtonSection.MEDIA, IrCode.Nec(address = 0xBF00, command = 0x1E, extended = true)),
                    btn("Pausa", ButtonRole.PAUSE, ButtonSection.MEDIA, IrCode.Nec(address = 0xBF00, command = 0x4E, extended = true)),
                    btn("Detener", ButtonRole.STOP, ButtonSection.MEDIA, IrCode.Nec(address = 0xBF00, command = 0x5A, extended = true)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x00, extended = true)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x01, extended = true)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x02, extended = true)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x03, extended = true)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x04, extended = true)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x05, extended = true)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x06, extended = true)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x07, extended = true)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x08, extended = true)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Nec(address = 0xBF00, command = 0x09, extended = true))
                )
            )
    )
}

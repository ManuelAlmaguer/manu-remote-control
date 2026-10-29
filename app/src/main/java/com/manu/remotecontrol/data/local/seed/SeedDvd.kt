package com.manu.remotecontrol.data.local.seed

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.DeviceType

/**
 * Semilla de reproductores DVD (LG y Sony).
 *
 * Los códigos provienen de la base de datos abierta Flipper-IRDB (licencia CC0 1.0) y se
 * han transcrito respetando protocolo, dirección y comando originales. Los mandos de aire
 * acondicionado se guardan como patrones RAW porque envían el estado completo en cada trama.
 */
internal object SeedDvd {

    val devices: List<SeedDevice> = listOf(
            SeedDevice(
                key = "dvd_lg",
                name = "LG DVD",
                brand = "LG",
                model = "AKB35840202",
                type = DeviceType.DVD,
                frequencyHz = 38000,
                description = "Reproductor LG que usa el protocolo Samsung 32 (dirección 0x2D).",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Samsung(address = 0x2D, command = 0x30)),
                    btn("Abrir / cerrar", ButtonRole.EXIT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x36)),
                    btn("Reproducir", ButtonRole.PLAY, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x31)),
                    btn("Pausa", ButtonRole.PAUSE, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x38)),
                    btn("Detener", ButtonRole.STOP, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x39)),
                    btn("Retroceder", ButtonRole.REWIND, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x32)),
                    btn("Avanzar", ButtonRole.FORWARD, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x33)),
                    btn("Anterior", ButtonRole.PREVIOUS, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x35)),
                    btn("Siguiente", ButtonRole.NEXT, ButtonSection.MEDIA, IrCode.Samsung(address = 0x2D, command = 0x34)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x47)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x48)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x59)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x5A)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x58)),
                    btn("Atrás", ButtonRole.BACK, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x45)),
                    btn("Menú DVD", ButtonRole.MENU, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x4B)),
                    btn("Título", ButtonRole.HOME, ButtonSection.NAVIGATION, IrCode.Samsung(address = 0x2D, command = 0x4A)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x44)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x3B)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x3C)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x3D)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x3E)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x3F)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x40)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x41)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x42)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Samsung(address = 0x2D, command = 0x43))
                )
            ),

            SeedDevice(
                key = "dvd_sony",
                name = "Sony DVD",
                brand = "Sony",
                model = "RMT-D197A",
                type = DeviceType.DVD,
                frequencyHz = 40000,
                description = "Reproductor Sony en SIRC de 20 bits (0x093A).",
                source = "Flipper-IRDB · CC0 1.0",
                buttons = listOf(
                    btn("Encendido", ButtonRole.POWER, ButtonSection.POWER, IrCode.Sony(device = 0x93A, command = 0x15, bits = 20)),
                    btn("Abrir / cerrar", ButtonRole.EXIT, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x16, bits = 20)),
                    btn("Reproducir", ButtonRole.PLAY, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x32, bits = 20)),
                    btn("Pausa", ButtonRole.PAUSE, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x39, bits = 20)),
                    btn("Detener", ButtonRole.STOP, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x38, bits = 20)),
                    btn("Retroceder", ButtonRole.REWIND, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x22, bits = 20)),
                    btn("Avanzar", ButtonRole.FORWARD, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x23, bits = 20)),
                    btn("Anterior", ButtonRole.PREVIOUS, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x30, bits = 20)),
                    btn("Siguiente", ButtonRole.NEXT, ButtonSection.MEDIA, IrCode.Sony(device = 0x93A, command = 0x31, bits = 20)),
                    btn("Arriba", ButtonRole.UP, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x79, bits = 20)),
                    btn("Abajo", ButtonRole.DOWN, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x7A, bits = 20)),
                    btn("Izquierda", ButtonRole.LEFT, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x7B, bits = 20)),
                    btn("Derecha", ButtonRole.RIGHT, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x7C, bits = 20)),
                    btn("OK", ButtonRole.OK, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x0B, bits = 20)),
                    btn("Atrás", ButtonRole.BACK, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x0E, bits = 20)),
                    btn("Menú", ButtonRole.MENU, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x1B, bits = 20)),
                    btn("Info", ButtonRole.INFO, ButtonSection.NAVIGATION, IrCode.Sony(device = 0x93A, command = 0x54, bits = 20)),
                    btn("0", ButtonRole.DIGIT_0, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x09, bits = 20)),
                    btn("1", ButtonRole.DIGIT_1, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x00, bits = 20)),
                    btn("2", ButtonRole.DIGIT_2, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x01, bits = 20)),
                    btn("3", ButtonRole.DIGIT_3, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x02, bits = 20)),
                    btn("4", ButtonRole.DIGIT_4, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x03, bits = 20)),
                    btn("5", ButtonRole.DIGIT_5, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x04, bits = 20)),
                    btn("6", ButtonRole.DIGIT_6, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x05, bits = 20)),
                    btn("7", ButtonRole.DIGIT_7, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x06, bits = 20)),
                    btn("8", ButtonRole.DIGIT_8, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x07, bits = 20)),
                    btn("9", ButtonRole.DIGIT_9, ButtonSection.DIGITS, IrCode.Sony(device = 0x93A, command = 0x08, bits = 20))
                )
            )
    )
}

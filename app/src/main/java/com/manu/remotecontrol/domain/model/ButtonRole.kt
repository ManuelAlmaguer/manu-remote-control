package com.manu.remotecontrol.domain.model

/**
 * Roles semánticos de los botones. La UI usa el rol para decidir el icono, el color y
 * la posición dentro de la plantilla del control (D-Pad, teclado, etc.).
 */
enum class ButtonRole(val storageKey: String, val defaultLabel: String) {
    POWER("POWER", "Encendido"),
    POWER_ON("POWER_ON", "Encender"),
    POWER_OFF("POWER_OFF", "Apagar"),
    UP("UP", "Arriba"),
    DOWN("DOWN", "Abajo"),
    LEFT("LEFT", "Izquierda"),
    RIGHT("RIGHT", "Derecha"),
    OK("OK", "OK"),
    BACK("BACK", "Atrás"),
    HOME("HOME", "Inicio"),
    MENU("MENU", "Menú"),
    EXIT("EXIT", "Salir"),
    INFO("INFO", "Info"),
    INPUT("INPUT", "Fuente"),
    MUTE("MUTE", "Silencio"),
    VOL_UP("VOL_UP", "Volumen +"),
    VOL_DOWN("VOL_DOWN", "Volumen −"),
    CH_UP("CH_UP", "Canal +"),
    CH_DOWN("CH_DOWN", "Canal −"),
    DIGIT_0("DIGIT_0", "0"),
    DIGIT_1("DIGIT_1", "1"),
    DIGIT_2("DIGIT_2", "2"),
    DIGIT_3("DIGIT_3", "3"),
    DIGIT_4("DIGIT_4", "4"),
    DIGIT_5("DIGIT_5", "5"),
    DIGIT_6("DIGIT_6", "6"),
    DIGIT_7("DIGIT_7", "7"),
    DIGIT_8("DIGIT_8", "8"),
    DIGIT_9("DIGIT_9", "9"),
    PLAY("PLAY", "Reproducir"),
    PAUSE("PAUSE", "Pausa"),
    STOP("STOP", "Detener"),
    NEXT("NEXT", "Siguiente"),
    PREVIOUS("PREVIOUS", "Anterior"),
    REWIND("REWIND", "Retroceder"),
    FORWARD("FORWARD", "Avanzar"),
    RECORD("RECORD", "Grabar"),
    NETFLIX("NETFLIX", "Netflix"),
    YOUTUBE("YOUTUBE", "YouTube"),
    TEMP_UP("TEMP_UP", "Temperatura +"),
    TEMP_DOWN("TEMP_DOWN", "Temperatura −"),
    MODE("MODE", "Modo"),
    FAN("FAN", "Ventilador"),
    SWING("SWING", "Oscilación"),
    TIMER("TIMER", "Temporizador"),
    TURBO("TURBO", "Turbo"),
    SLEEP("SLEEP", "Sleep"),
    CUSTOM("CUSTOM", "Botón");

    companion object {
        fun fromKey(key: String?): ButtonRole =
            entries.firstOrNull { it.storageKey == key } ?: CUSTOM

        fun digit(value: Int): ButtonRole = when (value) {
            0 -> DIGIT_0
            1 -> DIGIT_1
            2 -> DIGIT_2
            3 -> DIGIT_3
            4 -> DIGIT_4
            5 -> DIGIT_5
            6 -> DIGIT_6
            7 -> DIGIT_7
            8 -> DIGIT_8
            else -> DIGIT_9
        }
    }
}

/** Agrupación visual de los botones dentro del control. */
enum class ButtonSection(val storageKey: String, val displayName: String) {
    POWER("POWER", "Encendido"),
    NAVIGATION("NAVIGATION", "Navegación"),
    VOLUME("VOLUME", "Volumen"),
    CHANNEL("CHANNEL", "Canales"),
    DIGITS("DIGITS", "Teclado numérico"),
    MEDIA("MEDIA", "Multimedia"),
    APPS("APPS", "Aplicaciones"),
    CLIMATE("CLIMATE", "Climatización"),
    CUSTOM("CUSTOM", "Personalizados");

    companion object {
        fun fromKey(key: String?): ButtonSection =
            entries.firstOrNull { it.storageKey == key } ?: CUSTOM
    }
}

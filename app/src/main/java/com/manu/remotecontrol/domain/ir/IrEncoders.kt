package com.manu.remotecontrol.domain.ir

import com.manu.remotecontrol.domain.ir.protocols.NecEncoder
import com.manu.remotecontrol.domain.ir.protocols.RawEncoder
import com.manu.remotecontrol.domain.ir.protocols.Rc5Encoder
import com.manu.remotecontrol.domain.ir.protocols.SamsungEncoder
import com.manu.remotecontrol.domain.ir.protocols.SonySircEncoder

/**
 * Fábrica con el conjunto de codificadores que la app registra por defecto.
 *
 * Añadir un protocolo = crear su [IrEncoder] y añadirlo a esta lista (o inyectar otra
 * lista en el módulo de Hilt). El resto de la app no necesita cambios.
 */
object IrEncoders {

    fun defaultEncoders(): List<IrEncoder> = listOf(
        NecEncoder(),
        SamsungEncoder(),
        SonySircEncoder(),
        Rc5Encoder(),
        RawEncoder()
    )

    fun defaultRegistry(): IrProtocolRegistry = IrProtocolRegistry(defaultEncoders())
}

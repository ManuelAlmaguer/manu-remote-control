package com.manu.remotecontrol.domain.ir

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests del motor de protocolos. Verifican la estructura de las tramas generadas
 * (tiempos, número de pulsos y codificación de bits) y la ida y vuelta de los códigos.
 */
class IrProtocolEncoderTest {

    private val registry = IrEncoders.instantiateForTests()

    // ---------------------------------------------------------------- NEC

    @Test
    fun `nec frame has leader, 32 bits and trailer`() {
        val code = IrCode.Nec(address = 0x04, command = 0x08)
        val pattern = registry.encode(code).pattern

        // 2 de cabecera + 32 bits * 2 + marca final
        assertEquals(67, pattern.size)
        assertEquals(9_000, pattern[0])
        assertEquals(4_500, pattern[1])
        assertEquals(560, pattern.last())
        assertEquals(38_000, registry.encode(code).frequencyHz)
    }

    @Test
    fun `nec encodes address command and their inverses lsb first`() {
        val code = IrCode.Nec(address = 0x04, command = 0x08)
        val bits = decodePulseDistance(registry.encode(code).pattern, skip = 2, bitCount = 32)

        assertEquals(0x04, bits.value(0, 8))
        assertEquals(0xFB, bits.value(8, 8)) // ~0x04
        assertEquals(0x08, bits.value(16, 8))
        assertEquals(0xF7, bits.value(24, 8)) // ~0x08
    }

    @Test
    fun `nec extended uses 16 bit address without inversion`() {
        val code = IrCode.Nec(address = 0xC7EA, command = 0x17, extended = true)
        val encoding = registry.encode(code)
        val bits = decodePulseDistance(encoding.pattern, skip = 2, bitCount = 32)

        assertEquals(IrProtocolId.NEC_EXTENDED, code.protocol)
        assertEquals(0xC7EA, bits.value(0, 16))
        assertEquals(0x17, bits.value(16, 8))
        assertEquals(0xE8, bits.value(24, 8))
    }

    @Test
    fun `nec repeats add a gap between frames`() {
        val single = registry.encode(IrCode.Nec(0x04, 0x08, repeats = 1)).pattern
        val triple = registry.encode(IrCode.Nec(0x04, 0x08, repeats = 3)).pattern

        assertTrue(triple.size > single.size * 2)
        assertTrue(triple.contains(NecEncoderConstants.FRAME_GAP))
    }

    // ------------------------------------------------------------ SAMSUNG

    @Test
    fun `samsung frame repeats address and inverts the command`() {
        val code = IrCode.Samsung(address = 0x07, command = 0x02)
        val pattern = registry.encode(code).pattern
        assertEquals(4_500, pattern[0])
        assertEquals(4_500, pattern[1])

        val bits = decodePulseDistance(pattern, skip = 2, bitCount = 32)
        assertEquals(0x07, bits.value(0, 8))
        assertEquals(0x07, bits.value(8, 8))
        assertEquals(0x02, bits.value(16, 8))
        assertEquals(0xFD, bits.value(24, 8))
    }

    // ---------------------------------------------------------------- SONY

    @Test
    fun `sony sirc 12 bit frame layout`() {
        val code = IrCode.Sony(device = 0x01, command = 0x15, bits = 12, repeats = 1)
        val pattern = registry.encode(code).pattern

        assertEquals(2_400, pattern[0])
        assertEquals(600, pattern[1])
        // cabecera + 12 bits * 2
        assertEquals(26, pattern.size)

        // Los 7 primeros bits son el comando en LSB: 0x15 = 0b0010101
        val bits = (0 until 12).map { index ->
            val mark = pattern[2 + index * 2]
            if (mark > 900) 1 else 0
        }
        var command = 0
        bits.take(7).forEachIndexed { index, bit -> if (bit == 1) command = command or (1 shl index) }
        assertEquals(0x15, command)

        var device = 0
        bits.drop(7).forEachIndexed { index, bit -> if (bit == 1) device = device or (1 shl index) }
        assertEquals(0x01, device)
        assertEquals(40_000, registry.encode(code).frequencyHz)
    }

    @Test
    fun `sony sirc 20 bit frame is longer`() {
        val code = IrCode.Sony(device = 0x093A, command = 0x15, bits = 20, repeats = 1)
        val pattern = registry.encode(code).pattern
        assertEquals(2 + 20 * 2, pattern.size)
    }

    // ----------------------------------------------------------------- RC5

    @Test
    fun `rc5 frame duration matches 14 manchester bits`() {
        val code = IrCode.Rc5(system = 0, command = 0x0C)
        val encoding = registry.encode(code)

        assertEquals(36_000, encoding.frequencyHz)
        assertEquals(889, encoding.pattern.first())
        // 14 bits * 1778 µs (dos medias 889) con posibles fusiones
        assertTrue(encoding.durationMicros in 24_000..25_500)
    }

    @Test
    fun `rc5 toggle changes the waveform`() {
        val withoutToggle = registry.encode(IrCode.Rc5(0, 0x0C, toggle = false)).pattern.toList()
        val withToggle = registry.encode(IrCode.Rc5(0, 0x0C, toggle = true)).pattern.toList()
        assertTrue(withoutToggle != withToggle)
    }

    // ----------------------------------------------------------------- RAW

    @Test
    fun `raw keeps custom microsecond pattern`() {
        val pattern = listOf(9_000, 4_500, 560, 560, 560, 1_690)
        val encoding = registry.encode(IrCode.Raw(pattern, frequencyHz = 56_000))
        assertEquals(56_000, encoding.frequencyHz)
        assertEquals(pattern, encoding.pattern.toList())
        assertEquals(pattern.sum().toLong(), encoding.durationMicros)
    }

    @Test
    fun `raw repeats duplicate the pattern`() {
        val pattern = listOf(1_000, 2_000)
        val encoding = registry.encode(IrCode.Raw(pattern, repeats = 2))
        assertEquals(4, encoding.pattern.size) // marca, hueco, marca, espacio final
    }

    // --------------------------------------------------------------- CODEC

    @Test
    fun `nec hex round trip`() {
        val code = IrCode.Nec(address = 0x04, command = 0x08)
        assertEquals("04FB08F7", IrCodeCodec.toHex(code))
        assertEquals(code, IrCodeCodec.parse(IrProtocolId.NEC, "04FB08F7", 38_000, 1))
    }

    @Test
    fun `nec extended hex round trip`() {
        val parsed = IrCodeCodec.parse(IrProtocolId.NEC_EXTENDED, "EAC717E8", 38_000, 1)
        assertNotNull(parsed)
        val nec = parsed as IrCode.Nec
        assertEquals(0xC7EA, nec.address)
        assertEquals(0x17, nec.command)
        assertTrue(nec.extended)
        assertEquals("EAC717E8", IrCodeCodec.toHex(nec))
    }

    @Test
    fun `samsung hex round trip`() {
        val code = IrCode.Samsung(address = 0xE0, command = 0x40)
        assertEquals("E0E040BF", IrCodeCodec.toHex(code))
        assertEquals(code, IrCodeCodec.parse(IrProtocolId.SAMSUNG, "E0E040BF", 38_000, 1))
    }

    @Test
    fun `sony and rc5 hex parsing`() {
        val sony = IrCodeCodec.parse(IrProtocolId.SONY_SIRC, "0115", 40_000, 2) as IrCode.Sony
        assertEquals(0x01, sony.device)
        assertEquals(0x15, sony.command)
        assertEquals(12, sony.bits)

        val rc5 = IrCodeCodec.parse(IrProtocolId.RC5, "000C", 36_000, 1) as IrCode.Rc5
        assertEquals(0, rc5.system)
        assertEquals(0x0C, rc5.command)
    }

    @Test
    fun `invalid hex is rejected`() {
        assertNull(IrCodeCodec.parse(IrProtocolId.NEC, "1234", 38_000))
        assertNull(IrCodeCodec.parse(IrProtocolId.NEC, "ZZZZZZZZ", 38_000))
        assertNull(IrCodeCodec.parse(IrProtocolId.RAW, "", 38_000))
    }

    @Test
    fun `raw pattern parsing accepts commas and semicolons`() {
        val parsed = IrCodeCodec.parse(IrProtocolId.RAW, "9000, 4500; 560 560", 38_000) as IrCode.Raw
        assertEquals(listOf(9_000, 4_500, 560, 560), parsed.pattern)
    }

    // ------------------------------------------------------------- REGISTRY

    @Test
    fun `registry exposes all required protocols`() {
        val protocols = registry.supportedProtocols
        assertTrue(protocols.containsAll(listOf(
            IrProtocolId.NEC,
            IrProtocolId.NEC_EXTENDED,
            IrProtocolId.SAMSUNG,
            IrProtocolId.SONY_SIRC,
            IrProtocolId.RC5,
            IrProtocolId.RAW
        )))
    }

    @Test
    fun `carrier frequency override is honoured`() {
        val code = IrCode.Nec(0x04, 0x08)
        val encoding = registry.encode(code, carrierFrequencyHz = 40_000)
        assertEquals(40_000, encoding.frequencyHz)
    }

    // -------------------------------------------------------------- helpers

    private fun decodePulseDistance(pattern: IntArray, skip: Int, bitCount: Int): BitList {
        val bits = mutableListOf<Int>()
        for (index in 0 until bitCount) {
            val space = pattern[skip + index * 2 + 1]
            bits += if (space > 1_000) 1 else 0
        }
        return BitList(bits)
    }

    private class BitList(private val bits: List<Int>) {
        fun value(offset: Int, length: Int): Int {
            var result = 0
            for (i in 0 until length) {
                if (bits[offset + i] == 1) result = result or (1 shl i)
            }
            return result
        }
    }

    private object NecEncoderConstants {
        const val FRAME_GAP = 40_000
    }
}

/** Acceso al registro sin depender de Hilt en los tests. */
private fun IrEncoders.instantiateForTests(): IrProtocolRegistry = defaultRegistry()

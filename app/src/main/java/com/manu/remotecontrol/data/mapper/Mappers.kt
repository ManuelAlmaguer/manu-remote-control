package com.manu.remotecontrol.data.mapper

import com.manu.remotecontrol.data.local.entity.ButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogDeviceEntity
import com.manu.remotecontrol.data.local.entity.DeviceEntity
import com.manu.remotecontrol.data.local.entity.DeviceWithButtonCount
import com.manu.remotecontrol.data.local.seed.SeedButton
import com.manu.remotecontrol.data.local.seed.SeedDevice
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.CatalogDevice
import com.manu.remotecontrol.domain.model.DeviceType
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice

/** Serializa un [IrCode] a las columnas planas de [ButtonEntity]. */
object IrCodeMapper {

    fun toColumns(code: IrCode): IrColumns = when (code) {
        is IrCode.Nec -> IrColumns(
            protocol = code.protocol.name,
            frequencyHz = code.frequencyHz,
            address = code.address,
            command = code.command,
            repeats = code.repeats
        )

        is IrCode.Samsung -> IrColumns(
            protocol = code.protocol.name,
            frequencyHz = code.frequencyHz,
            address = code.address,
            command = code.command,
            repeats = code.repeats
        )

        is IrCode.Sony -> IrColumns(
            protocol = code.protocol.name,
            frequencyHz = code.frequencyHz,
            address = code.device,
            command = code.command,
            bits = code.bits,
            repeats = code.repeats
        )

        is IrCode.Rc5 -> IrColumns(
            protocol = code.protocol.name,
            frequencyHz = code.frequencyHz,
            address = code.system,
            command = code.command,
            toggle = code.toggle,
            repeats = code.repeats
        )

        is IrCode.Raw -> IrColumns(
            protocol = code.protocol.name,
            frequencyHz = code.frequencyHz,
            repeats = code.repeats,
            rawPattern = code.pattern.joinToString(" ")
        )
    }

    /** Reconstruye el [IrCode] de dominio a partir de las columnas almacenadas. */
    fun fromColumns(
        protocol: String,
        frequencyHz: Int,
        address: Int,
        command: Int,
        bits: Int,
        toggle: Boolean,
        repeats: Int,
        rawPattern: String?
    ): IrCode = when (IrProtocolId.fromNameOrDefault(protocol)) {
        IrProtocolId.NEC -> IrCode.Nec(address, command, frequencyHz, repeats.coerceAtLeast(1))
        IrProtocolId.NEC_EXTENDED -> IrCode.Nec(address, command, frequencyHz, repeats.coerceAtLeast(1), extended = true)
        IrProtocolId.SAMSUNG -> IrCode.Samsung(address, command, frequencyHz, repeats.coerceAtLeast(1))
        IrProtocolId.SONY_SIRC -> IrCode.Sony(address, command, bits, frequencyHz, repeats.coerceAtLeast(2))
        IrProtocolId.RC5 -> IrCode.Rc5(address, command, toggle, frequencyHz, repeats.coerceAtLeast(1))
        IrProtocolId.RAW -> IrCode.Raw(
            pattern = rawPattern.orEmpty().split(" ").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() },
            frequencyHz = frequencyHz,
            repeats = repeats.coerceAtLeast(1)
        )
    }

    data class IrColumns(
        val protocol: String,
        val frequencyHz: Int,
        val address: Int = 0,
        val command: Int = 0,
        val bits: Int = 12,
        val toggle: Boolean = false,
        val repeats: Int = 1,
        val rawPattern: String? = null
    )
}

fun DeviceEntity.toDomain(buttonCount: Int = 0): RemoteDevice = RemoteDevice(
    id = id,
    name = name,
    brand = brand,
    model = model,
    type = DeviceType.fromKey(type),
    isFavorite = isFavorite,
    isCustom = isCustom,
    carrierFrequencyHz = carrierFrequencyHz,
    createdAt = createdAt,
    buttonCount = buttonCount
)

fun DeviceWithButtonCount.toDomain(): RemoteDevice = device.toDomain(buttonCount)

fun RemoteDevice.toEntity(): DeviceEntity = DeviceEntity(
    id = id,
    name = name,
    brand = brand,
    model = model,
    type = type.storageKey,
    isFavorite = isFavorite,
    isCustom = isCustom,
    carrierFrequencyHz = carrierFrequencyHz,
    createdAt = createdAt
)

fun ButtonEntity.toDomain(): RemoteButton = RemoteButton(
    id = id,
    deviceId = deviceId,
    label = label,
    role = ButtonRole.fromKey(role),
    section = ButtonSection.fromKey(section),
    code = IrCodeMapper.fromColumns(protocol, frequencyHz, address, command, bits, toggle, repeats, rawPattern),
    sortOrder = sortOrder
)

fun CatalogButtonEntity.toDomain(deviceId: Long): RemoteButton = RemoteButton(
    id = 0L,
    deviceId = deviceId,
    label = label,
    role = ButtonRole.fromKey(role),
    section = ButtonSection.fromKey(section),
    code = IrCodeMapper.fromColumns(protocol, frequencyHz, address, command, bits, toggle, repeats, rawPattern),
    sortOrder = sortOrder
)

fun RemoteButton.toEntity(): ButtonEntity {
    val columns = IrCodeMapper.toColumns(code)
    return ButtonEntity(
        id = id,
        deviceId = deviceId,
        label = label,
        role = role.storageKey,
        section = section.storageKey,
        protocol = columns.protocol,
        frequencyHz = columns.frequencyHz,
        address = columns.address,
        command = columns.command,
        bits = columns.bits,
        toggle = columns.toggle,
        repeats = columns.repeats,
        rawPattern = columns.rawPattern,
        sortOrder = sortOrder
    )
}

internal fun SeedDevice.toCatalogEntity(): CatalogDeviceEntity = CatalogDeviceEntity(
    catalogKey = key,
    name = name,
    brand = brand,
    model = model,
    type = type.storageKey,
    frequencyHz = frequencyHz,
    description = description,
    source = source,
    buttonCount = buttons.size
)

internal fun SeedButton.toCatalogEntity(seedKey: String, order: Int): CatalogButtonEntity {
    val columns = IrCodeMapper.toColumns(code)
    return CatalogButtonEntity(
        catalogKey = seedKey,
        label = label,
        role = role.storageKey,
        section = section.storageKey,
        protocol = columns.protocol,
        frequencyHz = columns.frequencyHz,
        address = columns.address,
        command = columns.command,
        bits = columns.bits,
        toggle = columns.toggle,
        repeats = columns.repeats,
        rawPattern = columns.rawPattern,
        sortOrder = order
    )
}

fun CatalogDeviceEntity.toDomain(): CatalogDevice = CatalogDevice(
    key = catalogKey,
    name = name,
    brand = brand,
    model = model,
    type = DeviceType.fromKey(type),
    frequencyHz = frequencyHz,
    description = description,
    buttonCount = buttonCount,
    source = source
)

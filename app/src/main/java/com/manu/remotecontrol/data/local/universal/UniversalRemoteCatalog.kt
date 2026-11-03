package com.manu.remotecontrol.data.local.universal

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/** Paso del manual de un mando universal. */
data class ManualStep(val title: String, val steps: List<String>)

/** Código de programación de un mando universal (los de 3 dígitos del folleto). */
data class UniversalCode(
    val code: String,
    val brand: String,
    val description: String,
    /** Marca equivalente dentro del catálogo IR de la app (si existe). */
    val appBrand: String? = null,
    /** Tipo de dispositivo del catálogo con el que se corresponde. */
    val appType: String? = null
)

/** Grupo de códigos (televisores, cajas decodificadoras, DVD...). */
data class UniversalCodeGroup(val name: String, val codes: List<UniversalCode>)

/** Mando universal concreto (por ejemplo el KT1440) con su manual y su lista de códigos. */
data class UniversalRemote(
    val id: String,
    val title: String,
    val brand: String,
    val summary: String,
    val notes: List<String>,
    val source: String,
    val manual: List<ManualStep>,
    val groups: List<UniversalCodeGroup>
) {
    val totalCodes: Int get() = groups.sumOf { it.codes.size }
}

/**
 * Catálogo de mandos universales físicos, leído del asset
 * `assets/catalog/universal_remotes.json`.
 *
 * A diferencia del catálogo IR (que guarda códigos infrarrojos listos para emitir), aquí se
 * guarda lo que trae el folleto del mando: el manual de programación y la tabla de códigos
 * de 3 dígitos por marca, con el enlace a la marca equivalente del catálogo de la app.
 */
@Singleton
class UniversalRemoteCatalog @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Volatile
    private var cached: List<UniversalRemote>? = null

    suspend fun load(): List<UniversalRemote> {
        cached?.let { return it }
        val parsed = runCatching { parse(readAsset()) }.getOrDefault(emptyList())
        cached = parsed
        return parsed
    }

    private fun readAsset(): String =
        context.assets.open(PATH).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun parse(json: String): List<UniversalRemote> {
        val root = JSONObject(json)
        val remotes = root.optJSONArray("remotes") ?: JSONArray()
        return (0 until remotes.length()).map { index ->
            val remote = remotes.getJSONObject(index)
            UniversalRemote(
                id = remote.optString("id"),
                title = remote.optString("title"),
                brand = remote.optString("brand"),
                summary = remote.optString("summary"),
                notes = remote.optJSONArray("notes").toStringList(),
                source = remote.optString("source"),
                manual = parseManual(remote.optJSONArray("manual")),
                groups = parseGroups(remote.optJSONArray("groups"))
            )
        }
    }

    private fun parseManual(array: JSONArray?): List<ManualStep> {
        if (array == null) return emptyList()
        return (0 until array.length()).map { index ->
            val step = array.getJSONObject(index)
            ManualStep(
                title = step.optString("title"),
                steps = step.optJSONArray("steps").toStringList()
            )
        }
    }

    private fun parseGroups(array: JSONArray?): List<UniversalCodeGroup> {
        if (array == null) return emptyList()
        return (0 until array.length()).map { index ->
            val group = array.getJSONObject(index)
            val codes = group.optJSONArray("codes") ?: JSONArray()
            UniversalCodeGroup(
                name = group.optString("name"),
                codes = (0 until codes.length()).map { codeIndex ->
                    val code = codes.getJSONObject(codeIndex)
                    UniversalCode(
                        code = code.optString("code"),
                        brand = code.optString("brand"),
                        description = code.optString("description"),
                        appBrand = code.optString("appBrand").takeIf { it.isNotBlank() },
                        appType = code.optString("appType").takeIf { it.isNotBlank() }
                    )
                }
            )
        }
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).map { optString(it) }.filter { it.isNotBlank() }
    }

    private companion object {
        const val PATH = "catalog/universal_remotes.json"
    }
}

package com.manu.remotecontrol.presentation.universal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manu.remotecontrol.data.local.universal.ManualStep
import com.manu.remotecontrol.data.local.universal.UniversalCode
import com.manu.remotecontrol.data.local.universal.UniversalCodeGroup
import com.manu.remotecontrol.data.local.universal.UniversalRemote
import com.manu.remotecontrol.presentation.components.SectionHeader

/**
 * Sección de mandos universales físicos (por ejemplo el KT1440): manual de programación y
 * tabla completa de códigos de 3 dígitos por marca.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalRemotesScreen(
    onBack: () -> Unit,
    onOpenCatalog: () -> Unit,
    viewModel: UniversalRemotesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val remote = state.selected

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mandos universales",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Manual y códigos de programación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.isLoading) {
                item { Text("Cargando mandos…", style = MaterialTheme.typography.bodyMedium) }
            } else if (state.remotes.isEmpty()) {
                item { EmptyUniversalCard() }
            } else {
                if (state.remotes.size > 1) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.remotes.forEach { item ->
                                FilterChip(
                                    selected = item.id == remote?.id,
                                    onClick = { viewModel.onSelect(item.id) },
                                    label = { Text(item.title) }
                                )
                            }
                        }
                    }
                }

                if (remote != null) {
                    item { RemoteHeaderCard(remote) }

                    item { SectionHeader(title = "Manual de programación") }
                    items(remote.manual) { step -> ManualStepCard(step) }

                    item {
                        SectionHeader(
                            title = "Códigos",
                            subtitle = "${remote.totalCodes} códigos de 3 dígitos"
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = state.query,
                            onValueChange = viewModel::onQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Rounded.Search, contentDescription = null)
                            },
                            label = { Text("Buscar por marca, código o modelo") }
                        )
                    }

                    val filtered = remote.groups.mapNotNull { group ->
                        val codes = group.codes.filter { code ->
                            state.query.isBlank() || code.matches(state.query)
                        }
                        if (codes.isEmpty()) null else group to codes
                    }
                    filtered.forEach { (group, codes) ->
                        item(key = "h_${group.name}") {
                            SectionHeader(title = group.name, subtitle = "${codes.size} códigos")
                        }
                        items(codes, key = { "${group.name}_${it.code}_${it.description}" }) { code ->
                            CodeRow(code = code, onOpenCatalog = onOpenCatalog)
                        }
                    }

                    if (filtered.isEmpty()) {
                        item {
                            Text(
                                text = "Ningún código coincide con «${state.query}».",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Fuente: ${remote.source}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun UniversalCode.matches(query: String): Boolean {
    val needle = query.trim().lowercase()
    if (needle.isEmpty()) return true
    return code.contains(needle) ||
        brand.lowercase().contains(needle) ||
        description.lowercase().contains(needle)
}

@Composable
private fun RemoteHeaderCard(remote: UniversalRemote) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${remote.title} · ${remote.brand}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = remote.summary, style = MaterialTheme.typography.bodyMedium)
            remote.notes.forEach { note ->
                Text(
                    text = "• $note",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ManualStepCard(step: ManualStep) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            step.steps.forEachIndexed { index, line ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = line, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun CodeRow(code: UniversalCode, onOpenCatalog: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = code.code,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = code.brand,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = code.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (code.appBrand != null) {
                TextButton(onClick = onOpenCatalog) {
                    Text("Buscar mando")
                }
            }
        }
    }
}

@Composable
private fun EmptyUniversalCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Sin mandos universales", fontWeight = FontWeight.Bold)
            Text(
                text = "El archivo de mandos universales no está disponible en esta compilación.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Suppress("unused")
private fun UniversalCodeGroup.summary(): String = "${name}: ${codes.size} códigos"

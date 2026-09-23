package com.example.prestamolab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.viewmodel.EstadoPagina
import com.example.prestamolab.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    state: PrestamoUiState,
    onEquipoClick: (Int) -> Unit,
    onSincronizar: () -> Unit,
    onFiltroCategoria: (CategoriaEquipo?) -> Unit
) {
    var fotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    val lanzarCamara = recordarCapturaCamara { uri -> fotoUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "PrestamoLab CTMA",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Catalogo de equipos",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { lanzarCamara() }
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onSincronizar,
                        enabled = !state.sincronizando
                    ) {
                        if (state.sincronizando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Sincronizar con el servicio",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        val equiposFiltrados = state.equipos.filter { equipo ->
            state.filtroCategoria == null || equipo.categoria == state.filtroCategoria
        }

        when (state.estadoCatalogo) {
            EstadoPagina.CARGANDO -> CargandoState(Modifier.padding(padding))
            EstadoPagina.VACIO -> EmptyState(
                message = "No hay equipos disponibles.",
                modifier = Modifier.padding(padding)
            )
            EstadoPagina.ERROR -> {
                Column(modifier = Modifier.padding(padding)) {
                    ErrorState("No se pudo cargar el catalogo.", Modifier.weight(1f))
                    Button(
                        onClick = onSincronizar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Reintentar")
                    }
                }
            }
            EstadoPagina.CONTENIDO -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    FiltroCategoriaRow(
                        seleccion = state.filtroCategoria,
                        onSeleccion = onFiltroCategoria
                    )
                }
                item {
                    Text(
                        "Selecciona un equipo para ver su detalle.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(equiposFiltrados, key = { it.id }) { equipo ->
                    EquipoCard(equipo, onEquipoClick)
                }

                if (equiposFiltrados.isEmpty()) {
                    item {
                        Text(
                            "Ningún equipo coincide con el filtro seleccionado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }

    fotoUri?.let { uri ->
        AlertDialog(
            onDismissRequest = { fotoUri = null },
            title = { Text("Foto capturada") },
            text = {
                Column {
                    EvidenciaPreview(uri)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "URI: $uri",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { fotoUri = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun FiltroCategoriaRow(
    seleccion: CategoriaEquipo?,
    onSeleccion: (CategoriaEquipo?) -> Unit
) {
    val opciones = listOf<CategoriaEquipo?>(null) + CategoriaEquipo.values().toList()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { categoria ->
            val seleccionado = seleccion == categoria
            FilterChip(
                selected = seleccionado,
                onClick = { onSeleccion(categoria) },
                label = {
                    Text(
                        categoria?.texto ?: "Todas",
                        fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
private fun EquipoCard(
    equipo: Equipo,
    onClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(equipo.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoriaIcon(equipo.categoria),
                    contentDescription = equipo.categoria.texto,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    equipo.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    equipo.categoria.texto,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                EstadoChip(equipo.estado)
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EstadoChip(estado: EstadoEquipo) {
    val (backgroundColor, textColor) = when (estado) {
        EstadoEquipo.DISPONIBLE -> com.example.prestamolab.ui.theme.StatusDisponibleBg to com.example.prestamolab.ui.theme.StatusDisponible
        EstadoEquipo.RESERVADO -> com.example.prestamolab.ui.theme.StatusReservadoBg to com.example.prestamolab.ui.theme.StatusReservado
        EstadoEquipo.PRESTADO -> com.example.prestamolab.ui.theme.StatusPrestadoBg to com.example.prestamolab.ui.theme.StatusPrestado
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = estado.texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

private fun categoriaIcon(categoria: CategoriaEquipo): ImageVector = when (categoria) {
    CategoriaEquipo.COMPUTO -> Icons.Default.Computer
    CategoriaEquipo.AUDIOVISUAL -> Icons.Default.Videocam
    CategoriaEquipo.ELECTRONICA -> Icons.Default.Memory
    CategoriaEquipo.REDES -> Icons.Default.Router
}
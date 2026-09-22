package com.example.prestamolab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.prestamolab.PrestamoLabApplication
import com.example.prestamolab.ui.screens.*
import com.example.prestamolab.util.AlarmRecordatorio
import com.example.prestamolab.util.GpsUbicacionProvider
import com.example.prestamolab.viewmodel.PrestamoViewModel
import com.example.prestamolab.viewmodel.PrestamoViewModelFactory

private object Routes {
    const val CATALOGO = "catalogo"
    const val SOLICITUDES = "solicitudes"
    const val EQUIPO = "equipo/{equipoId}"
    const val SOLICITAR = "solicitar/{equipoId}"
    const val SOLICITUD = "solicitud/{solicitudId}"
}

@Composable
fun PrestamoLabApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as PrestamoLabApplication
    val container = application.container

    val viewModel: PrestamoViewModel = viewModel(
        factory = PrestamoViewModelFactory(
            repository = container.repository,
            ubicacionProvider = GpsUbicacionProvider(context),
            notificador = AlarmRecordatorio(context)
        )
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                val backStack by navController.currentBackStackEntryAsState()
                NavigationBarItem(
                    selected = backStack?.destination?.route == Routes.CATALOGO,
                    onClick = {
                        navController.navigate(Routes.CATALOGO) {
                            popUpTo(Routes.CATALOGO) { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Catalogo") },
                    label = {
                        Text(
                            "Equipos",
                            fontWeight = if (backStack?.destination?.route == Routes.CATALOGO)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                NavigationBarItem(
                    selected = backStack?.destination?.route == Routes.SOLICITUDES,
                    onClick = {
                        navController.navigate(Routes.SOLICITUDES) {
                            popUpTo(Routes.CATALOGO)
                        }
                    },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Mis solicitudes") },
                    label = {
                        Text(
                            "Solicitudes",
                            fontWeight = if (backStack?.destination?.route == Routes.SOLICITUDES)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CATALOGO,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.CATALOGO) {
                CatalogoScreen(
                    state = state,
                    onEquipoClick = { id -> navController.navigate("equipo/$id") },
                    onSincronizar = viewModel::sincronizar,
                    onFiltroCategoria = viewModel::guardarFiltroCategoria
                )
            }

            composable(Routes.SOLICITUDES) {
                MisSolicitudesScreen(
                    state = state,
                    onSolicitudClick = { id -> navController.navigate("solicitud/$id") }
                )
            }

            composable(
                Routes.EQUIPO,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("equipoId") ?: -1
                EquipoDetalleScreen(
                    equipo = state.equipos.firstOrNull { it.id == id },
                    onBack = { navController.popBackStack() },
                    onSolicitar = { navController.navigate("solicitar/$id") }
                )
            }

            composable(
                Routes.SOLICITAR,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("equipoId") ?: -1
                SolicitarScreen(
                    equipo = state.equipos.firstOrNull { it.id == id },
                    state = state,
                    onBack = { navController.popBackStack() },
                    onCapturarUbicacion = viewModel::capturarUbicacion,
                    onGuardar = { destino, proposito, horas ->
                        viewModel.crearSolicitud(id, destino, proposito, horas) { ok ->
                            if (ok) {
                                navController.navigate(Routes.SOLICITUDES) {
                                    popUpTo(Routes.CATALOGO)
                                }
                            }
                        }
                    },
                    onDismissMessage = viewModel::limpiarMensaje
                )
            }

            composable(
                Routes.SOLICITUD,
                arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("solicitudId") ?: -1
                val solicitud = state.solicitudes.firstOrNull { it.id == id }
                SolicitudDetalleScreen(
                    solicitud = solicitud,
                    equipo = solicitud?.let { it1 -> state.equipos.firstOrNull { e -> e.id == it1.equipoId } },
                    onBack = { navController.popBackStack() },
                    onCancelar = { viewModel.cancelarSolicitud(id) },
                    onRegistrarDevolucion = { uri -> viewModel.registrarDevolucion(id, uri) }
                )
            }
        }
    }
}
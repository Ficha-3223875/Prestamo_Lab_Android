package com.example.prestamolab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.prestamolab.data.repository.RepositoryProvider
import com.example.prestamolab.ui.screens.*
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
    val viewModel: PrestamoViewModel = viewModel(
        factory = PrestamoViewModelFactory(RepositoryProvider.repository)
    )
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = NavigationBarDefaults.Elevation
            ) {
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == Routes.CATALOGO,
                    onClick = {
                        navController.navigate(Routes.CATALOGO) {
                            popUpTo(Routes.CATALOGO) { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = "Catalogo"
                        )
                    },
                    label = {
                        Text(
                            "Equipos",
                            fontWeight = if (navController.currentBackStackEntryAsState().value?.destination?.route == Routes.CATALOGO)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                NavigationBarItem(
                    selected = navController.currentBackStackEntryAsState().value?.destination?.route == Routes.SOLICITUDES,
                    onClick = {
                        navController.navigate(Routes.SOLICITUDES) {
                            popUpTo(Routes.CATALOGO)
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = "Mis solicitudes"
                        )
                    },
                    label = {
                        Text(
                            "Solicitudes",
                            fontWeight = if (navController.currentBackStackEntryAsState().value?.destination?.route == Routes.SOLICITUDES)
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
                    onEquipoClick = { id -> navController.navigate("equipo/$id") }
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
                    equipo = viewModel.equipo(id),
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
                    equipo = viewModel.equipo(id),
                    state = state,
                    onBack = { navController.popBackStack() },
                    onGuardar = { destino, proposito, horas ->
                        val ok = viewModel.crearSolicitud(id, destino, proposito, horas)
                        if (ok) navController.navigate(Routes.SOLICITUDES) {
                            popUpTo(Routes.CATALOGO)
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
                SolicitudDetalleScreen(
                    solicitud = viewModel.solicitud(id),
                    equipo = viewModel.solicitud(id)?.let { viewModel.equipo(it.equipoId) },
                    onBack = { navController.popBackStack() },
                    onCancelar = {
                        viewModel.cancelarSolicitud(id)
                    }
                )
            }
        }
    }
}

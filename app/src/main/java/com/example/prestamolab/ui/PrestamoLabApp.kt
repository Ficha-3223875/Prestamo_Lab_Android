package com.example.prestamolab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.prestamolab.data.local.PrestamoDatabase
import com.example.prestamolab.data.repository.RoomPrestamoRepository
import com.example.prestamolab.ui.screens.*
import com.example.prestamolab.viewmodel.PrestamoViewModel
import com.example.prestamolab.viewmodel.PrestamoViewModelFactory

private object Routes {
    const val CATALOGO = "catalogo"
    const val SOLICITUDES = "solicitudes"
    const val EQUIPO_DETALLE = "equipo_detalle/{equipoId}"
    const val SOLICITAR = "solicitar/{equipoId}"
    const val SOLICITUD_DETALLE = "solicitud_detalle/{solicitudId}"

    fun equipoDetalle(equipoId: Int) = "equipo_detalle/$equipoId"
    fun solicitar(equipoId: Int) = "solicitar/$equipoId"
    fun solicitudDetalle(solicitudId: Int) = "solicitud_detalle/$solicitudId"
}

@Composable
fun PrestamoLabApp() {
    val context = LocalContext.current
    val repository = remember {
        val db = PrestamoDatabase.getInstance(context)
        RoomPrestamoRepository(db.equipoDao(), db.solicitudDao())
    }
    val viewModel: PrestamoViewModel = viewModel(
        factory = PrestamoViewModelFactory(repository)
    )

    // Estado centralizado del ViewModel
    val state by viewModel.uiState.collectAsState()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == Routes.CATALOGO || currentRoute == Routes.SOLICITUDES) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.CATALOGO,
                        onClick = {
                            navController.navigate(Routes.CATALOGO) {
                                popUpTo(Routes.CATALOGO) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = "Equipos"
                            )
                        },
                        label = { Text("Equipos") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.SOLICITUDES,
                        onClick = {
                            navController.navigate(Routes.SOLICITUDES) {
                                popUpTo(Routes.CATALOGO) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = "Solicitudes"
                            )
                        },
                        label = { Text("Solicitudes") }
                    )
                }
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
                    onEquipoClick = { equipoId ->
                        navController.navigate(Routes.equipoDetalle(equipoId))
                    }
                )
            }

            composable(
                route = Routes.EQUIPO_DETALLE,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: return@composable
                val equipo = viewModel.equipo(equipoId)

                EquipoDetalleScreen(
                    equipo = equipo,
                    onBack = { navController.popBackStack() },
                    onSolicitar = { id ->
                        navController.navigate(Routes.solicitar(id))
                    }
                )
            }

            composable(
                route = Routes.SOLICITAR,
                arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: return@composable
                val equipo = viewModel.equipo(equipoId)

                SolicitarScreen(
                    equipo = equipo,
                    onBack = { navController.popBackStack() },
                    onSubmit = { destino, proposito, duracion, fotoUri ->
                        viewModel.crearSolicitud(
                            equipoId = equipoId,
                            destino = destino,
                            proposito = proposito,
                            duracionHoras = duracion,
                            fotoUri = fotoUri
                        ) { exito ->
                            if (exito) {
                                navController.navigate(Routes.CATALOGO) {
                                    popUpTo(Routes.CATALOGO) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            composable(Routes.SOLICITUDES) {
                MisSolicitudesScreen(
                    state = state,
                    onSolicitudClick = { solicitudId ->
                        navController.navigate(Routes.solicitudDetalle(solicitudId))
                    }
                )
            }

            composable(
                route = Routes.SOLICITUD_DETALLE,
                arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
            ) { backStackEntry ->
                val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: return@composable
                val solicitud = viewModel.solicitud(solicitudId)
                val equipo = solicitud?.let { viewModel.equipo(it.equipoId) }

                SolicitudDetalleScreen(
                    solicitud = solicitud,
                    equipo = equipo,
                    onBack = { navController.popBackStack() },
                    onCancelar = { id ->
                        viewModel.cancelarSolicitud(id) { exito ->
                            if (exito) navController.popBackStack()
                        }
                    },
                    onDevolver = { id, fotoDevolucionUri ->
                        viewModel.devolverSolicitud(id, fotoDevolucionUri) { exito ->
                            if (exito) navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}
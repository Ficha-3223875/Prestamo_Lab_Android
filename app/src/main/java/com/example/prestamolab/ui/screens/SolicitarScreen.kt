package com.example.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable  // ⬅️ IMPORT FALTANTE
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarScreen(
    equipo: Equipo?,
    state: PrestamoUiState,
    onBack: () -> Unit,
    onGuardar: (String, String, Int) -> Unit,
    onDismissMessage: () -> Unit
) {
    var destino by rememberSaveable { mutableStateOf("") }
    var proposito by rememberSaveable { mutableStateOf("") }
    var horasTexto by rememberSaveable { mutableStateOf("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar préstamo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        // ⬇️ Reestructurado: evita return@Scaffold que confunde al compilador
        if (equipo == null) {
            ErrorState(
                message = "No se encontró el equipo.",
                modifier = Modifier.padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(equipo.nombre, style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = destino,
                    onValueChange = { destino = it },
                    label = { Text("Ambiente o destino *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = proposito,
                    onValueChange = { if (it.length <= 200) proposito = it },
                    label = { Text("Propósito *") },
                    supportingText = { Text("${proposito.length}/200 caracteres") }, // Corregido: era 180 en texto pero 200 en lógica
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                OutlinedTextField(
                    value = horasTexto,
                    onValueChange = { horasTexto = it.filter(Char::isDigit).take(2) },
                    label = { Text("Duración estimada (horas) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        onGuardar(
                            destino,
                            proposito,
                            horasTexto.toIntOrNull() ?: 0
                        )
                    },
                    enabled = !state.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.guardando) "Guardando..." else "Guardar solicitud")
                }

                state.mensaje?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onDismissMessage) {
                        Text("Cerrar mensaje")
                    }
                }
            }
        }
    }
}
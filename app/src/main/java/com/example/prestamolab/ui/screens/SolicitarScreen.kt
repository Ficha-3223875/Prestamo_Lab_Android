package com.example.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.ui.components.CameraCaptureSection
import com.example.prestamolab.ui.components.autenticarConHuellaOPin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarScreen(
    equipo: Equipo?,
    onBack: () -> Unit,
    onSubmit: (ambiente: String, proposito: String, duracion: Int, fotoUriString: String?) -> Unit
) {
    val context = LocalContext.current

    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionHorasText by remember { mutableStateOf("2") }
    var fotoUriString by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorAutenticacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Préstamo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (equipo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Equipo no encontrado.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Equipo seleccionado:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = equipo.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            OutlinedTextField(
                value = ambiente,
                onValueChange = {
                    ambiente = it
                    showError = false
                },
                label = { Text("Ambiente / Salón de destino") },
                placeholder = { Text("Ej. Lab de Software 201") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && ambiente.isBlank(),
                singleLine = true
            )

            OutlinedTextField(
                value = proposito,
                onValueChange = {
                    proposito = it
                    showError = false
                },
                label = { Text("Propósito o uso") },
                placeholder = { Text("Ej. Práctica de la materia X") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && proposito.isBlank(),
                minLines = 3,
                maxLines = 5
            )

            OutlinedTextField(
                value = duracionHorasText,
                onValueChange = {
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        duracionHorasText = it
                        showError = false
                    }
                },
                label = { Text("Duración estimada (horas)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            CameraCaptureSection(
                fotoUriString = fotoUriString,
                onFotoCapturada = { uriString -> fotoUriString = uriString }
            )

            if (showError) {
                Text(
                    text = "Por favor completa los campos requeridos.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            errorAutenticacion?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val duracion = duracionHorasText.toIntOrNull() ?: 0
                    if (ambiente.isBlank() || proposito.isBlank() || duracion <= 0) {
                        showError = true
                    } else {
                        errorAutenticacion = null
                        autenticarConHuellaOPin(
                            context = context,
                            titulo = "Confirmar solicitud de préstamo",
                            subtitulo = "Verifica tu identidad para solicitar ${equipo.nombre}",
                            onExito = {
                                onSubmit(
                                    ambiente.trim(),
                                    proposito.trim(),
                                    duracion,
                                    fotoUriString
                                )
                            },
                            onError = { mensaje ->
                                errorAutenticacion = mensaje
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Enviar Solicitud", fontWeight = FontWeight.Bold)
            }
        }
    }
}
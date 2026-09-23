package com.example.prestamolab.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Devuelve una función que abre la cámara del sistema y, si la foto se toma,
 * entrega su URI mediante [onFoto]. Si se cancela, borra el archivo temporal.
 * No requiere permiso CAMERA (se usa la app de cámara del sistema).
 */
@Composable
fun recordarCapturaCamara(onFoto: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    var uriPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            uriPendiente?.let(onFoto)
        } else {
            uriPendiente?.let { uri ->
                runCatching { File(Uri.parse(uri).path ?: "").delete() }
            }
        }
        uriPendiente = null
    }

    return {
        val uri = crearUriFoto(context)
        uriPendiente = uri.toString()
        launcher.launch(uri)
    }
}

/** Crea un archivo .jpg temporal en filesDir/evidencias y devuelve su URI de FileProvider. */
fun crearUriFoto(context: Context): Uri {
    val carpeta = File(context.filesDir, "evidencias").apply { mkdirs() }
    val archivo = File.createTempFile("evidencia_", ".jpg", carpeta)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}

/** Vista previa de la foto tomada, redimensionada para evitar problemas de memoria. */
@Composable
fun EvidenciaPreview(uriTexto: String) {
    val resolver = LocalContext.current.contentResolver
    var bitmap by remember(uriTexto) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(uriTexto) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(uriTexto)
                val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
                var factor = 1
                val maxLado = 1024
                while (limites.outWidth / factor >= maxLado * 2 ||
                    limites.outHeight / factor >= maxLado * 2
                ) {
                    factor *= 2
                }
                val opciones = BitmapFactory.Options().apply { inSampleSize = factor }
                resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
            } catch (e: Exception) {
                null
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Vista previa de la evidencia",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}
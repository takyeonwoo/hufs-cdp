package com.scanpang.app.components.ar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.Image
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.scanpang.app.ui.theme.ScanPangColors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.awaitCancellation

private suspend fun Context.awaitProcessCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            {
                try {
                    continuation.resume(future.get())
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            },
            ContextCompat.getMainExecutor(this),
        )
    }

@Composable
fun ScanPangCameraPreview(
    lifecycleOwner: LifecycleOwner,
    isFrozen: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val previewView = remember(context) {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    var frozenBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(lifecycleOwner, previewView) {
        val provider = runCatching { context.awaitProcessCameraProvider() }.getOrNull()
            ?: return@LaunchedEffect

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
            )
        } catch (_: Exception) {
            return@LaunchedEffect
        }
        try {
            awaitCancellation()
        } finally {
            runCatching { provider.unbindAll() }
        }
    }

    // freeze 전환 시 현재 프레임 캡처 / 해제
    LaunchedEffect(isFrozen) {
        frozenBitmap = if (isFrozen) previewView.bitmap else null
    }

    Box(modifier = modifier) {
        // 카메라는 항상 살아 있음 — 언프리즈 시 즉시 live 복귀
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        // freeze 상태이고 캡처 성공 시 captured 프레임을 위에 덮음
        val bitmap = frozenBitmap
        if (isFrozen && bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun ArCameraBackdrop(
    isFrozen: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { cameraGranted = it },
    )
    LaunchedEffect(Unit) {
        if (!cameraGranted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraGranted) {
            ScanPangCameraPreview(
                lifecycleOwner = lifecycleOwner,
                isFrozen = isFrozen,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScanPangColors.Background),
            )
        }
        if (isFrozen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScanPangColors.ArFreezeTint),
            )
        }
    }
}

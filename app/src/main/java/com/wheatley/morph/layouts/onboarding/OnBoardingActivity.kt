package com.wheatley.morph.layouts.onboarding

import com.wheatley.morph.util.ui.ThemeManager
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.wheatley.morph.components.PrimaryButton
import com.wheatley.morph.model.UserPrefs
import com.wheatley.morph.ui.theme.MorphTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import com.wheatley.morph.components.CustomTextField
import com.wheatley.morph.layouts.DashboardActivity
import com.wheatley.morph.ui.theme.ApplySystemUiRegister

//TODO: Перейти на Voyadger

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.loadTheme(this)
        enableEdgeToEdge()
        setContent {
            MorphTheme {
                RegistrationScreen {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    MorphTheme {
        RegistrationScreen(onRegistered = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedCrossroadTargetStateParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrationScreen(onRegistered: () -> Unit) {

    ApplySystemUiRegister()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            photoUri = it
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = (currentStep.coerceIn(0, 4)) / 4f,
        animationSpec = tween(300),
        label = "animatedProgress"
    )

    Scaffold(
        content = { innerPadding ->
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00C6FF),
                                Color(0xFF0072FF)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 56.dp)
                ) {
                    LinearWavyProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                    )
                    when (currentStep) {
                        0 -> {
                            StepZero(
                                onNextStep = { currentStep++ },
                                innerPadding = innerPadding
                            )
                        }

                        1 -> {

                            StepOne(
                                onNextStep = { if (name.isNotBlank()) currentStep++ },
                                innerPadding = innerPadding,
                                enabled = name.isNotBlank(),
                                name = name,
                                onNameChange = { name = it }
                            )
                        }

                        2 -> {
                            StepTwo(
                                onNextStep = { currentStep++ },
                                onPreviousStep = { currentStep-- },
                                innerPadding = innerPadding,
                                photoUri = photoUri,
                                onPhotoSelected = { photoUri = it },
                                launcher = launcher
                            )
                        }

                        3 -> {
                            // Шаг 3: Подтверждение
                            Text("Шаг 3: Подтверждение")
                            Spacer(Modifier.height(8.dp))
                            Text("Имя: $name")
                            photoUri?.let {
                                Spacer(Modifier.height(8.dp))
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = null,
                                    modifier = Modifier.height(120.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(onClick = { currentStep-- }) {
                                    Text("Назад")
                                }
                                Button(
                                    onClick = { if (name.isNotBlank()) currentStep++ },
                                    enabled = name.isNotBlank()
                                ) {
                                    Text("Далее")
                                }
                            }

                        }

                        4 -> {
                            // Шаг 1: Ввод имени
                            Text("Шаг 1: Крутор")
                            Spacer(Modifier.height(16.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(onClick = {
                                    scope.launch {
                                        UserPrefs.saveUser(context, name, photoUri?.toString())
                                        onRegistered()
                                    }
                                }) {
                                    Text("Завершить")
                                }
                            }
                        }
                    }

                }
            }
        }
    )
}

@Composable
fun StepZero(
    onNextStep: () -> Unit,
    innerPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                imageVector = Icons.Outlined.Check, // замени на свой ресурс
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Лучшее время начать — сейчас!",
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.displaySmall,
            )
            Text(
                text = "Первый шаг — самый важный. Мы рядом, чтобы помочь.",
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
            )

            PrimaryButton(
                onClick = onNextStep,
                text = "Вперёд!"
            )

        }
    }
}

@Composable
fun StepOne(
    onNextStep: () -> Unit,
    innerPadding: PaddingValues,
    enabled: Boolean,
    name: String,
    onNameChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                imageVector = Icons.Outlined.Check, // замени на свой ресурс
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Как тебя зовут?",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.displaySmall,
            )

            Text(
                text = "Нам очень важно знать это.",
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
            )

            CustomTextField(
                modifier = Modifier
                    .padding(bottom = 32.dp),
                value = name,
                onValueChange = onNameChange,
                label = "Имя"
            )

            PrimaryButton(
                onClick = onNextStep,
                text = "Вперёд!",
                enabled = enabled
            )

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StepTwo(
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    innerPadding: PaddingValues,
    photoUri: Uri?,
    onPhotoSelected: (Uri) -> Unit,
    launcher: ManagedActivityResultLauncher<String, Uri?>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Добавь фото профиля",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Это поможет нам сделать всё чуть уютнее 🙂",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Открыть галерею")
            }

            Spacer(modifier = Modifier.height(16.dp))

            photoUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(MaterialShapes.Cookie12Sided.toShape()),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onPreviousStep, modifier = Modifier.weight(1f)) {
                    Text("Назад")
                }
                Button(
                    onClick = onNextStep,
                    enabled = photoUri != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Далее")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
package fr.jg.sosalert.ui.view

import android.Manifest
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.jg.sosalert.R
import fr.jg.sosalert.ui.AppViewModelProvider
import fr.jg.sosalert.ui.navigation.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val topBarTitle = R.string.navigation_home_top_bar_title
    override val bottomBarTitle = R.string.navigation_home_bottom_bar_title
    override val bottomBarIcon = Icons.Default.Home
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val isPressAndHoldToSendAlert by viewModel.isPressAndHoldToSendAlert.collectAsState(true)
    val pressDuration = if (isPressAndHoldToSendAlert) 3 else 0
    var showPermissionRequiredMessage by rememberSaveable { mutableStateOf(false) }
    var showAlertSent by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    val smsPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                showPermissionRequiredMessage = false
                viewModel.sendAlert()
            } else {
                showPermissionRequiredMessage = true
            }
        }

    LaunchedEffect(viewModel.isAlertSent) {
        if (viewModel.isAlertSent) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(
                        R.string.home_alert_sent,
                        viewModel.totalSentAlerts
                    )
                )
                showAlertSent = true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isPressAndHoldToSendAlert) {
                    stringResource(
                        R.string.home_press_and_hold_to_send_alert,
                        pressDuration
                    )
                } else {
                    stringResource(
                        R.string.home_press_to_send_alert,
                    )
                },
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            AlertButton(pressDuration, shouldAnimate = {
                !showPermissionRequiredMessage
            }, onButtonPressed = {
                if (viewModel.hasSendSmsPermission()) {
                    viewModel.sendAlert()
                } else {
                    showPermissionRequiredMessage = true
                    smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                }
            })
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showPermissionRequiredMessage) {
        PermissionRequiredDialog(
            onConfirmation = {
                showPermissionRequiredMessage = false
            },
            content = R.string.permission_required_sms_content
        )
    }
}

@Composable
fun AlertButton(
    pressDuration: Int,
    onButtonPressed: () -> Unit,
    shouldAnimate: () -> Boolean,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.alert_icon))
    var isPlaying by remember { mutableStateOf(false) }
    val progress by remember { mutableFloatStateOf(0f) }

    var pressStartTime by remember { mutableLongStateOf(0) }
    var timeRemaining by remember { mutableIntStateOf(3) }
    var isPressed by remember { mutableStateOf(false) }

    val animationState = animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        iterations = 2,
        clipSpec = LottieClipSpec.Progress(0f, 1f)
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            if (pressDuration > 0) {
                for (i in pressDuration downTo 1) {
                    timeRemaining = i
                    delay(1000L)
                }
            }
            isPressed = false
            onButtonPressed()
            isPlaying = shouldAnimate()
        } else {
            timeRemaining = 3
        }
    }

    LaunchedEffect(animationState.isAtEnd) {
        if (animationState.isAtEnd) {
            isPlaying = false
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { if (isPlaying) animationState.progress else progress },
        modifier = Modifier
            .size(300.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressStartTime = SystemClock.uptimeMillis()
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        pressStartTime = 0
                    }
                )
            }
    )

    if (isPressed && pressDuration > 0) {
        Text(
            text = timeRemaining.toString(),
            style = MaterialTheme.typography.headlineLarge,
        )
    } else {
        Spacer(modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun PermissionRequiredDialog(
    onConfirmation: () -> Unit,
    content: Int,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(R.string.permission_required_title)) },
        text = {
            Text(text = stringResource(content))
        },
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
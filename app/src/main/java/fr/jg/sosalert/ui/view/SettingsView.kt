package fr.jg.sosalert.ui.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.jg.sosalert.R
import fr.jg.sosalert.ui.AppViewModelProvider
import fr.jg.sosalert.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val topBarTitle = R.string.navigation_settings_top_bar_title
    override val bottomBarTitle = R.string.navigation_settings_bottom_bar_title
    override val bottomBarIcon = Icons.Default.Settings
}

@Composable
fun Settings(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollState = rememberScrollState()
    val defaultMessageContent = stringResource(R.string.settings_default_message_content)
    val isPressAndHold by viewModel.isPressAndHold.collectAsState()
    val isSendLocation by viewModel.isSendLocation.collectAsState()
    var messageContent by rememberSaveable {
        mutableStateOf(
            viewModel.messageContent ?: defaultMessageContent
        )
    }
    var showPermissionRequiredMessage by rememberSaveable { mutableStateOf(false) }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.onLocationPermissionGranted()
            } else {
                showPermissionRequiredMessage = true
            }
        }

    LaunchedEffect(messageContent) {
        kotlinx.coroutines.delay(500)
        viewModel.updateMessageContent(messageContent)
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.settings_press_hold_to_send_alert),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isPressAndHold,
                onCheckedChange = {
                    viewModel.updateIsPressAndHold(it)
                },
                modifier = Modifier.padding(4.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.settings_include_location),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isSendLocation,
                onCheckedChange = {
                    showPermissionRequiredMessage = false
                    if (!it) {
                        viewModel.updateIsSendLocation(false)
                    } else {
                        if (viewModel.hasLocationPermission()) {
                            viewModel.updateIsSendLocation(true)
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                },
                modifier = Modifier.padding(4.dp),
            )
        }

        Text(text = stringResource(R.string.settings_message_content))
        TextField(
            value = messageContent ?: "",
            minLines = 3,
            maxLines = 5,
            onValueChange = { messageContent = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && messageContent != null) {
                        viewModel.updateMessageContent(messageContent ?: "")
                    }
                },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        if (showPermissionRequiredMessage) {
            PermissionRequiredDialog(
                onConfirmation = {
                    showPermissionRequiredMessage = false
                },
                content = R.string.permission_required_location_content
            )
        }
    }
}
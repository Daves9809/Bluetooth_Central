package com.example.ble_central.ui.Main

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ble_central.Components.Advertisement.AdvertisementsList
import com.example.ble_central.Components.BluetoothDisabled
import com.example.ble_central.Components.BluetoothPermissionsNotAvailable
import com.example.ble_central.Components.BluetoothPermissionsNotGranted
import com.example.ble_central.Utils.ScanStatus
import com.example.ble_central.navigation.Destination
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.juul.kable.Advertisement

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    isBtenabled: Boolean,
    navController: NavController,
    enableBluetooth: () -> Unit,
    openAppDetails: () -> Unit,
    viewModel: MainViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AppBar(viewModel = viewModel, isBluetoothEnabled = isBtenabled)

        Box(
            Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            ProvideTextStyle(
                TextStyle(color = contentColorFor(backgroundColor = MaterialTheme.colors.background))
            ) {
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                } else {
                    listOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
                val permissionsState = rememberMultiplePermissionsState(permissions)

                var didAskForPermission by remember { mutableStateOf(false) }
                if (!didAskForPermission) {
                    didAskForPermission = true
                    SideEffect {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }

                if (permissionsState.allPermissionsGranted) {
                    if (isBtenabled) {
                        val advertisements = viewModel.advertisements.collectAsState().value
                        AdvertisementsList(advertisements) {
                            onAdvertisementClicked(
                                viewModel,
                                navController, it
                            )
                        }
                    } else {
                        BluetoothDisabled(enableBluetooth)
                    }
                } else {
                    if (permissionsState.shouldShowRationale) {
                        BluetoothPermissionsNotGranted(permissionsState)
                    } else {
                        BluetoothPermissionsNotAvailable(openAppDetails)
                    }
                }
            }

            StatusSnackbar(viewModel)
        }

    }
}

@Composable
fun AppBar(viewModel: MainViewModel, isBluetoothEnabled: Boolean) {
    val status = viewModel.status.collectAsState().value

    TopAppBar(
        title = {
            Text("Bluetooth Central")
        },
        actions = {
            if (isBluetoothEnabled) {
                if (status !is ScanStatus.Scanning) {
                    IconButton(onClick = viewModel::start) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
                IconButton(onClick = viewModel::clear) {
                    Icon(Icons.Filled.Delete, contentDescription = "Clear")
                }
            }
        }
    )
}

@Composable
fun BoxScope.StatusSnackbar(viewModel: MainViewModel) {
    val status = viewModel.status.collectAsState().value

    if (status !is ScanStatus.Stopped) {
        val text = when (status) {
            is ScanStatus.Scanning -> "Scanning"
            is ScanStatus.Stopped -> "Idle"
            is ScanStatus.Failed -> "Error: ${status.message}"
        }
        Snackbar(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {
            Text(text, style = MaterialTheme.typography.body1)
        }
    }
}

private fun onAdvertisementClicked(
    viewModel: MainViewModel,
    navController: NavController,
    advertisement: Advertisement
) {
    viewModel.stop()
    /*val intent = SensorActivityIntent(
        context = this@ScanActivity,
        macAddress = advertisement.address
    )*/
    Log.d("TAG", "Should new activity start")
    navController.navigate("${Destination.DeviceScreen.name}/${advertisement.address}")
//    startActivity(intent)
}
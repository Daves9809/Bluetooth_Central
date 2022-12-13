package com.example.ble_central.navigation

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ble_central.Utils.isBluetoothEnabled
import com.example.ble_central.ui.DeviceScreen
import com.example.ble_central.ui.DeviceViewModel
import com.example.ble_central.ui.Main.MainScreen
import com.example.ble_central.ui.Main.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.MainScreen.name,
    enableBluetooth: () -> Unit,
    openAppDetails: () -> Unit,
) {
    val isBTEnabled = isBluetoothEnabled.collectAsState(initial = BluetoothAdapter.getDefaultAdapter().isEnabled)
        .value

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Destination.MainScreen.name){
            val mainViewModel = hiltViewModel<MainViewModel>()
            MainScreen(
                viewModel = mainViewModel,
                isBtenabled = isBTEnabled,
                navController = navController,
                enableBluetooth = { enableBluetooth() },
                openAppDetails = { openAppDetails })
        }
        composable("${Destination.DeviceScreen.name}/{macAdress}"){ backStackEntry ->
            backStackEntry.arguments?.getString("macAdress")?.let { macAdress ->
                val deviceViewModel = hiltViewModel<DeviceViewModel>()
                DeviceScreen(deviceViewModel)
            }
        }
    }
}
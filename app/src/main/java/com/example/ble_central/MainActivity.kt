package com.example.ble_central

import android.bluetooth.BluetoothAdapter
import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.ble_central.Utils.enableBluetooth
import com.example.ble_central.Utils.isBluetoothEnabled
import com.example.ble_central.Utils.openAppDetails
import com.example.ble_central.navigation.MyAppNavHost
import com.example.ble_central.ui.theme.BLE_CentralTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLE_CentralTheme {
                /*val isBTEnabled =
                    isBluetoothEnabled.collectAsState(initial = BluetoothAdapter.getDefaultAdapter().isEnabled)
                        .value*/
                MyAppNavHost(
                    enableBluetooth = { enableBluetooth() },
                    openAppDetails = { openAppDetails() })
            }
        }
    }

}

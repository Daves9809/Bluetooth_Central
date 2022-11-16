package com.example.ble_central

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.ble_central.Utils.enableBluetooth
import com.example.ble_central.Utils.openAppDetails
import com.example.ble_central.ui.MainScreen
import com.example.ble_central.ui.MainViewModel
import com.example.ble_central.ui.theme.BLE_CentralTheme
import com.juul.tuulbox.coroutines.flow.broadcastReceiverFlow
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {

    private val isBluetoothEnabled = broadcastReceiverFlow(IntentFilter(ACTION_STATE_CHANGED))
        .map { intent -> intent.getIsBluetoothEnabled() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLE_CentralTheme {
                val isBTEnabled = isBluetoothEnabled.collectAsState(initial = BluetoothAdapter.getDefaultAdapter().isEnabled)
                    .value
                MainScreen(isBTEnabled, { enableBluetooth() },{openAppDetails()})
            }
        }
    }

}

private fun Intent.getIsBluetoothEnabled(): Boolean = when (getIntExtra(
    BluetoothAdapter.EXTRA_STATE,
    BluetoothAdapter.ERROR
)) {
    BluetoothAdapter.STATE_TURNING_ON, BluetoothAdapter.STATE_ON, BluetoothAdapter.STATE_CONNECTING, BluetoothAdapter.STATE_CONNECTED, BluetoothAdapter.STATE_DISCONNECTING, BluetoothAdapter.STATE_DISCONNECTED -> true
    else -> false // STATE_TURNING_OFF, STATE_OFF
}
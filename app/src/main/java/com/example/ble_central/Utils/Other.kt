package com.example.ble_central.Utils

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import com.juul.tuulbox.coroutines.flow.broadcastReceiverFlow
import kotlinx.coroutines.flow.map

val isBluetoothEnabled = broadcastReceiverFlow(IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    .map { intent -> intent.getIsBluetoothEnabled() }

private fun Intent.getIsBluetoothEnabled(): Boolean = when (getIntExtra(
    BluetoothAdapter.EXTRA_STATE,
    BluetoothAdapter.ERROR
)) {
    BluetoothAdapter.STATE_TURNING_ON, BluetoothAdapter.STATE_ON, BluetoothAdapter.STATE_CONNECTING, BluetoothAdapter.STATE_CONNECTED, BluetoothAdapter.STATE_DISCONNECTING, BluetoothAdapter.STATE_DISCONNECTED -> true
    else -> false // STATE_TURNING_OFF, STATE_OFF
}
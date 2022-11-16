package com.example.ble_central.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConnectState(connectState: Boolean) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (connectState) OutlinedTextField(
            value = "Connected",
            onValueChange = {},
            enabled = false,
            label = {
                Text(text = "State")
            })
        else OutlinedTextField(value = "Disconnected",
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "State")
            })
    }
}

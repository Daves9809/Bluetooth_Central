package com.example.ble_central.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.logging.Handler


@Composable
fun AutoScan(checked: Boolean, isScanningPossible: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Scan & autoconnect")
        Switch(checked = checked, onCheckedChange = { onChange(it) }, enabled = isScanningPossible)

    }
}

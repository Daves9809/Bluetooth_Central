package com.example.ble_central.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReadableCharacteristic() {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(top = 10.dp, end = 10.dp)) {
            Text(text = "Read", maxLines = 1)
        }
        OutlinedTextField(
            value = "None",
            onValueChange = {},
            enabled = false,
            label = {
                Text(text = "Readable Characteristic")
            }
        )
    }
}
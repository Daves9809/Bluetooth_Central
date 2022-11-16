package com.example.ble_central.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LogsComponent() {
    Box(modifier = Modifier.border(BorderStroke(2.dp, Color.Black)).fillMaxSize()){
        Button(onClick = { /*TODO*/ }, modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) {
            Text(text = "Clear")
        }
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "Logs:")
            Text(text = "Output Text")
        }
    }
}

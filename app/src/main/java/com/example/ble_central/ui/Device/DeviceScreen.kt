package com.example.ble_central.ui

import android.bluetooth.BluetoothGattCharacteristic
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.juul.kable.*


@Composable
fun DeviceScreen(
    navController: NavController,
    macAdress: String,
    deviceViewModel: DeviceViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(text = "Bluetooth Central") })

        val connectionState = deviceViewModel.connectionState.collectAsState()
        val peripheralName = deviceViewModel.peripheralName.collectAsState()
        val peripheralServices = deviceViewModel.peripheralServices.collectAsState()

        DeviceNameAndState(peripheralName.value, connectionState.value.name)
        DeviceServices(peripheralServices.value)
    }
}

@Composable
fun DeviceServices(peripheralServices: List<DiscoveredService>?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        peripheralServices?.let { services ->
            items(services) { service ->
                ServiceItem(service)
            }
        }
    }
}

@Composable
fun ServiceItem(service: DiscoveredService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(text = "Service Uuid:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "${service.serviceUuid}")
            Characteristics(service.characteristics)
        }
    }

}

@Composable
fun Characteristics(characteristics: List<DiscoveredCharacteristic>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(100.dp)
    ) {
        items(characteristics) { characteristic ->
            CharacteristicItem(characteristic)
        }
    }
}

@Composable
fun CharacteristicItem(characteristic: DiscoveredCharacteristic) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Characteristic Uuid:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "${characteristic.characteristicUuid}")
            if (characteristic.descriptors.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    characteristic.descriptors.forEach { descriptor ->
                        Text(text = "Descriptors:", fontWeight = FontWeight.Bold)
                        Text(text = "${descriptor}")
                        Text(text = "Properties:", fontWeight = FontWeight.Bold)
                        PropertiesOfCharacteristic(characteristic.properties)
                    }
                }
            }
        }
    }

}

@Composable
fun PropertiesOfCharacteristic(
    properties: Characteristic.Properties
) {
    val isCharacteristicRead = properties.read
    val isCharacteristicWrite = properties.write
    val isCharacteristicIndicate = properties.indicate
    Column() {
        if (isCharacteristicRead)
            Text(text = "Read")
        if (isCharacteristicWrite)
            Text(text = "Write")
        if (isCharacteristicIndicate)
            Text(text = "Indicate")
    }
}

@Composable
fun DeviceNameAndState(deviceName: String, stateConnection: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Device name : $deviceName",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 1
            )
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(value = stateConnection, onValueChange = {}, label = {
                Text(text = "State Connection")
            })
        }
    }

}

fun isCharacteristicWritable(pChar: BluetoothGattCharacteristic): Boolean {
    return pChar.properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
}
package com.example.ble_central.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.juul.kable.*


@Composable
fun DeviceScreen(
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
                        Text(text = "$descriptor")
                    }
                }
            }
            Text(text = "Properties:", fontWeight = FontWeight.Bold)
            PropertiesOfCharacteristic(characteristic = characteristic)
        }
    }

}

@Composable
fun PropertiesOfCharacteristic(
    characteristic: DiscoveredCharacteristic,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val indicatedData = viewModel.indicatedData.collectAsState()
    val readData = viewModel.readData.collectAsState()

    val isCharacteristicRead = characteristic.properties.read
    val isCharacteristicWrite = characteristic.properties.write
    val isCharacteristicIndicate = characteristic.properties.indicate

    Column() {
        if (isCharacteristicRead) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = returnRead(readData.value,characteristic),
                    enabled = false,
                    onValueChange = {},
                    label = {
                        Text(text = "Read")
                    })
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.readData(characteristic) }) {
                    Text(text = "Read")
                }
            }
        }
        if (isCharacteristicWrite) {
            var textWrite by remember {
                mutableStateOf("")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = textWrite,
                    onValueChange = { textWrite = it },
                    label = {
                        Text(text = "Write")
                    })
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.writeData(characteristic, textWrite) }) {
                    Text(text = "Write")
                }
            }
        }
        if (isCharacteristicIndicate) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = indicatedData.value,
                    onValueChange = { },
                    enabled = false,
                    label = {
                        Text(text = "Indicate")
                    })
                Button(onClick = { viewModel.indicate(characteristic) }) {
                    Text(text = "Indicate")
                }
            }
        }
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

private fun returnRead(
    readData: Pair<String, String>,
    characteristic: DiscoveredCharacteristic
): String = if (readData.first == characteristic.characteristicUuid.toString())
    readData.second
else
    "Read"
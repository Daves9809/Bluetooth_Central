package com.example.ble_central

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.os.*
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.ble_central.Components.*
import com.example.ble_central.Utils.Constants
import com.example.ble_central.ui.theme.BLE_CentralTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.juul.kable.Advertisement
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothLeScanner: BluetoothLeScanner
    lateinit var advertisement: Advertisement
    //lateinit var scanFilter: ScanFilter
    var scanCallback: ScanCallback? = null
    var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        handler = Handler(Looper.myLooper()!!)
/*        scanFilter =
            ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(Constants.SERVICE_UUID)))
                .build()*/
        setContent {
            BLE_CentralTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(bluetoothLeScanner, scanCallback)
                }
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    bluetoothLeScanner: BluetoothLeScanner,
    scanCallback: ScanCallback?
) {
    //permisions
    val locationPermission = rememberPermissionState(permission = Constants.LOCATION_FINE_PERM)
    val isLocationPermissionRequired by remember {
        mutableStateOf(Build.VERSION.SDK_INT in Build.VERSION_CODES.N..Build.VERSION_CODES.R)
    }

    var mCheckedState by remember {
        mutableStateOf(false)
    }
    var connectState by remember {
        mutableStateOf(false)
    }
    var textLog by remember {
        mutableStateOf("")
    }
    var isScanningPossible by remember {
        mutableStateOf(false)
    }
    
    if (isLocationPermissionRequired && !locationPermission.hasPermission) {
        if (locationPermission.shouldShowRationale) {
            val alertDialogBuilder = AlertDialog.Builder(LocalContext.current)
            with(alertDialogBuilder) {
                setTitle("Need location access for Bluetooth Scanning")
                setMessage("Grant location access to discover nearby Bluetooth Scanning")
                setPositiveButton("Okay") { _, _ ->
                    locationPermission.launchPermissionRequest()
                }
            }
            alertDialogBuilder.create().show()
        } else {
            LaunchedEffect(key1 = true) {
                locationPermission.launchPermissionRequest()
            }
        }
    } else {
        isScanningPossible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AutoScan(mCheckedState, isScanningPossible) { mCheckedState = it }
        ConnectState(connectState)
        ReadableCharacteristic()
        WritableCharacteristic()
        LogsComponent()
    }
}

/*fun safeStartBleScan(
    isScanning: Boolean,
    scanFilter: ScanFilter,
    handler: Handler,
    scanCallback: ScanCallback?,
    bluetoothLeScanner: BluetoothLeScanner
) {
    if (isScanning) {
        appendLog("Already scanning")
        return
    }

    val serviceFilter = scanFilter.serviceUuid?.uuid.toString()
    appendLog("Starting BLE Scan, filter: $")

}*/


/*fun appendLog(message: String) {
    Log.d("appendLog", message)
    val strTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    textLog += "\n$strTime $message"




// scroll after delay, because textView has to be updated first
    Handler().postDelayed({
        scrollViewLog.fullScroll(View.FOCUS_DOWN)
    }, 16)*//*


}
*/


package com.example.ble_central.ui.Main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble_central.Utils.Constants
import com.example.ble_central.Utils.ScanStatus
import com.example.ble_central.Utils.cancelChildren
import com.example.ble_central.Utils.childScope
import com.juul.kable.Advertisement
import com.juul.kable.Scanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val scanner = Scanner{
        filters= listOf(

        )
    }
    private val scanScope = viewModelScope.childScope()
    private val found = hashMapOf<String, Advertisement>()

    private val _status = MutableStateFlow<ScanStatus>(ScanStatus.Stopped)
    val status = _status.asStateFlow()

    private val _advertisements = MutableStateFlow<List<Advertisement>>(emptyList())
    val advertisements = _advertisements.asStateFlow()

    fun start() {
        if (_status.value == ScanStatus.Scanning) return //Scan already in progress
        _status.value = ScanStatus.Scanning

        scanScope.launch {
            withTimeoutOrNull(Constants.SCAN_DURATION_MILLIS) {
                scanner
                    .advertisements
                    .catch { exception ->
                        _status.value = ScanStatus.Failed(exception.message ?: "Unkown error")
                    }
                    .onCompletion { cause ->
                        if (cause == null || cause is CancellationException) _status.value =
                            ScanStatus.Stopped
                    }
                    //.filter { it.name.let { it?.startsWith("G") != null } }
                    //.filter { it.rssi in -50..0 }
                    //.filter { it.uuids.isNotEmpty()}
                    //.filter { it.uuids.contains(uuid5Of())}
                    .collect{ advertisement ->
                        found[advertisement.address] = advertisement
                        _advertisements.value = found.values.toList()
                    }
            }
        }
    }

    fun stop(){
        scanScope.cancelChildren()
    }

    fun clear(){
        stop()
        _advertisements.value = emptyList()
    }
}


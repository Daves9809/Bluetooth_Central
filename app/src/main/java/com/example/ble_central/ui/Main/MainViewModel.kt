package com.example.ble_central.ui.Main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble_central.Utils.*
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
    private val scanner = Scanner {
        filters = listOf(

        )
    }
    private val scanScope = viewModelScope.childScope()
    private val found = hashMapOf<String, Advertisement>()

    private val _status = MutableStateFlow<ScanStatus>(ScanStatus.Stopped)
    val status = _status.asStateFlow()

    private val _advertisements = MutableStateFlow<List<Advertisement>>(emptyList())
    val advertisements = _advertisements.asStateFlow()

    private val _showAdvertisements = MutableStateFlow<List<Advertisement>>(emptyList())
    val showAdvertisement = _showAdvertisements.asStateFlow()

    private val _filterType = MutableStateFlow<FilterType>(FilterType.NONE)

    fun start() {
        if (_status.value == ScanStatus.Scanning) return //Scan already in progress
        _status.value = ScanStatus.Scanning


        scanScope.launch {
            withTimeoutOrNull(Constants.SCAN_DURATION_MILLIS) {
                scanner
                    .advertisements
                    .catch { exception ->
                        _status.value = ScanStatus.Failed(exception.message ?: "Unknown error")
                    }
                    .onCompletion { cause ->
                        if (cause == null || cause is CancellationException) _status.value =
                            ScanStatus.Stopped
                    }
                    //.filter { filtering(it) }
                    .collect { advertisement ->
                        found[advertisement.address] = advertisement
                        _advertisements.value = found.values.toList()
                        _showAdvertisements.value = found.values.toList().filter { filtering(it) }
                    }
            }
        }
    }

    private fun filtering( advertisement: Advertisement): Boolean {
        if (_filterType.value == FilterType.CLOSE_AREA){
            return advertisement.rssi in -45..0
        }
        else{
            return true
        }

    }
    fun setFilteringType(filterType: FilterType){
        _filterType.value = filterType
        _showAdvertisements.value = _advertisements.value.filter { filtering(it) }
    }

    fun stop() {
        scanScope.cancelChildren()
    }

    fun clear() {
        stop()
        _advertisements.value = emptyList()
        _showAdvertisements.value = emptyList()
        found.clear()
    }
}


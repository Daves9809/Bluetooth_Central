package com.example.ble_central.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble_central.Utils.Connection
import com.example.ble_central.Utils.Constants.DISCONNECT_TIMEOUT
import com.juul.kable.*
import com.juul.tuulbox.logging.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.math.pow


@HiltViewModel
class DeviceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val macAdress: String = checkNotNull(savedStateHandle["macAdress"])
    private val _peripheral = viewModelScope.peripheral(identifier = macAdress)
    private val connectionAttempt = AtomicInteger()

    private val _connectionState = MutableStateFlow(Connection.Connecting)
    val connectionState = _connectionState.asStateFlow()

    private val _peripheralName = MutableStateFlow("")
    val peripheralName = _peripheralName.asStateFlow()

    private val _peripheralServices = MutableStateFlow(_peripheral.services)
    val peripheralServices = _peripheralServices.asStateFlow()

    private val _indicatedData = MutableStateFlow("")
    val indicatedData = _indicatedData.asStateFlow()

    private val _readData = MutableStateFlow(Pair<String,String>("",""))
    val readData = _readData.asStateFlow()

    init {
        viewModelScope.enableAutoReconnect()
        viewModelScope.connect()
    }

    private fun CoroutineScope.enableAutoReconnect() {
        _peripheral.state
            .filter { it is State.Disconnected }
            .onEach {
                val timeMillis =
                    backoff(
                        base = 500L,
                        multiplier = 2f,
                        retry = connectionAttempt.getAndIncrement()
                    )
                Log.info { "Waiting $timeMillis ms to reconnect..." }
                delay(timeMillis)
                connect()
            }
            .launchIn(this)

    }

    private fun CoroutineScope.connect() {
        _connectionState.value = Connection.Connecting
        connectionAttempt.incrementAndGet()
        launch {
            Log.debug { "connect" }
            try {
                _peripheral.connect()
                connectionAttempt.set(0)
                _connectionState.value = Connection.Connected
                _peripheralName.value = _peripheral.name ?: "Unnamed peripheral"
                _peripheralServices.value = _peripheral.services
            } catch (e: ConnectionLostException) {
                Log.warn(e) { "Connection attempt failed" }
            }
        }
    }

    fun writeData(characteristic: DiscoveredCharacteristic, string: String){
        viewModelScope.launch {
            _peripheral.write(characteristic,string.toByteArray())
            Log.debug { "Data has been send" }
        }
    }

    fun indicate(characteristic: DiscoveredCharacteristic){
        viewModelScope.launch {
            _peripheral.observe(characteristic).collect{ data ->
                _indicatedData.value = String(data)
            }
        }
    }

    fun readData(characteristic: DiscoveredCharacteristic){
        viewModelScope.launch {
            val data = _peripheral.read(characteristic)
            _readData.value = Pair(characteristic.characteristicUuid.toString(),String(data))
        }
    }

    override fun onCleared() {
        GlobalScope.launch {
            withTimeoutOrNull(DISCONNECT_TIMEOUT) {
                _peripheral.disconnect()
            }
        }
    }
}


private fun backoff(
    base: Long,
    multiplier: Float,
    retry: Int,
): Long = (base * multiplier.pow(retry - 1)).toLong()
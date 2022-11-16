package com.example.ble_central.Components.Advertisement

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.juul.kable.Advertisement

@Composable
fun AdvertisementsList(
    advertisements: List<Advertisement>,
    onRowClick: (Advertisement) -> Unit
) {
    LazyColumn {
        items(advertisements.size) { index ->
            val advertisement = advertisements[index]
            AdvertisementRow(advertisement) { onRowClick(advertisement) }
        }
    }
}
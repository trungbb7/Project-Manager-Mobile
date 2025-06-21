package com.example.projectmanagerapp.ui.main.screens

// MapPickerScreen.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projectmanagerapp.ui.main.CardLocation
import com.example.projectmanagerapp.viewmodels.MapPickerViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    navController: NavController,
     viewModel: MapPickerViewModel
) {
    val hcmCity = LatLng(10.7769, 106.7009)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(hcmCity, 15f)
    }



    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.selectedLatLng) {
        uiState.selectedLatLng?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 17f),
                1000
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chọn một vị trí") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    uiState.selectedLatLng?.let { latLng  ->
                        val result = CardLocation(
                            latitude = latLng.latitude,
                            longitude = latLng.longitude,
                            address = uiState.selectedAddress ?: "",
                            placeName = uiState.selectedPlaceName
                        )
                        // Trả kết quả về màn hình trước
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("picked_location", result)
                        navController.popBackStack()
                    }
                },
                enabled = uiState.selectedLatLng != null
            ) {
                Icon(Icons.Default.Check, null)
                Text("Chọn vị trí này")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.onMapClick(latLng)
                }
            ) {
                // Hiển thị marker ở vị trí đã chọn
                uiState.selectedLatLng?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Vị trí đã chọn",
                        snippet = uiState.selectedAddress ?: "Đang tìm địa chỉ..."
                    )
                }
            }
            // Lớp tìm kiếm ở trên cùng
            Column(modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(8.dp)
                .fillMaxWidth()
            ) {
                // Ô tìm kiếm
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                    placeholder = { Text("Tìm kiếm địa điểm...") },
                )

                // Danh sách kết quả gợi ý
                if (uiState.autocompletePredictions.isNotEmpty()) {
                    LazyColumn (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        items(uiState.autocompletePredictions) { prediction ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onPredictionSelected(prediction) }
                                    .padding(16.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(prediction.getPrimaryText(null).toString(), fontWeight = FontWeight.Bold)
                                    Text(prediction.getSecondaryText(null).toString(), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
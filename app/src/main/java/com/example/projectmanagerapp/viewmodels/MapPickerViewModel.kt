package com.example.projectmanagerapp.viewmodels

// viewmodels/MapPickerViewModel.kt
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.utils.getAddressFromCoordinates
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

data class MapPickerUiState(
    val searchQuery: String = "",
    val selectedLatLng: LatLng? = null,
    val selectedPlaceName: String? = null,
    val selectedAddress: String? = null,
    val isSearching: Boolean = false,
    val autocompletePredictions: List<AutocompletePrediction> = emptyList()
)

@OptIn(FlowPreview::class)
class MapPickerViewModel(
    @ApplicationContext private val context: Context,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapPickerUiState())
    val uiState = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.length < 2) {
                        _uiState.value = _uiState.value.copy(autocompletePredictions = emptyList())
                    } else {
                        Log.d("MapPickerViewModel", "Query: $query")
                        findAutocompletePredictions(query)
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        queryFlow.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    private fun findAutocompletePredictions(query: String) {
        _uiState.value = _uiState.value.copy(isSearching = true)
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setCountries("VN")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                Log.d("MapPickerViewModel", "Response: $response")
                _uiState.value = _uiState.value.copy(
                    autocompletePredictions = response.autocompletePredictions,
                    isSearching = false
                )
            }
            .addOnFailureListener {
                Log.d("MapPickerViewModel", "Error finding autocomplete predictions: $it")
                _uiState.value = _uiState.value.copy(isSearching = false)
                // Xử lý lỗi
            }
    }

    fun onPredictionSelected(prediction: AutocompletePrediction) {
        _uiState.value = _uiState.value.copy(
            searchQuery = prediction.getPrimaryText(null).toString(),
            autocompletePredictions = emptyList()
        )

        fetchPlaceDetails(prediction.placeId)
    }

    private fun fetchPlaceDetails(placeId: String) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                Log.d("MapPickerViewModel", "Place details response: $response")
                val place = response.place
                _uiState.value = _uiState.value.copy(
                    selectedLatLng = place.latLng,
                    selectedPlaceName = place.name,
                    selectedAddress = place.address
                )
            }
            .addOnFailureListener {
                Log.d("MapPickerViewModel", "Error fetching place details: $it")
            }
    }

    fun onMapClick(latLng: LatLng) {
        _uiState.value = _uiState.value.copy(
            selectedLatLng = latLng,
            selectedAddress = null
        )

        viewModelScope.launch {
            val addressObject = getAddressFromCoordinates(context, latLng.latitude, latLng.longitude)
            val formattedAddress = if (addressObject != null) {
                addressObject.getAddressLine(0)
            } else {
                "Không tìm thấy địa chỉ"
            }

            _uiState.value = _uiState.value.copy(
                selectedAddress = formattedAddress,
            )
        }
    }
}
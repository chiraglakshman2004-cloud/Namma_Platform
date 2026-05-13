package com.example.namma_platform.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.namma_platform.data.TrainRepository
import com.example.namma_platform.model.Station
import com.example.namma_platform.model.Train
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrainViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val repository = TrainRepository(application)
    private var tts: TextToSpeech? = null

    private val _stations = MutableStateFlow<List<Station>>(emptyList())
    val stations: StateFlow<List<Station>> = _stations.asStateFlow()

    private val _selectedStation = MutableStateFlow<Station?>(null)
    val selectedStation: StateFlow<Station?> = _selectedStation.asStateFlow()

    private val _trains = MutableStateFlow<List<Train>>(emptyList())
    val trains: StateFlow<List<Train>> = _trains.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSpeaking = MutableStateFlow<String?>(null) // Train ID being announced
    val isSpeaking: StateFlow<String?> = _isSpeaking.asStateFlow()

    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    init {
        tts = TextToSpeech(application, this)
        loadStations()
        startTimeUpdates()
    }

    private fun startTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                _currentTime.value = sdf.format(Date())
                delay(1000)
            }
        }
    }

    private fun loadStations() {
        viewModelScope.launch {
            _isLoading.value = true
            val stationList = repository.getStations()
            _stations.value = stationList
            if (stationList.isNotEmpty() && _selectedStation.value == null) {
                selectStation(stationList[0])
            }
            _isLoading.value = false
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Simulate network delay
            _selectedStation.value?.let {
                _trains.value = repository.getTrainsForStation(it.id)
            }
            _isLoading.value = false
        }
    }

    fun selectStation(station: Station) {
        if (_selectedStation.value?.id == station.id) return
        _selectedStation.value = station
        viewModelScope.launch {
            _isLoading.value = true
            delay(300) // Smooth transition
            _trains.value = repository.getTrainsForStation(station.id)
            _isLoading.value = false
        }
    }

    fun speakAnnouncement(train: Train) {
        val text = "${train.nameKn} ರೈಲು ಪ್ಲಾಟ್‌ಫಾರ್ಮ್ ${train.platform}ಕ್ಕೆ ಬರುತ್ತದೆ."
        Toast.makeText(getApplication(), "ಅನೌನ್ಸ್ಮೆಂಟ್ ಕೇಳಿ / Speaking Announcement", Toast.LENGTH_SHORT).show()
        
        _isSpeaking.value = train.id
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "announcement_${train.id}")
        
        // Reset speaking state after a delay (estimate based on text length)
        viewModelScope.launch {
            delay(5000)
            _isSpeaking.value = null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("kn", "IN")
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

    fun getTrainById(id: String): Train? {
        return repository.getTrainById(id)
    }
}

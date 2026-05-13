package com.example.namma_platform.data

import android.content.Context
import com.example.namma_platform.model.Station
import com.example.namma_platform.model.Train
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Serializable
data class TrainData(
    val stations: List<Station>,
    val trains: List<Train>
)

class TrainRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    
    private var cachedData: TrainData? = null

    private fun loadData(): TrainData {
        if (cachedData != null) return cachedData!!
        
        val jsonString = context.assets.open("train_data.json").bufferedReader().use { it.readText() }
        cachedData = json.decodeFromString<TrainData>(jsonString)
        return cachedData!!
    }

    fun getStations(): List<Station> = loadData().stations

    fun getTrainsForStation(stationId: String): List<Train> {
        return loadData().trains.filter { it.stationId == stationId }
    }

    fun getTrainById(trainId: String): Train? {
        return loadData().trains.find { it.id == trainId }
    }
}

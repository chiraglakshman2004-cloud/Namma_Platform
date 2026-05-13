package com.example.namma_platform.model

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val id: String,
    val name: String,
    val nameKn: String
)

@Serializable
data class Coach(
    val name: String,
    val type: CoachType
)

enum class CoachType {
    ENGINE, GENERAL, LADIES, SLEEPER, AC, SECOND_CLASS
}

@Serializable
data class Train(
    val id: String,
    val number: String,
    val name: String,
    val nameKn: String,
    val platform: String,
    val arrivalTime: String,
    val coachSequence: List<String>,
    val stationId: String
)

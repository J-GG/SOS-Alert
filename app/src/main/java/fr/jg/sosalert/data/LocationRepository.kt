package fr.jg.sosalert.data

import android.location.Location

interface LocationRepository {
    fun hasLocationPermission(): Boolean

    suspend fun getLocation(): Location?
}
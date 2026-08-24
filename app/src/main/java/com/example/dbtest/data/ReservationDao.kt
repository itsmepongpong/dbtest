package com.example.dbtest.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation)

    @Query("SELECT * FROM reservations ORDER BY id DESC")
    fun readAllData(): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE buildingName = :buildingName ORDER BY id DESC")
    suspend fun getReservationsForBuilding(buildingName: String): List<Reservation>

    // Only ACTIVE ("Reserved") bookings block a new time slot. A cancelled reservation, or one
    // marked Done (early dismissal), no longer counts against the room.
    @Query("SELECT * FROM reservations WHERE buildingName = :buildingName AND date = :date AND status = 'Reserved'")
    suspend fun getActiveReservationsForBuildingAndDate(buildingName: String, date: String): List<Reservation>

    // All of this room's active bookings (any date), for the "manage bookings" list.
    @Query("SELECT * FROM reservations WHERE buildingName = :buildingName AND status = 'Reserved' ORDER BY date ASC, startTime ASC")
    suspend fun getActiveReservationsForBuilding(buildingName: String): List<Reservation>

    // Buildings that have a booking covering THIS exact moment - used for the map's status
    // indicator. A booking for later today, tomorrow, or last week doesn't count here; it's
    // handled by the per-slot overlap check instead, so multiple non-overlapping bookings
    // are always allowed for the same building.
    @Query(
        "SELECT DISTINCT buildingName FROM reservations " +
                "WHERE status = 'Reserved' AND date = :today AND startTime <= :nowTime AND endTime > :nowTime"
    )
    suspend fun getBuildingsReservedRightNow(today: String, nowTime: String): List<String>

    @Query("UPDATE reservations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
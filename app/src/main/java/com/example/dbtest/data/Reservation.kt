package com.example.dbtest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val buildingName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val purpose: String,
    val status: String = STATUS_RESERVED
) {
    companion object {
        const val STATUS_RESERVED = "Reserved"
        const val STATUS_CANCELLED = "Cancelled"
        // Early dismissal: the class/booking ended before its scheduled end time,
        // so the room should count as free again from now on.
        const val STATUS_DONE = "Done"
    }
}
package com.example.dbtest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dbtest.data.UserDatabase
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class ITBuilding : AppCompatActivity() {

    private val zoomScale = 1.8f
    private val animDuration = 300L

    private lateinit var mapContent: RelativeLayout
    private lateinit var slider: Slider
    private var currentBuildingId: Int? = null

    // Names of buildings that have a reservation covering THIS exact moment, loaded from the DB.
    // A building can still be booked any number of times for other, non-overlapping time slots -
    // this set only drives the "In Use Now" vs "Available Now" status indicator.
    private val reservedBuildings = mutableSetOf<String>()

    private val reservationDao by lazy {
        UserDatabase.getDatabase(applicationContext).reservationDao()
    }

    // This list matches the EXACT IDs from your itbuilding.xml
    private val buildings = listOf(
        R.id.btn_1a_block to "1A Building",
        R.id.btn_1b_block to "1B Building",
        R.id.btn_2a_block to "2A Building",
        R.id.btn_2b_block to "2B Building",
        R.id.btn_4_block to "Comp Lab",
        R.id.btn_3_block to "Internet Room",
        R.id.btn_faculty_block to "Faculty Building"
    )

    // Launches ReservationActivity and reacts once it returns, instead of a bare startActivity
    // that had no way to tell ITBuilding a reservation/cancel/done just happened.
    private val reservationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Re-check from the DB rather than assuming - the change might have been a new
            // booking for later today, a cancellation, or a "marked done", each of which
            // affects the "right now" status differently.
            refreshReservedBuildingsAndSlider()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.itbuilding)

        mapContent = findViewById(R.id.mapContent)
        slider = Slider(rootView = findViewById(R.id.slider_root))

        slider.onClose = { resetZoom() }

        // Handle back press with modern API
        val callback = object : OnBackPressedCallback(true /* enabled */) {
            override fun handleOnBackPressed() {
                if (slider.isOpen) {
                    slider.close()
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // Setup click listeners for all building buttons
        for ((id, label) in buildings) {
            findViewById<Button>(id).setOnClickListener { button ->
                currentBuildingId = id
                zoomToBuilding(button)

                // Use the real, up-to-date "in use right now" status instead of a hardcoded false.
                slider.open(label, isReserved = reservedBuildings.contains(label))
            }
        }

        // Handle the slider's reserve button click to open ReservationActivity.
        // Always allowed - even a building in use right now can be booked for a different time.
        slider.onReserveClick = {
            val buildingId = currentBuildingId
            if (buildingId != null) {
                val buildingName = buildings.find { it.first == buildingId }?.second ?: "Unknown"

                val intent = Intent(this, ReservationActivity::class.java)
                intent.putExtra("BUILDING_NAME", buildingName)
                reservationLauncher.launch(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload every time this screen becomes visible - covers a brand new reservation,
        // a cancellation, and a "marked as done" change, all in one place.
        loadReservedBuildings()
    }

    private fun loadReservedBuildings() {
        lifecycleScope.launch {
            reservedBuildings.clear()
            reservedBuildings.addAll(fetchBuildingsReservedRightNow())
        }
    }

    private fun refreshReservedBuildingsAndSlider() {
        lifecycleScope.launch {
            reservedBuildings.clear()
            reservedBuildings.addAll(fetchBuildingsReservedRightNow())

            // If the slider is still open on a building, update its status immediately
            // instead of waiting for the next time it's opened.
            val currentLabel = buildings.find { it.first == currentBuildingId }?.second
            if (currentLabel != null) {
                slider.setReservationState(isReserved = reservedBuildings.contains(currentLabel))
            }
        }
    }

    private suspend fun fetchBuildingsReservedRightNow(): List<String> {
        val now = Calendar.getInstance()
        val today = String.format(
            Locale.US, "%04d-%02d-%02d",
            now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH)
        )
        val nowTime = String.format(
            Locale.US, "%02d:%02d",
            now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)
        )
        return reservationDao.getBuildingsReservedRightNow(today, nowTime)
    }

    private fun zoomToBuilding(button: View) {
        // Zoom in, centered on the building that was tapped.
        mapContent.pivotX = button.left + button.width / 2f
        mapContent.pivotY = button.top + button.height / 2f
        mapContent.animate()
            .scaleX(zoomScale)
            .scaleY(zoomScale)
            .setDuration(animDuration)
            .start()
    }

    private fun resetZoom() {
        mapContent.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(animDuration)
            .start()
    }
}
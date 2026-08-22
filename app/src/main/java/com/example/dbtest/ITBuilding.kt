package com.example.dbtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class ITBuilding : AppCompatActivity() {

    private val zoomScale = 1.8f
    private val animDuration = 300L

    private lateinit var mapContent: RelativeLayout
    private lateinit var slider: Slider
    private var currentBuildingId: Int? = null

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

                // Open the slider (isReserved = false for now)
                slider.open(label, isReserved = false)
            }
        }

        // Handle the slider's reserve button click to open ReservationActivity
        slider.onReserveClick = {
            val buildingId = currentBuildingId
            if (buildingId != null) {
                val buildingName = buildings.find { it.first == buildingId }?.second ?: "Unknown"

                val intent = Intent(this, ReservationActivity::class.java)
                intent.putExtra("BUILDING_NAME", buildingName)
                startActivity(intent)
            }
        }
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
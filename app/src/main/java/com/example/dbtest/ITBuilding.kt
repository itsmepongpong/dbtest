package com.example.dbtest

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

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

        // Open Settings when the gear FAB is tapped, and make it draggable
        setupDraggableSettingsButton()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDraggableSettingsButton() {
        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        // Set the FAB's click behavior (opens Settings on a normal tap)
        fab.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        // Restore the last saved position once the view has been measured
        fab.post {
            val prefs = getSharedPreferences("fab_position", Context.MODE_PRIVATE)
            if (prefs.contains("fab_x") && prefs.contains("fab_y")) {
                fab.x = prefs.getFloat("fab_x", fab.x)
                fab.y = prefs.getFloat("fab_y", fab.y)
            }
        }

        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop

        var downRawX = 0f
        var downRawY = 0f
        var startX = 0f
        var startY = 0f
        var isDragging = false

        fab.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX
                    downRawY = event.rawY
                    startX = view.x
                    startY = view.y
                    isDragging = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downRawX
                    val dy = event.rawY - downRawY


                    if (!isDragging && (abs(dx) > touchSlop || abs(dy) > touchSlop)) {
                        isDragging = true
                    }

                    if (isDragging) {
                        val parent = view.parent as View
                        var newX = startX + dx
                        var newY = startY + dy

                        // Keep the button fully inside its parent
                        newX = newX.coerceIn(0f, (parent.width - view.width).toFloat())
                        newY = newY.coerceIn(0f, (parent.height - view.height).toFloat())

                        view.x = newX
                        view.y = newY
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        // Save the new position for next time
                        getSharedPreferences("fab_position", Context.MODE_PRIVATE)
                            .edit()
                            .putFloat("fab_x", view.x)
                            .putFloat("fab_y", view.y)
                            .apply()
                    } else {
                        // It was a tap, not a drag — trigger the click listener
                        view.performClick()
                    }
                    true
                }

                else -> false
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
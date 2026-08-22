package com.example.dbtest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReservationActivity : AppCompatActivity() {

    private lateinit var tvBuildingName: TextView
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etPurpose: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation) // Make sure your XML file is named reservation.xml

        // Initialize views
        tvBuildingName = findViewById(R.id.tv_building_name)
        etDate = findViewById(R.id.et_date)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        etPurpose = findViewById(R.id.et_purpose)
        btnSubmit = findViewById(R.id.btn_submit_reservation)
        btnCancel = findViewById(R.id.btn_cancel_reservation)

        // Get building name from intent
        val buildingName = intent.getStringExtra("BUILDING_NAME") ?: "Unknown Building"

        tvBuildingName.text = "Building: $buildingName"

        // Submit button click
        btnSubmit.setOnClickListener {
            val date = etDate.text.toString()
            val startTime = etStartTime.text.toString()
            val endTime = etEndTime.text.toString()
            val purpose = etPurpose.text.toString()

            if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            } else {
                // Show success message
                Toast.makeText(this, "Reservation submitted successfully!", Toast.LENGTH_LONG).show()

                // Close the activity and go back to the map
                finish()
            }
        }

        // Cancel button click
        btnCancel.setOnClickListener {
            finish()
        }
    }
}
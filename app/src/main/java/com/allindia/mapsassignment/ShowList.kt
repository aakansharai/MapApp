package com.allindia.mapsassignment

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ShowList : AppCompatActivity() {

    lateinit var fromLocation : TextView
    lateinit var toLocation : TextView

    lateinit var fromLocation2 : TextView
    lateinit var toLocation2 : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var startLocation = intent.getStringExtra("startLocation")
        var endLocation = intent.getStringExtra("endLocation")


        fromLocation = findViewById(R.id.fromLocation)
        toLocation = findViewById(R.id.toLocation)

        fromLocation2 = findViewById(R.id.fromLocation2)
        toLocation2 = findViewById(R.id.toLocation2)

        if(startLocation != null && endLocation !== null) {
            fromLocation.text = startLocation.toString()
            toLocation.text = endLocation.toString()

            fromLocation2.text = startLocation.toString()
            toLocation2.text = endLocation.toString()
        }

    }
}
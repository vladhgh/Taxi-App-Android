package io.dev.taxi.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.dev.taxi.R
import kotlinx.android.synthetic.main.activity_waiting.*

class WaitingForOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)
        this.done.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {

    }
}

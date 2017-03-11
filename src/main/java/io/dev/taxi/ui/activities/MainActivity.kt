package io.dev.taxi.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.dev.taxi.R
import io.dev.taxi.utils.ConstantManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var TAG: String = ConstantManager.TAG_PREFIX + " Main Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.button_one.setOnClickListener {
            Log.d(TAG, "button pressed")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

    }
}

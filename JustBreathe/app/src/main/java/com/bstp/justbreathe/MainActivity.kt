package com.bstp.justbreathe

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bstp.justbreathe.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity()
{
    companion object {
        lateinit var accManager: AccelerometerManager
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        accManager = AccelerometerManager(this)

        val calibrationValue : Float? = savedInstanceState?.getFloat("calibrationValue", 0f)
        if(calibrationValue != null)
            accManager.setCalibrationValue(calibrationValue)
        Log.d("MiaAPP", "******************************************************************************")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.itemCalibration -> {
                Toast.makeText(applicationContext, R.string.TextCalibrationPhase, Toast.LENGTH_SHORT).show()
                accManager.startCalibration()
            }
            R.id.itemAbout -> {
                Toast.makeText(applicationContext, R.string.textDevelopers, Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putFloat("calibrationValue", accManager.getCalibrationValue())
    }

    override fun onDestroy()
    {
        accManager.unregisterSensorListener()
        super.onDestroy()
    }

    //SENSOR_DELAY_NORMAL = 200ms -> 5Hz
    //SENSOR_DELAY_UI = 66ms -> 15.55Hz
    //SENSOR_DELAY_GAME = 20ms -> 50Hz
    //SENSOR_DELAY_FASTEST = 2ms -> 500Hz
}
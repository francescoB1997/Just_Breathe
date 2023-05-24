package com.bstp.justbreathe

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

class AccelerometerManager(private val appCompatActivity : AppCompatActivity) : SensorEventListener , Serializable
{
    private val SAMPLE_DELAY : Int = SensorManager.SENSOR_DELAY_GAME
    private val WINDOW_SIZE : Int = 125

    private var calibrationMode : Boolean = false
    private var samplingMode : Boolean = false // Modificata dall'esterno per abilitare il sampling dal sensore
    private var calibrationValue : Float = 0f

    private var values = mutableListOf<Float>()
    //private var windowAverageValues = mutableListOf<Float>() --> Inglobata in FilterManager

    private var sensorManager : SensorManager = appCompatActivity.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager

    init {
        setUpSensor()
    }

    private fun setUpSensor()
    {
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SAMPLE_DELAY,
                SAMPLE_DELAY)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?)
    {
        if(event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            if(calibrationMode && !samplingMode) // Se sto calibrando AND non sono in fase di sampling ==> Calibro
            {
                if(values.size < 100)
                    values.add(event.values[2])
                else
                {
                    stopCalibration()
                    if( (values.filter { it !in -1f..1f }).size > 0)
                    {
                        clearSampleValues()
                        Toast.makeText(appCompatActivity.applicationContext, R.string.TextCalibrationFailed, Toast.LENGTH_SHORT).show()
                        return
                    }
                    calibrationValue = values.average().toFloat() // Faccio la media di 120 valori appena campionati
                    clearSampleValues()
                    Toast.makeText(appCompatActivity, R.string.TextCalibrationSuccess, Toast.LENGTH_SHORT).show()
                }
                return
            }

            if(samplingMode)
            {
                val accZ = event.values[2] - calibrationValue
                values.add(accZ)
                if(values.size >= WINDOW_SIZE) // Se posso calcolare la media
                {
                    val startPosition = values.size - WINDOW_SIZE
                    FilterManager.windowAverageValues.add( FilterManager.windowAverage(values, startPosition) )
                }
            }
        }
    }

    private fun clearSampleValues() { values.clear() }
    fun enableSampling() { samplingMode = true }
    fun disableSampling()
    {
        samplingMode = false
        clearSampleValues()
    }

    fun startCalibration()  {  calibrationMode = true }
    private fun stopCalibration()  { calibrationMode = false  }
    fun getCalibrationValue() : Float = calibrationValue
    fun setCalibrationValue(calibrationValue_ : Float)  { this.calibrationValue = calibrationValue_ }
    fun unregisterSensorListener()  { sensorManager.unregisterListener(this) }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
}
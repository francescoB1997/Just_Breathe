package com.bstp.justbreathe

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bstp.justbreathe.databinding.SamplingActivityBinding

@Suppress("DEPRECATION")
class SamplingActivity : AppCompatActivity()
{
    private lateinit var binding: SamplingActivityBinding
    private var sampling : Boolean = false
    private lateinit var imgButton : ImageView
    private var pattern : String = ""
    private var timer = 3
    private val handler = Handler(Looper.getMainLooper())
    private var vibrator: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = SamplingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgButton = binding.imgBtnSample
        imgButton.setOnClickListener { clickListenerSampling() }

        val intent = intent
        val toolbar: Toolbar = binding.toolbarSampActivity
        toolbar.title = getString(R.string.toolbarSamplingTitle) + " " + intent.getStringExtra("pattern")
        setSupportActionBar(toolbar)
        pattern = intent.getStringExtra("pattern")!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        if (Build.VERSION.SDK_INT >= 31) {
            vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager!!.defaultVibrator
        } else {
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun clickListenerSampling()
    {
        sampling = !sampling
        if(sampling)
        {
            binding.lblScore.text = getString(R.string.lblScoreTextScore, "")
            handler.post(object : Runnable
            {
                override fun run()
                {
                    handler.postDelayed(this, 1000)
                    updateTime()
                }
            })
        }
        else {
            binding.progressBar.visibility = View.INVISIBLE
            imgButton.setImageResource(R.drawable.ic_start_sampling)
            MainActivity.accManager.disableSampling()
            val filterManager = FilterManager()
            val filteredData : DoubleArray? = filterManager.filterData()
            val score = filterManager.scoreBreathing(filteredData, pattern, applicationContext)
            if( (score) != -1f)
                binding.lblScore.text = getString(R.string.lblScoreTextScore, score.toString())
            else
            {
                Toast.makeText(applicationContext, R.string.msgSampleError, Toast.LENGTH_LONG).show()
                binding.lblScore.text = getString(R.string.lblScoreTextStart)
            }
        }
    }

    private fun updateTime()
    {
        imgButton.visibility = View.INVISIBLE

        binding.textTimer.visibility = View.VISIBLE
        binding.textTimer.text = (timer--).toString()
        if( timer < 0)
        {
            binding.progressBar.visibility = View.VISIBLE
            vibratePhone()
            handler.removeMessages(0)
            binding.textTimer.visibility = View.INVISIBLE
            imgButton.setImageResource(R.drawable.ic_stop_sampling)
            imgButton.visibility = View.VISIBLE
            resetTimer()
            MainActivity.accManager.enableSampling()
        }
    }

    private fun vibratePhone()
    {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else //deprecated in API 26
        {
            v.vibrate(300)
        }
    }

    private fun resetTimer()
    {
        timer = 3
    }
}
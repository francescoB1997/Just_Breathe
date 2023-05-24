package com.bstp.justbreathe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.bstp.justbreathe.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbarSampActivity
        val pattern :String = intent.getStringExtra("pattern")!!
        val view = window.decorView

        toolbar.title = getString(R.string.toolbarInfoTitle) + " " + pattern
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener({ finish() })

        if (pattern.compareTo(applicationContext.getString(R.string.PATTERN_404))== 0) { //we are in 4-0-4
            binding.lblInfoPattern.text = getString(R.string.infoPattern404)
            view.setBackgroundResource(R.color.light_blue)
        }
        else if (pattern.compareTo(applicationContext.getString(R.string.PATTERN_478))== 0) { //we are in 4-7-8
            binding.lblInfoPattern.text = getString(R.string.infoPattern478)
            view.setBackgroundResource(R.color.light_green)
        }
        else if (pattern.compareTo(applicationContext.getString(R.string.PATTERN_444))== 0) { //we are in 4-4-4
            binding.lblInfoPattern.text = getString(R.string.infoPattern444)
            view.setBackgroundResource(R.color.yellow)
        }
    }
}
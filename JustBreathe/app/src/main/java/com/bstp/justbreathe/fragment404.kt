package com.bstp.justbreathe

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bstp.justbreathe.databinding.Fragment404Binding

private const val ARG_PARAM1 = "param1"

class Fragment404 : Fragment() {
    private var param1: String? = null
    private var _binding: Fragment404Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }

    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = Fragment404Binding.inflate(inflater, container, false)
        val view = binding.root

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.imageView404.layoutParams.height = 350
            binding.imageView404.layoutParams.width = 350
        }

        binding.btnInfo404.setOnClickListener {
            activity?.let {
                val intent = Intent(it, InfoActivity::class.java)
                intent.putExtra("pattern", getString(R.string.PATTERN_404))
                it.startActivity(intent)
            }
        }

        binding.imageView404.setOnClickListener {
            activity?.let {
                val intent = Intent(it, SamplingActivity::class.java)
                intent.putExtra("pattern", getString(R.string.PATTERN_404))
                it.startActivity(intent)
            }
        }
        return view
    }
}
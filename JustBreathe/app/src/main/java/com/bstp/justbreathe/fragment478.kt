package com.bstp.justbreathe

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bstp.justbreathe.databinding.Fragment478Binding

private const val ARG_PARAM1 = "param1"

class Fragment478 : Fragment() {

    private var param1: String? = null
    private var _binding: Fragment478Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment478Binding.inflate(inflater, container, false)
        val view = binding.root

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.imageView478.layoutParams.height = 350
            binding.imageView478.layoutParams.width = 350
        }

        binding.btnInfo478.setOnClickListener {
            activity?.let {
                val intent = Intent(it, InfoActivity::class.java)
                intent.putExtra("pattern", getString(R.string.PATTERN_478))
                it.startActivity(intent)
            }
        }

        binding.imageView478.setOnClickListener {
            activity?.let {
                val intent = Intent(it, SamplingActivity::class.java)
                intent.putExtra("pattern", getString(R.string.PATTERN_478))
                it.startActivity(intent)
            }
        }
        return view
    }
}
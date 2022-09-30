package com.avengers.maskfitting.mafiafin.main.fitting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avengers.maskfitting.mafiafin.databinding.FragmentMaskFittingBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Mask_fitting.newInstance] factory method to
 * create an instance of this fragment.
 */
class Mask_fitting : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMaskFittingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMaskFittingBinding.inflate(inflater, container, false)

        binding.imagebtn1.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn2.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn3.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn4.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn5.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn6.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn7.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn8.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn9.setOnClickListener{
            val intent = Intent(activity, FittingActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment mask_fitting.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Mask_fitting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

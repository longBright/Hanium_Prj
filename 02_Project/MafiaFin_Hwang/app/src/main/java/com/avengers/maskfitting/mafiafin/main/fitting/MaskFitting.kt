package com.avengers.maskfitting.mafiafin.main.fitting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avengers.maskfitting.mafiafin.databinding.FragmentMaskFittingNewBinding
import com.avengers.maskfitting.mafiafin.main.fitting.bird.FaceLandmarksActivityBird
import com.avengers.maskfitting.mafiafin.main.fitting.bird.FaceLandmarksActivityBird_Black
import com.avengers.maskfitting.mafiafin.main.fitting.bird.FaceLandmarksActivityBird_Pink
import com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivity
import com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivityBlack
import com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivityPink
import com.avengers.maskfitting.mafiafin.main.fitting.kf.FaceLandmarksActivityKF
import com.avengers.maskfitting.mafiafin.main.fitting.kf.FaceLandmarksActivityKFBlack
import com.avengers.maskfitting.mafiafin.main.fitting.kf.FaceLandmarksActivityKFPink

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
    var maskNum = 0
    private lateinit var binding: FragmentMaskFittingNewBinding


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
        binding = FragmentMaskFittingNewBinding.inflate(inflater, container, false)

        binding.imagebtn1.setOnClickListener{
            maskNum =1

            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivity::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn2.setOnClickListener{
            maskNum = 2
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.kf.FaceLandmarksActivityKF::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)

        }

        binding.imagebtn3.setOnClickListener{
            maskNum = 3
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.bird.FaceLandmarksActivityBird::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn4.setOnClickListener{
            maskNum = 4
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivityBlack::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn5.setOnClickListener{
            maskNum = 5
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.kf.FaceLandmarksActivityKFBlack::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn6.setOnClickListener{
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.bird.FaceLandmarksActivityBird_Black::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn7.setOnClickListener{
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.dental.FaceLandmarksActivityPink::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn8.setOnClickListener{
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.kf.FaceLandmarksActivityKFPink::class.java)
            // intent 에 값을 넣어 전달
            startActivity(intent)
        }

        binding.imagebtn9.setOnClickListener{
            val intent = Intent(activity, com.avengers.maskfitting.mafiafin.main.fitting.bird.FaceLandmarksActivityBird_Pink::class.java)
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

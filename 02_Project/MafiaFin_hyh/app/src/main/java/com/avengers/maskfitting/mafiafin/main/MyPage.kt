package com.avengers.maskfitting.mafiafin.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avengers.maskfitting.mafiafin.databinding.FragmentMypageBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class MyPage() : Fragment() {
    private lateinit var binding: FragmentMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 현재 로그인중인 계정 로드
        account = this.let { GoogleSignIn.getLastSignedInAccount(it.requireContext()) }
        Log.d("MyPage", "${account?.email}")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        // 현재 로그인 중인 사용자의 이름 표시(구글)
        binding.helloText.text = account?.displayName + binding.helloText.text

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        var account: GoogleSignInAccount? = null
    }
}
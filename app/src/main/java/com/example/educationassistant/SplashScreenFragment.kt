package com.example.educationassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser

        //if there is a user who has already logged in, he/she will see the main page directly after clicking the "Eğitimde Parlamaya Başla" button.
        if (currentUser != null) {
            binding.toMainPageButton.setOnClickListener {
                val action =
                    SplashScreenFragmentDirections.actionSplashScreenFragmentToHomePageFragment()
                Navigation.findNavController(it).navigate(action)
            }
        } else {
            //if there is no a current user, to be able to go to the registration page by clicking to "Eğitimde Parlamaya Başla" button in splash screen
            binding.toMainPageButton.setOnClickListener {
                val action =
                    SplashScreenFragmentDirections.actionSplashScreenFragmentToRegistrationFragment()
                Navigation.findNavController(it).navigate(action)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
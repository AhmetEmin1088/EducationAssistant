package com.example.educationassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logInButton.setOnClickListener {

            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()

            if (email.equals("") || password.equals("")) {
                Toast.makeText(context,"Email veya parola boş bırakılamaz.",Toast.LENGTH_LONG).show()
            }else {
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {

                    val action = LogInFragmentDirections.actionLogInFragmentToHomePageFragment()
                    Navigation.findNavController(view).navigate(action)

                }.addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }

        }

        binding.registerTextButton.setOnClickListener {
            val action = LogInFragmentDirections.actionLogInFragmentToRegistrationFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.forgotPasswordTextView.setOnClickListener {

            val emailAddress = binding.emailText.text.toString().trim()

            if (emailAddress.isEmpty()) {
                Toast.makeText(context, "Lütfen e-posta adresinizi girin.", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                Toast.makeText(context, "Lütfen geçerli bir e-posta adresi girin.", Toast.LENGTH_SHORT).show()
            } else {
                Firebase.auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Lütfen e-posta kutunuzu kontrol edin.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, task.exception?.localizedMessage ?: "Şifre sıfırlama işlemi sırasında bir hata oluştu.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
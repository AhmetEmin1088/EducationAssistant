package com.example.educationassistant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if user already has an account, he/she will click this button to be able go to log in page
        binding.logInTextButton.setOnClickListener {
            val action = RegistrationFragmentDirections.actionRegistrationFragmentToLogInFragment()
            Navigation.findNavController(it).navigate(action)
        }

        //if user doesn't have an account, he/she will click this button to register after filling all the gaps
        binding.registerButton.setOnClickListener {

            var firstName = binding.firstNameText.text.toString()
            var lastName = binding.lastNameText.text.toString()
            val email = binding.emailText.text.toString()
            val password = binding.passwordText.text.toString()
            val passwordAgain = binding.passwordAgainText.text.toString()

            if (firstName.equals("") || lastName.equals("") || email.equals("") || password.equals("") || passwordAgain.equals("")) {
                Toast.makeText(context,"Lütfen bütün boş alanları doldurunuz.",Toast.LENGTH_LONG).show()
            }else{
                if (password.equals(passwordAgain)) {

                    if(isPasswordStrong(password)) {

                        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {

                            // Kullanıcı UID'sini al
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid

                            // Kullanıcı bilgilerini içeren bir harita oluştur
                            val userMap = hashMapOf<String,Any>()
                            userMap.put("userFirstName",firstName)
                            userMap.put("userLastName",lastName)
                            userMap.put("userEmail",currentUser!!.email!!)

                            // Kullanıcı UID'si ile Firestore'a kaydet
                            if (userId != null) {
                                firestore.collection("Users").document(userId).set(userMap).addOnSuccessListener {
                                    // Veritabanına başarıyla kaydedildi
                                    val action = RegistrationFragmentDirections.actionRegistrationFragmentToLogInFragment()
                                    Navigation.findNavController(view).navigate(action)

                                }.addOnFailureListener {
                                    Toast.makeText(context, "Kullanıcı bilgileri kaydedilemedi: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }

                        }.addOnFailureListener {
                            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }

                    }else {
                        Toast.makeText(context, "Parola en az 8 karakter uzunluğunda olmalı ve büyük harf, küçük harf, rakam ve özel karakter içermelidir.", Toast.LENGTH_LONG).show()
                    }

                }else {
                    Toast.makeText(context,"Girdiğiniz parolalar eşleşmiyor.",Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    fun isPasswordStrong(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&.,;:+/=-])[A-Za-z\\d@\$!%*?&.,;:+/=-]{8,}$"
        val pattern = Regex(passwordPattern)
        return pattern.containsMatchIn(password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
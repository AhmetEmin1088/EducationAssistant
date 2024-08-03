package com.example.educationassistant

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserInfo()

        binding.userNameAndSurnameCard.setOnClickListener {
            toggleVisibility(binding.newUserNameText, binding.newUserSurnameText, binding.changeNameAndSurnameButton)
        }

        binding.changeNameAndSurnameButton.setOnClickListener {
            val user = auth.currentUser
            val newFirstName = binding.newUserNameText.text.toString()
            val newLastName = binding.newUserSurnameText.text.toString()

            if (newFirstName.isNotEmpty() && newLastName.isNotEmpty()) {
                // Firebase Authentication'daki kullanıcı profilini güncelle
                val profileUpdates = userProfileChangeRequest {
                    displayName = "$newFirstName $newLastName"
                }

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Firestore'daki kullanıcı adını ve soyadını güncelle
                            val userRef = db.collection("Users").document(user.uid)
                            userRef.update("userFirstName", newFirstName, "userLastName", newLastName)
                                .addOnSuccessListener {
                                    binding.userNameAndSurnameText.text = "$newFirstName $newLastName"
                                    Toast.makeText(requireContext(), "Ad ve soyad başarıyla güncellendi.", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Ad ve soyad güncellenirken bir hata oluştu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(requireContext(), "Profil güncellenirken bir hata oluştu: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Lütfen yeni ad ve soyadınızı girin.", Toast.LENGTH_SHORT).show()
            }
        }


        binding.changePasswordButton.setOnClickListener {
            val currentPassword = binding.userPasswordText.text.toString()
            val newPassword = binding.newUserPasswordText.text.toString()

            if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                val user = auth.currentUser
                val email = user?.email

                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user.reauthenticate(credential)
                        .addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            Toast.makeText(requireContext(), "Şifre güncellendi.", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(requireContext(), "Şifre güncellenirken bir hata oluştu.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(requireContext(), "Mevcut şifreniz yanlış.", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Email adresiniz alınamadı.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(requireContext(), "Lütfen mevcut ve yeni şifrenizi girin.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteAccountButton.setOnClickListener {
            val user = auth.currentUser

            val alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog.setTitle("Hesabı Sil")
                .setMessage("Hesabınızı silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet",DialogInterface.OnClickListener { dialogInterface, i ->

                    if (user != null) {
                        db.collection("Users").document(user.uid).delete().addOnSuccessListener {

                            user.delete().addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    Toast.makeText(requireActivity(), "Kullanıcı silindi.", Toast.LENGTH_LONG).show()

                                    val action = ProfileFragmentDirections.actionProfileFragmentToRegistrationFragment()
                                    Navigation.findNavController(requireView()).navigate(action)

                                } else {
                                    Toast.makeText(requireActivity(), "Kullanıcı silinemedi: ${task.exception?.localizedMessage}.", Toast.LENGTH_LONG).show()
                                }
                            }

                        }.addOnFailureListener {
                            Toast.makeText(requireActivity(), "Kullanıcı bilgileri silinemedi. : ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }

                })
                .setNegativeButton("Hayır", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .create()
                .show()
        }
    }

    private fun toggleVisibility(vararg views: View) {
        views.forEach { view ->
            view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun showUserInfo() {

        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Firestore'daki kullanıcı bilgilerine eriş
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    val firstName = document.getString("userFirstName")
                    val lastName = document.getString("userLastName")
                    val email = document.getString("userEmail")

                    binding.userNameAndSurnameText.text = firstName + " " + lastName
                    binding.userEmailText.text = email
                } else {
                    // Belge bulunamadı
                    Toast.makeText(context, "Kullanıcı bilgileri bulunamadı.", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { e ->
                // Hata oluştu
                Toast.makeText(context, "Kullanıcı bilgileri alınamadı: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Kullanıcı oturum açmamış
            Toast.makeText(context, "Kullanıcı oturum açmamış.", Toast.LENGTH_LONG).show()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = ProfileFragmentDirections.actionProfileFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            Toast.makeText(requireActivity(),"Zaten profil sayfasındasınız.",Toast.LENGTH_LONG).show()

        } else if (item.itemId == R.id.timetable) {

            val action = ProfileFragmentDirections.actionProfileFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = ProfileFragmentDirections.actionProfileFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = ProfileFragmentDirections.actionProfileFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = ProfileFragmentDirections.actionProfileFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = ProfileFragmentDirections.actionProfileFragmentToLogInFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.close_app) {

            val alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog
                .setTitle("Uygulamayı Kapat")
                .setMessage("Uygulamayı kapatmak istediğinize emin misiniz?")
                .setPositiveButton("Evet", DialogInterface.OnClickListener { dialogInterface, i ->
                    requireActivity().finish()
                })
                .setNegativeButton("Hayır", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .create()
                .show()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

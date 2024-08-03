package com.example.educationassistant

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomePageFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var currentAvatar: Int = R.drawable.profile_man_avatar

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
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        currentAvatar = sharedPreferences.getInt("profile_avatar", R.drawable.profile_man_avatar)
        binding.profileAvatarView.setImageResource(currentAvatar)

        binding.profileAvatarView.setOnClickListener {
            toggleAvatar()
        }


        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Firestore'daki kullanıcı bilgilerine eriş
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {

                    val firstName = document.getString("userFirstName")
                    val lastName = document.getString("userLastName")

                    binding.userNameText.text = firstName + " " + lastName
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

        binding.createTimetableView.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToTimetableFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.assignmentTrackingView.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToAssignmentFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.examRemainderView.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToExamFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.gpaCalculationView.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToGpaFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.learningResourcesView.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToResourcesFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.newsView.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToNewsFragment()
            Navigation.findNavController(it).navigate(action)
        }


    }

    private fun toggleAvatar() {
        val newAvatar = if (currentAvatar == R.drawable.profile_man_avatar) {
            R.drawable.profile_woman_avatar
        } else {
            R.drawable.profile_man_avatar
        }

        currentAvatar = newAvatar
        binding.profileAvatarView.setImageResource(newAvatar)
        sharedPreferences.edit().putInt("profile_avatar", newAvatar).apply()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            Toast.makeText(requireActivity(),"Zaten ana sayfadasınız.",Toast.LENGTH_LONG).show()

        } else if (item.itemId == R.id.profile) {

            val action = HomePageFragmentDirections.actionHomePageFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.timetable) {

            val action = HomePageFragmentDirections.actionHomePageFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = HomePageFragmentDirections.actionHomePageFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = HomePageFragmentDirections.actionHomePageFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = HomePageFragmentDirections.actionHomePageFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = HomePageFragmentDirections.actionHomePageFragmentToLogInFragment()
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
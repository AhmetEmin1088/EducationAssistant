package com.example.educationassistant

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.TimetableFragmentDirections
import com.example.educationassistant.databinding.FragmentSplashScreenBinding
import com.example.educationassistant.databinding.FragmentTimetableBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class TimetableFragment : Fragment() {
    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lessonNameText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // EditText odaklandığında ve metin varsayılan metinse, metni sil.
                if (binding.lessonNameText.text.toString() == "Dersin Adını Giriniz : ") {
                    binding.lessonNameText.text.clear()
                }
            } else {
                // EditText odak dışı olduğunda ve metin boşsa, varsayılan metni geri yükle.
                if (binding.lessonNameText.text.toString().isEmpty()) {
                    binding.lessonNameText.setText("Dersin Adını Giriniz : ")
                }
            }
        }

        binding.startTimeText.setOnClickListener {
            showTimePickerDialog(binding.startTimeText)
        }
        binding.endTimeText.setOnClickListener {
            showTimePickerDialog(binding.endTimeText)
        }

        binding.addToTimetableButton.setOnClickListener {

            val lessonName = binding.lessonNameText.text.toString()
            val lessonStartTime = binding.startTimeText.text.toString()
            val lessonEndTime = binding.endTimeText.text.toString()
            val dayOfLesson = binding.dayOfLessonText.selectedItem.toString()

            if (checkTimeValidity(lessonStartTime,lessonEndTime)) {

                if(auth.currentUser != null) {
                    val lessonMap = hashMapOf<String,Any>()
                    lessonMap.put("userEmail", auth.currentUser!!.email!!)
                    lessonMap.put("lessonName",lessonName)
                    lessonMap.put("lessonStartTime",lessonStartTime)
                    lessonMap.put("lessonEndTime",lessonEndTime)
                    lessonMap.put("dayOfLesson",dayOfLesson)
                    lessonMap.put("date",Timestamp.now())

                    firestore.collection("Lessons").add(lessonMap).addOnSuccessListener {

                        val action = TimetableFragmentDirections.actionTimetableFragmentToDisplayTimetableFragment()
                        Navigation.findNavController(requireView()).navigate(action)

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                    
                }

            }else {
                // Başlangıç saati bitiş saatinden sonra, kullanıcıya hata mesajı gösterin
                Toast.makeText(requireContext(), "Bitiş saati başlangıç saatinden sonra olmalıdır.", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun showTimePickerDialog(timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            timeTextView.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun checkTimeValidity(startTime: String, endTime: String): Boolean {
        val startTimeParts = startTime.split(":")
        val endTimeParts = endTime.split(":")

        val startHour = startTimeParts[0].toInt()
        val startMinute = startTimeParts[1].toInt()
        val endHour = endTimeParts[0].toInt()
        val endMinute = endTimeParts[1].toInt()

        // Başlangıç saatini oluşturun
        val startTimeCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, startHour)
            set(Calendar.MINUTE, startMinute)
        }

        // Bitiş saatini oluşturun
        val endTimeCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
        }

        // Başlangıç saati bitiş saatinden önce olmalı
        return startTimeCalendar.before(endTimeCalendar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = TimetableFragmentDirections.actionTimetableFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = TimetableFragmentDirections.actionTimetableFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = TimetableFragmentDirections.actionTimetableFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = TimetableFragmentDirections.actionTimetableFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = TimetableFragmentDirections.actionTimetableFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = TimetableFragmentDirections.actionTimetableFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = TimetableFragmentDirections.actionTimetableFragmentToLogInFragment()
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

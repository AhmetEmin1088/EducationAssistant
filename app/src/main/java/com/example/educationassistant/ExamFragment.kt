package com.example.educationassistant

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import com.example.educationassistant.databinding.FragmentDisplayTimetableBinding
import com.example.educationassistant.databinding.FragmentExamBinding
import com.example.educationassistant.databinding.FragmentTimetableBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExamFragment : Fragment() {

    private var _binding: FragmentExamBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExamBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.examNameText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // EditText odaklandığında ve metin varsayılan metinse, metni sil.
                if (binding.examNameText.text.toString() == "Sınavın Adını Giriniz : ") {
                    binding.examNameText.text.clear()
                }
            } else {
                // EditText odak dışı olduğunda ve metin boşsa, varsayılan metni geri yükle.
                if (binding.examNameText.text.toString().isEmpty()) {
                    binding.examNameText.setText("Sınavın Adını Giriniz : ")
                }
            }
        }

        binding.examDateText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    binding.examDateText.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.examTimeText.setOnClickListener {
            showTimePickerDialog(binding.examTimeText)
        }

        binding.setReminderButton.setOnClickListener {

            val examName = binding.examNameText.text.toString()
            val examDate = binding.examDateText.text.toString()
            val examTime = binding.examTimeText.text.toString()

            if (auth.currentUser != null) {
                val examMap = hashMapOf<String, Any>()
                examMap.put("userEmail", auth.currentUser!!.email!!)
                examMap.put("examName", examName)
                examMap.put("examDate", examDate)
                examMap.put("examTime", examTime)
                examMap.put("date", Timestamp.now())

                firestore.collection("Exams").add(examMap).addOnSuccessListener {

                    // Bildirim planlama fonksiyonunu çağırın
                    scheduleExamNotification(requireContext(), it.id, examName, examDate,examTime)

                    val action = ExamFragmentDirections.actionExamFragmentToAllExamsFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    private fun scheduleExamNotification(context: Context, examId: String, examName: String, examDate: String, examTime: String) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("titleExtra", "Sınav Hatırlatma")
            putExtra("messageExtra", "$examName sınavı yarın saat $examTime.")
            putExtra("notificationId",examId.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(examDate)!!
            val timeParts = examTime.split(":")
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = ExamFragmentDirections.actionExamFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = ExamFragmentDirections.actionExamFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = ExamFragmentDirections.actionExamFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = ExamFragmentDirections.actionExamFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = ExamFragmentDirections.actionExamFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = ExamFragmentDirections.actionExamFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = ExamFragmentDirections.actionExamFragmentToLogInFragment()
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
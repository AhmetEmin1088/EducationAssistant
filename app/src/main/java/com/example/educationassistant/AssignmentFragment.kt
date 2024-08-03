package com.example.educationassistant

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
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
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentAssignmentBinding
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

class AssignmentFragment : Fragment() {

    private var _binding: FragmentAssignmentBinding? = null
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
        // Inflate the layout for this fragment
        _binding = FragmentAssignmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.assignmentNameText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // EditText odaklandığında ve metin varsayılan metinse, metni sil.
                if (binding.assignmentNameText.text.toString() == "Dersin Adını Giriniz : ") {
                    binding.assignmentNameText.text.clear()
                }
            } else {
                // EditText odak dışı olduğunda ve metin boşsa, varsayılan metni geri yükle.
                if (binding.assignmentNameText.text.toString().isEmpty()) {
                    binding.assignmentNameText.setText("Dersin Adını Giriniz : ")
                }
            }
        }

        binding.assignmentDescriptionText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // EditText odaklandığında ve metin varsayılan metinse, metni sil.
                if (binding.assignmentDescriptionText.text.toString() == "13:00") {
                    binding.assignmentDescriptionText.text.clear()
                }
            } else {
                // EditText odak dışı olduğunda ve metin boşsa, varsayılan metni geri yükle.
                if (binding.assignmentDescriptionText.text.toString().isEmpty()) {
                    binding.assignmentDescriptionText.setText("13:00")
                }
            }
        }

        binding.assignmentDeadlineText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    binding.assignmentDeadlineText.setText("$dayOfMonth/${monthOfYear + 1}/$year")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.setReminderButton.setOnClickListener {

            val assignmentName = binding.assignmentNameText.text.toString()
            val assignmentDescription = binding.assignmentDescriptionText.text.toString()
            val assignmentDeadline = binding.assignmentDeadlineText.text.toString()

            if (auth.currentUser != null) {
                val assignmentMap = hashMapOf<String, Any>()
                assignmentMap.put("userEmail", auth.currentUser!!.email!!)
                assignmentMap.put("assignmentName", assignmentName)
                assignmentMap.put("assignmentDescription", assignmentDescription)
                assignmentMap.put("assignmentDeadline", assignmentDeadline)
                assignmentMap.put("date", Timestamp.now())

                firestore.collection("Assignments").add(assignmentMap).addOnSuccessListener {

                    val action = AssignmentFragmentDirections.actionAssignmentFragmentToAllAssignmentsFragment()
                    Navigation.findNavController(requireView()).navigate(action)

                    scheduleAssignmentNotification(requireContext(), it.id, assignmentName, assignmentDeadline)

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }

        }

    }

    private fun scheduleAssignmentNotification(context: Context, assignmentId: String, assignmentName: String, assignmentDeadline: String) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        notificationIntent.putExtra("titleExtra","Ödev Hatırlatması")
        notificationIntent.putExtra("messageExtra","$assignmentName ödevinizin son teslim tarihi yarın.")
        notificationIntent.putExtra("notificationId",assignmentId.hashCode())

        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(assignmentDeadline)!!
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = AssignmentFragmentDirections.actionAssignmentFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = AssignmentFragmentDirections.actionAssignmentFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = AssignmentFragmentDirections.actionAssignmentFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = AssignmentFragmentDirections.actionAssignmentFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = AssignmentFragmentDirections.actionAssignmentFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = AssignmentFragmentDirections.actionAssignmentFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = AssignmentFragmentDirections.actionAssignmentFragmentToLogInFragment()
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
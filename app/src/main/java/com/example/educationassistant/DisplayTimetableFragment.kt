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
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.educationassistant.adapter.TimetableRecyclerAdapter
import com.example.educationassistant.databinding.FragmentDisplayTimetableBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DisplayTimetableFragment : Fragment() {

    private var _binding: FragmentDisplayTimetableBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var lessonArrayList : ArrayList<LessonOfTimetable>
    private lateinit var timetableRecyclerAdapter: TimetableRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth
        db = Firebase.firestore

        lessonArrayList = ArrayList<LessonOfTimetable>()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDisplayTimetableBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timetableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        timetableRecyclerAdapter = TimetableRecyclerAdapter(lessonArrayList)
        binding.timetableRecyclerView.adapter = timetableRecyclerAdapter

        binding.addMoreLessonButton.setOnClickListener {
            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToTimetableFragment()
            Navigation.findNavController(it).navigate(action)
        }

        val userEmail = auth.currentUser?.email

        userEmail.let {

            db.collection("Lessons").whereEqualTo("userEmail",it).orderBy("date",Query.Direction.ASCENDING).addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (value != null) {
                        if (!value.isEmpty) {

                            val documents = value.documents

                            lessonArrayList.clear()

                            for (document in documents) {

                                val lessonId = document.id
                                val lessonName = document.get("lessonName") as String
                                val lessonStartTime = document.get("lessonStartTime") as String
                                val lessonEndTime = document.get("lessonEndTime") as String
                                val dayOfLesson = document.get("dayOfLesson") as String

                                val lessonOfTimetable = LessonOfTimetable(lessonId,lessonName,lessonStartTime, lessonEndTime, dayOfLesson)
                                lessonArrayList.add(lessonOfTimetable)

                            }

                            timetableRecyclerAdapter.notifyDataSetChanged()

                        }
                    }


                }

            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            Toast.makeText(requireActivity(),"Zaten ders programı sayfasındasınız.",Toast.LENGTH_LONG).show()

        } else if (item.itemId == R.id.all_exams) {

            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = DisplayTimetableFragmentDirections.actionDisplayTimetableFragmentToLogInFragment()
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
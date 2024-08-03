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
import com.example.educationassistant.adapter.ExamsRecyclerAdapter
import com.example.educationassistant.databinding.FragmentAllExamsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllExamsFragment : Fragment() {

    private var _binding: FragmentAllExamsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var examArrayList : ArrayList<Exam>
    private lateinit var examsRecyclerAdapter: ExamsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth
        db = Firebase.firestore

        examArrayList = ArrayList<Exam>()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllExamsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.examsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        examsRecyclerAdapter = ExamsRecyclerAdapter(examArrayList)
        binding.examsRecyclerview.adapter = examsRecyclerAdapter

        val userEmail = auth.currentUser?.email

        userEmail.let {

            db.collection("Exams").whereEqualTo("userEmail",it).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (value != null) {
                        if (!value.isEmpty) {

                            val documents = value.documents

                            examArrayList.clear()

                            for (document in documents) {

                                val examId = document.id
                                val examName = document.get("examName") as String
                                val examDate = document.get("examDate") as String
                                val examTime = document.get("examTime") as String

                                val exam = Exam(examId,examName, examDate, examTime)
                                examArrayList.add(exam)

                            }

                            examsRecyclerAdapter.notifyDataSetChanged()

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

            val action = AllExamsFragmentDirections.actionAllExamsFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = AllExamsFragmentDirections.actionAllExamsFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            Toast.makeText(requireActivity(),"Zaten sınavlarım sayfasındasınız.",Toast.LENGTH_LONG).show()

        } else if (item.itemId == R.id.all_assignments) {

            val action = AllExamsFragmentDirections.actionAllExamsFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = AllExamsFragmentDirections.actionAllExamsFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = AllExamsFragmentDirections.actionAllExamsFragmentToLogInFragment()
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
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
import com.example.educationassistant.adapter.AssignmentsRecyclerAdapter
import com.example.educationassistant.databinding.FragmentAllAssignmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllAssignmentsFragment : Fragment() {

    private var _binding: FragmentAllAssignmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var assignmentArrayList : ArrayList<Assignment>
    private lateinit var assignmentRecyclerAdapter: AssignmentsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth
        db = Firebase.firestore

        assignmentArrayList = ArrayList<Assignment>()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllAssignmentsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.assignmentsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        assignmentRecyclerAdapter = AssignmentsRecyclerAdapter(assignmentArrayList)
        binding.assignmentsRecyclerview.adapter = assignmentRecyclerAdapter

        val userEmail = auth.currentUser?.email
        userEmail.let {

            db.collection("Assignments").whereEqualTo("userEmail",it).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()
                } else {

                    if (value != null) {
                        if (!value.isEmpty) {

                            val documents = value.documents

                            assignmentArrayList.clear()

                            for (document in documents) {

                                val assignmentId = document.id
                                val assignmentName = document.get("assignmentName") as String
                                val assignmentDescription = document.get("assignmentDescription") as String
                                val assignmentDeadline = document.get("assignmentDeadline")

                                val assignment = Assignment(assignmentId,assignmentName,assignmentDescription, assignmentDeadline.toString())
                                assignmentArrayList.add(assignment)


                            }

                            assignmentRecyclerAdapter.notifyDataSetChanged()

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

            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            Toast.makeText(requireActivity(),"Zaten ödevlerim sayfasındasınız.",Toast.LENGTH_LONG).show()

        } else if (item.itemId == R.id.about_app) {

            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = AllAssignmentsFragmentDirections.actionAllAssignmentsFragmentToLogInFragment()
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
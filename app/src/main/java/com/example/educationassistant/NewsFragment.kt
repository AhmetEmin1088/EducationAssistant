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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.educationassistant.adapter.NewsRecyclerAdapter
import com.example.educationassistant.databinding.FragmentNewsBinding
import com.example.educationassistant.model.RSS
import com.example.educationassistant.service.IRecyclerClickListener
import com.example.educationassistant.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NewsFragment : Fragment(), IRecyclerClickListener {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth

        //initialize viewModel
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        viewModel.rssFeed.observe(viewLifecycleOwner) { rss : RSS ->

            val adapter = rss.channel!!.items?.let { NewsRecyclerAdapter(it,this) }

            binding.newsRecyclerView.adapter = adapter

        }

        viewModel.error.observe(viewLifecycleOwner) {
                error : String? ->
            Toast.makeText(requireActivity(),error,Toast.LENGTH_LONG).show()
        }

    }

    override fun onItemClicked(url: String) {

        val action = NewsFragmentDirections.actionNewsFragmentToNewsWebViewFragment(url)
        Navigation.findNavController(requireView()).navigate(action)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = NewsFragmentDirections.actionNewsFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = NewsFragmentDirections.actionNewsFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.timetable) {

            val action = NewsFragmentDirections.actionNewsFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = NewsFragmentDirections.actionNewsFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = NewsFragmentDirections.actionNewsFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = NewsFragmentDirections.actionNewsFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = NewsFragmentDirections.actionNewsFragmentToLogInFragment()
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
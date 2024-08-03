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
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentGpaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GpaFragment : Fragment() {

    private var _binding: FragmentGpaBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var allLessons: ArrayList<Lessons>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth

        allLessons = ArrayList<Lessons>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGpaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (binding.linear.childCount == 0) {
            binding.calculateButton.visibility = View.INVISIBLE
        } else {
            binding.calculateButton.visibility = View.VISIBLE
        }

        binding.addLessonButton.setOnClickListener {
            if (!binding.lessonNameTextView.text.isNullOrEmpty()) {
                val inflater = LayoutInflater.from(requireContext())
                val newLessonView = inflater.inflate(R.layout.new_lesson, null)

                val lessonName = binding.lessonNameTextView.text.toString()
                val credit = binding.creditSpinner.selectedItem.toString()
                val grade = binding.gradeSpinner.selectedItem.toString()

                newLessonView.findViewById<TextView>(R.id.newLessonNameText).text = lessonName
                newLessonView.findViewById<Spinner>(R.id.newCreditSpinner)
                    .setSelection(spinnerPositionAl(binding.creditSpinner, credit))
                newLessonView.findViewById<Spinner>(R.id.newGradeSpinner)
                    .setSelection(spinnerPositionAl(binding.gradeSpinner, grade))

                newLessonView.findViewById<Button>(R.id.deleteLessonButton).setOnClickListener {
                    binding.linear.removeView(newLessonView)
                    if (binding.linear.childCount == 0) {
                        binding.calculateButton.visibility = View.INVISIBLE
                    } else {
                        binding.calculateButton.visibility = View.VISIBLE
                    }
                }

                binding.linear.addView(newLessonView)
                binding.calculateButton.visibility = View.VISIBLE
                sifirla()
            } else {
                Toast.makeText(requireActivity(), "Ders adını giriniz!", Toast.LENGTH_LONG).show()
            }
        }

        binding.calculateButton.setOnClickListener {
            ortalamaHesapla()
        }


    }

    private fun sifirla() {
        binding.lessonNameTextView.setText("")
        binding.creditSpinner.setSelection(0)
        binding.gradeSpinner.setSelection(0)
    }

    private fun spinnerPositionAl(spinner: Spinner, aranacakDeger: String): Int {
        var index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(aranacakDeger)) {
                index = i
                break
            }
        }
        return index
    }

    fun ortalamaHesapla() {
        val deger: Int = binding.linear.childCount - 1
        var toplamNot = 0.0
        var toplamKredi = 0.0

        for (i in 0..deger) {
            val tekSatir = binding.linear.getChildAt(i)
            val geciciDers = Lessons(
                tekSatir.findViewById<TextView>(R.id.newLessonNameText).text.toString(),
                ((tekSatir.findViewById<Spinner>(R.id.newCreditSpinner).selectedItemPosition) + 1).toString(),
                tekSatir.findViewById<Spinner>(R.id.newGradeSpinner).selectedItem.toString()
            )
            allLessons.add(geciciDers)
        }

        for (oankiDers in allLessons) {
            toplamNot += harfiNotaCevir(oankiDers.lessonGrade) * (oankiDers.lessonCredit.toDouble())
            toplamKredi += oankiDers.lessonCredit.toDouble()
        }

        val sonNot: Double = toplamNot / toplamKredi
        Toast.makeText(requireContext(), "Gano: $sonNot", Toast.LENGTH_LONG).show()
        allLessons.clear()
    }

    private fun harfiNotaCevir(str: String): Double {
        var deger = 0.0
        when (str) {
            "AA" -> deger = 4.0
            "BA" -> deger = 3.5
            "BB" -> deger = 3.0
            "CB" -> deger = 2.5
            "CC" -> deger = 2.0
            "DC" -> deger = 1.5
            "DD" -> deger = 1.0
            "FD" -> deger = 0.5
            "FF" -> deger = 0.0
        }
        return deger
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = GpaFragmentDirections.actionGpaFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = GpaFragmentDirections.actionGpaFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = GpaFragmentDirections.actionGpaFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = GpaFragmentDirections.actionGpaFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = GpaFragmentDirections.actionGpaFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = GpaFragmentDirections.actionGpaFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = GpaFragmentDirections.actionGpaFragmentToLogInFragment()
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
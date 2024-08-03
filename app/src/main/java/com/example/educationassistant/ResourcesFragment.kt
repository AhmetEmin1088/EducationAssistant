package com.example.educationassistant

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.educationassistant.databinding.FragmentResourcesBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResourcesFragment : Fragment() {

    private var _binding: FragmentResourcesBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        auth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResourcesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.findResourcesButton.setOnClickListener {
            val userRequest = binding.resourcesSearchBarText.text.toString()
            val prompt = "$userRequest ile alakalı bir dizi kaynak yaz."

            binding.resourcesPageView.visibility = View.INVISIBLE

            CoroutineScope(Dispatchers.Main).launch {
                val response = withContext(Dispatchers.IO) {
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = BuildConfig.apiKey
                    )
                    try {
                        generativeModel.generateContent(prompt)
                    } catch (e: Exception) {
                        Toast.makeText(context,e.localizedMessage,Toast.LENGTH_LONG).show()
                        null
                    }
                }

                response?.text?.let {
                    val processedText = processResponse(it, userRequest)
                    binding.resourcesText.text = Html.fromHtml(processedText, Html.FROM_HTML_OPTION_USE_CSS_COLORS)
                } ?: run {
                    binding.resourcesText.text = "Kaynak bulunamadı veya bir hata oluştu."
                }
            }
        }
    }

    private fun processResponse(responseText: String, userRequest: String): String {
        val processedText = "<ol>" + responseText.replace(userRequest, "<b>$userRequest</b>", ignoreCase = true).split("\n").joinToString("</li><li>") + "</ol>";
        return processedText;
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.main_page){

            val action = ResourcesFragmentDirections.actionResourcesFragmentToHomePageFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.profile) {

            val action = ResourcesFragmentDirections.actionResourcesFragmentToProfileFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }else if (item.itemId == R.id.timetable) {

            val action = ResourcesFragmentDirections.actionResourcesFragmentToDisplayTimetableFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_exams) {

            val action = ResourcesFragmentDirections.actionResourcesFragmentToAllExamsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.all_assignments) {

            val action = ResourcesFragmentDirections.actionResourcesFragmentToAllAssignmentsFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.about_app) {

            val action = ResourcesFragmentDirections.actionResourcesFragmentToAboutAppFragment()
            Navigation.findNavController(requireView()).navigate(action)

        } else if (item.itemId == R.id.log_out) {

            auth.signOut()
            val action = ResourcesFragmentDirections.actionResourcesFragmentToLogInFragment()
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

package com.example.educationassistant.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.educationassistant.LessonOfTimetable
import com.example.educationassistant.R
import com.example.educationassistant.databinding.RecyclerRow4Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private lateinit var db: FirebaseFirestore
private var context : Context? = null

class TimetableRecyclerAdapter(private val lessonList: ArrayList<LessonOfTimetable>) :
    RecyclerView.Adapter<TimetableRecyclerAdapter.LessonHolder>() {

    class LessonHolder(val binding: RecyclerRow4Binding) : ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonHolder {

        context = parent.context

        val binding = RecyclerRow4Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonHolder(binding)
    }

    override fun getItemCount(): Int {
        return lessonList.size
    }

    override fun onBindViewHolder(holder: LessonHolder, position: Int) {

        db = Firebase.firestore

        holder.binding.lessonNameTextView.text = lessonList.get(position).lessonName
        holder.binding.timeTextView.text =
            "${lessonList.get(position).lessonStartTime} - ${lessonList.get(position).lessonEndTime}"

        when (lessonList.get(position).dayOfLesson) {

            "Pazartesi" -> holder.binding.dayImageView.setImageResource(R.drawable.monday)
            "Salı" -> holder.binding.dayImageView.setImageResource(R.drawable.tuesday)
            "Çarşamba" -> holder.binding.dayImageView.setImageResource(R.drawable.wednesday)
            "Perşembe" -> holder.binding.dayImageView.setImageResource(R.drawable.thursday)
            "Cuma" -> holder.binding.dayImageView.setImageResource(R.drawable.friday)
            "Cumartesi" -> holder.binding.dayImageView.setImageResource(R.drawable.saturday)
            "Pazar" -> holder.binding.dayImageView.setImageResource(R.drawable.sunday)
            else -> holder.binding.dayImageView.setImageResource(R.drawable.ic_launcher_background)

        }

        holder.binding.lessonCard.setOnClickListener {
            val removeButton = holder.binding.recyclerRemoveAssignmentButton
            if (removeButton.visibility == View.VISIBLE) {
                removeButton.visibility = View.GONE
                val layoutParams = removeButton.layoutParams as LinearLayout.LayoutParams
                layoutParams.width = 0
                layoutParams.height = 0
                removeButton.layoutParams = layoutParams
            } else {
                removeButton.visibility = View.VISIBLE
                val layoutParams = removeButton.layoutParams as LinearLayout.LayoutParams
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                removeButton.layoutParams = layoutParams
            }
        }

        holder.binding.recyclerRemoveAssignmentButton.setOnClickListener {

            val lessonIdToDelete = lessonList.get(position).lessonDocumentId

            db.collection("Lessons").document(lessonIdToDelete).delete().addOnSuccessListener {

                lessonList.removeAt(position)
                // RecyclerView'ı güncelle
                notifyDataSetChanged()
                // Kullanıcıya başarılı silme mesajı göster
                Toast.makeText(context, "Ders başarıyla silindi", Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }

    }

}

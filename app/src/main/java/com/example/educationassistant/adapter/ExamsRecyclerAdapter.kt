package com.example.educationassistant.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.educationassistant.Exam
import com.example.educationassistant.NotificationReceiver
import com.example.educationassistant.databinding.RecyclerRow3Binding
import com.example.educationassistant.notificationId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private lateinit var db: FirebaseFirestore

class ExamsRecyclerAdapter(private val examList : ArrayList<Exam>) : RecyclerView.Adapter<ExamsRecyclerAdapter.ExamHolder>() {

    class ExamHolder(val binding: RecyclerRow3Binding) : ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamHolder {
        val binding = RecyclerRow3Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ExamHolder(binding)
    }

    override fun getItemCount(): Int {
        return examList.size
    }

    override fun onBindViewHolder(holder: ExamHolder, position: Int) {

        db = Firebase.firestore

        holder.binding.recyclerNameText.text = examList[position].examName
        holder.binding.recyclerDateText.text = examList[position].examDate
        holder.binding.recyclerTimeText.text = examList[position].examTime

        holder.binding.recyclerRemoveExamButton.setOnClickListener {

            val examIdToDelete = examList.get(position).examDocumentId

            db.collection("Exams").document(examIdToDelete).delete().addOnSuccessListener {

                cancelNotification(holder.itemView.context,examIdToDelete)

                examList.removeAt(position)
                // RecyclerView'ı güncelle
                notifyDataSetChanged()
                // Kullanıcıya başarılı silme mesajı göster
                Toast.makeText(holder.itemView.context, "Sınav başarıyla silindi", Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                Toast.makeText(holder.itemView.context,it.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun cancelNotification(context: Context, examId: String) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        notificationIntent.putExtra("notificationId",examId.hashCode())
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

}
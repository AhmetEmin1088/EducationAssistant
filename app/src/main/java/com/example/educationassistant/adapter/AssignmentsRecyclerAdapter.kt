package com.example.educationassistant.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.educationassistant.Assignment
import com.example.educationassistant.NotificationReceiver
import com.example.educationassistant.databinding.RecyclerRow2Binding
import com.example.educationassistant.notificationId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private lateinit var db: FirebaseFirestore

class AssignmentsRecyclerAdapter(private val assignmentList : ArrayList<Assignment>) : RecyclerView.Adapter<AssignmentsRecyclerAdapter.AssignmentHolder>() {

    class AssignmentHolder(val binding : RecyclerRow2Binding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentHolder {
        val binding = RecyclerRow2Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AssignmentHolder(binding)
    }

    override fun getItemCount(): Int {
        return assignmentList.size
    }

    override fun onBindViewHolder(holder: AssignmentHolder, position: Int) {

        db = Firebase.firestore

        holder.binding.recyclerNameText.text = assignmentList[position].assignmentName
        holder.binding.recyclerDescriptionText.text = assignmentList[position].assignmentDescription
        holder.binding.recyclerDeadlineText.text = assignmentList[position].assignmentDeadline

        holder.binding.recyclerRemoveAssignmentButton.setOnClickListener {

            val assignmentIdToDelete = assignmentList.get(position).assignmentDocumentId

            db.collection("Assignments").document(assignmentIdToDelete).delete().addOnSuccessListener {

                cancelNotification(holder.itemView.context,assignmentIdToDelete)

                assignmentList.removeAt(position)
                // RecyclerView'ı güncelle
                notifyDataSetChanged()
                // Kullanıcıya başarılı silme mesajı göster
                Toast.makeText(holder.itemView.context, "Ödev başarıyla silindi", Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                Toast.makeText(holder.itemView.context,it.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun cancelNotification(context: Context, assignmentId: String) {
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
        notificationIntent.putExtra("notificationId",assignmentId.hashCode())
         // Ödevin benzersiz hash kodunu request kodu olarak kullan
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }


}
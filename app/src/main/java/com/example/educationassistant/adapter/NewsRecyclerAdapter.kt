package com.example.educationassistant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.educationassistant.databinding.RecyclerRowBinding
import com.example.educationassistant.model.Item
import com.example.educationassistant.service.IRecyclerClickListener
import com.squareup.picasso.Picasso

class NewsRecyclerAdapter(private val items : List<Item>, private val listener : IRecyclerClickListener) : RecyclerView.Adapter<NewsRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.recyclerNewsTitleText.text = items[position].title

        Picasso.get().load(items[position].content!!.url).into(holder.binding.recyclerNewsImageView)

        //event
        holder.binding.recyclerNewsImageView.setOnClickListener {
         listener.onItemClicked(items[position].link)
        }
    }


}
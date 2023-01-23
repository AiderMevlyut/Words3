package com.example.myapplication.words.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.R
import com.example.myapplication.words.model.Result

class CustomAdapterFinish(private val mListResult: List<Result>) : RecyclerView.Adapter<CustomAdapterFinish.ViewHolder>() {

    private lateinit var onItemListener: OnItemClickListener

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_finish, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mListResult[position]

        holder.tv_word_finish.text = itemsViewModel.word
        holder.tv_translate_finish.text = itemsViewModel.translate

        holder.iv_volume_finish.setOnClickListener {
            onItemListener.onItemClick(position)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mListResult.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tv_word_finish: TextView = itemView.findViewById(R.id.tv_word_finish)
        val tv_translate_finish: TextView = itemView.findViewById(R.id.tv_translate_finish)
        val iv_volume_finish: ImageView = itemView.findViewById(R.id.iv_volume_finish)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }
}
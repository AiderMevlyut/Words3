package com.example.myapplication.words.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.R
import com.example.myapplication.words.model.CategoryItem

class CustomAdapterCategory(private val mList: List<CategoryItem>) :
    RecyclerView.Adapter<CustomAdapterCategory.ViewHolder>() {

    private lateinit var onItemListener: OnItemClickListener

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.tv_category.text = itemsViewModel.categoryName
        holder.tv_progress.text = "${itemsViewModel.progress} из ${itemsViewModel.generalCount}"
        //holder.prBarHorizontal.progress = itemsViewModel.progress
        holder.cardView.setOnClickListener {
            onItemListener.onItemClick(position)
        }

        val totalNumber = itemsViewModel.generalCount
        val learnedNumber = itemsViewModel.progress

        if (((learnedNumber * 100) / totalNumber) == 100) {
            holder.prBarHorizontal.progress = 100
        }

        if (((learnedNumber * 100) / totalNumber) < 100 && ((learnedNumber * 100) / totalNumber) >= 80) {
            holder.prBarHorizontal.progress = 90
        }

        if (((learnedNumber * 100) / totalNumber) < 80 && ((learnedNumber * 100) / totalNumber) >= 50) {
            holder.prBarHorizontal.progress = 65
        }

        if (((learnedNumber * 100) / totalNumber) < 50 && ((learnedNumber * 100) / totalNumber) >= 30) {
            holder.prBarHorizontal.progress = 40
        }

        if (((learnedNumber * 100) / totalNumber) < 30 && ((learnedNumber * 100) / totalNumber) >= 10) {
            holder.prBarHorizontal.progress = 20
        }

        if (((learnedNumber * 100) / totalNumber) < 10 && ((learnedNumber * 100) / totalNumber) >= 5) {
            holder.prBarHorizontal.progress = 8
        }

        if (((learnedNumber * 100) / totalNumber) < 10 && ((learnedNumber * 100) / totalNumber) >= 0) {
            holder.prBarHorizontal.progress = 0
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tv_category: TextView = itemView.findViewById(R.id.tv_category)
        val tv_progress: TextView = itemView.findViewById(R.id.tv_progress)
        val prBarHorizontal: ProgressBar = itemView.findViewById(R.id.prBarHorizontal)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }
}
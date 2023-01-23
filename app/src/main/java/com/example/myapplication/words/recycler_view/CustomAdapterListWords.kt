package com.example.myapplication.words.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.R
import com.example.myapplication.words.model.ListWordsItem

class CustomAdapterListWords (private val mListResult: ArrayList<ListWordsItem>) :
    RecyclerView.Adapter<CustomAdapterListWords.ViewHolder>() {

    private lateinit var deleteItemClickListener: DeleteItemClickListener
    private lateinit var valueClickListener: ValueClickListener

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_list_words, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mListResult[position]

        holder.tv_word_res_ListW.text = itemsViewModel.word
        holder.tv_translate_res_ListW.text = itemsViewModel.translate

        holder.iv_volume_res_ListW.setOnClickListener{
            valueClickListener.valueClickListener(position)
        }

        holder.iv_delete_res_ListW.setOnClickListener{
            deleteItemClickListener.myOnItemDelete(position)
        }

        if(itemsViewModel.category != null){
            holder.tv_categoryListWords.visibility = View.VISIBLE
            holder.tv_categoryListWords.text = mListResult[position].category
        } else {
            holder.tv_categoryListWords.visibility = View.GONE
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mListResult.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val iv_volume_res_ListW: ImageView = itemView.findViewById(R.id.iv_volume_res_ListW)
        val tv_word_res_ListW: TextView = itemView.findViewById(R.id.tv_word_res_ListW)
        val tv_translate_res_ListW: TextView = itemView.findViewById(R.id.tv_translate_res_ListW)
        val iv_delete_res_ListW: ImageView = itemView.findViewById(R.id.iv_delete_res_ListW)
        val tv_categoryListWords: TextView = itemView.findViewById(R.id.tv_categoryListWords)
    }

    fun deleteItemClickListener(deleteItemClickListener: DeleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener
    }

    fun onValueClickListener(valueClickListener: ValueClickListener){
        this.valueClickListener = valueClickListener
    }

    interface DeleteItemClickListener {
        fun myOnItemDelete(pos: Int)
    }

    interface ValueClickListener {
        fun valueClickListener(pos: Int)
    }
}
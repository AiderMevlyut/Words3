package com.example.myapplication.words.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.words.R
import com.example.myapplication.words.model.Result
import org.w3c.dom.Text

class CustomAdapterResult(private val mListResult: ArrayList<Result>) :
    RecyclerView.Adapter<CustomAdapterResult.ViewHolder>() {

    private lateinit var onItemListener: MyOnItemClickListener
    private lateinit var onItemLongClickListener: MyOnItemLongClickListener

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_result, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mListResult[position]

        holder.tv_word_result.text = itemsViewModel.word
        holder.tv_translate_res.text = itemsViewModel.translate

        if(itemsViewModel.learned){
            holder.tv_learned_res.text = "Изучено"
        }

        holder.iv_volume_res.setOnClickListener {
            onItemListener.myOnItemClick(position)
        }

        holder.constLayoutRes.setOnLongClickListener {
            onItemLongClickListener.myOnLongClick(position)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mListResult.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val iv_volume_res: ImageView = itemView.findViewById(R.id.iv_volume_res)
        val tv_word_result: TextView = itemView.findViewById(R.id.tv_word_res)
        val tv_translate_res: TextView = itemView.findViewById(R.id.tv_translate_res)
        val tv_learned_res: TextView = itemView.findViewById(R.id.tv_learned_res)
        val constLayoutRes: ConstraintLayout = itemView.findViewById(R.id.constLayoutRes)
    }

    fun setOnItemClickListener(onItemClickListener: MyOnItemClickListener) {
        this.onItemListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: MyOnItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener
    }

    interface MyOnItemClickListener {
        fun myOnItemClick(pos: Int)
    }

    interface MyOnItemLongClickListener {
        fun myOnLongClick(pos: Int): Boolean
    }
}
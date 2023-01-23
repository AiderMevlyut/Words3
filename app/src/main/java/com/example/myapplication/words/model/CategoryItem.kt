package com.example.myapplication.words.model

import com.example.myapplication.words.new_model.Words
import java.io.Serializable


data class CategoryItem(val categoryName: String, val progress: Int, val generalCount: Int, val arrayListWords: ArrayList<Words>): Serializable
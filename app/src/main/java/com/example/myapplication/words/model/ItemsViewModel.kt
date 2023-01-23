package com.example.myapplication.words.model

data class ItemsViewModel(val category: String, val progress: String, val progressHorizontal: Int, val allSizeWord: Int){
    override fun toString(): String {
        return "ItemsViewModel(category='$category', progress='$progress', progressHorizontal='$progressHorizontal', allSizeWord=$allSizeWord)"
    }
}
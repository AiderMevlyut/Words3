package com.example.myapplication.words.model

data class ListWordsItem (val category: String?, val categoryDelete: String, val word: String, val translate: String) {

    override fun toString(): String {
        return "ListWordsItem(category=$category, word='$word', translate='$translate')"
    }
}
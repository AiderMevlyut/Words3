package com.example.myapplication.words.model

data class Result(val word: String, val translate: String, val learned: Boolean) {

    override fun toString(): String {
        return "Result(word='$word', translate='$translate', learned=$learned)"
    }
}
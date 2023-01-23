package com.example.myapplication.words.model

import java.io.Serializable

class WordObjectByJson(val word: String, val translate: String, val learned: Boolean): Serializable {
    override fun toString(): String {
        return "C(word='$word', translate='$translate', learned=$learned)"
    }
}
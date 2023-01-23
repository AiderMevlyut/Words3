package com.example.myapplication.words.new_model

import java.io.Serializable

class User(){
    var nick: String = ""
    var login: String = ""
    var password: String = ""
    var arrayListLanguage: ArrayList<Language> = arrayListOf()

    constructor(nick: String, login: String, password: String, arrayListLanguage: ArrayList<Language>): this(){
        this.nick = nick
        this.login = login
        this.password = password
        this.arrayListLanguage = arrayListLanguage
    }

    override fun toString(): String {
        return "User(nick='$nick', login='$login', password='$password', arrayListLanguage=$arrayListLanguage)"
    }
}

class Language(){
    var language: String = ""
    var category: String = ""
    var words: ArrayList<Words> = arrayListOf()

    constructor(language: String, category: String, words: ArrayList<Words>): this(){
        this.language = language
        this.category = category
        this.words = words
    }

    override fun toString(): String {
        return "Language(language='$language', category='$category', words=$words)"
    }
}

class Words(): Serializable{
    var word: String = ""
    var translate: String = ""
    var learned: Boolean = false

    constructor(word: String, translate: String, learned: Boolean): this(){
        this.word = word
        this.translate = translate
        this.learned = learned
    }

    override fun toString(): String {
        return "Words(word='$word', translate='$translate', learned=$learned)"
    }
}
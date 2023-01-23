package com.example.myapplication.words.model

class Words(
    categoryName: String,
    studiedByCategory: Int,
    allAmountWords: Int,
    language: String
) {
    var language: String = language
    val categoryName: String = categoryName
    val studiedByCategory: Int = studiedByCategory
    val allAmountWords: Int = allAmountWords
    var arrayListWords: ArrayList<String> = arrayListOf()
    var arrayListTranslate: ArrayList<String> = arrayListOf()

    constructor(
        language: String,
        categoryName: String,
        studiedByCategory: Int,
        allAmountWords: Int,
        arrayListWords: ArrayList<String>,
        arrayListTranslate: ArrayList<String>
    ) : this(categoryName, studiedByCategory, allAmountWords, language) {
        this.language = language
        this.arrayListWords = arrayListWords
        this.arrayListTranslate = arrayListTranslate
    }

    override fun toString(): String {
        return "Words(language='$language', categoryName='$categoryName', studiedByCategory=$studiedByCategory, allAmountWords=$allAmountWords, arrayListWords=$arrayListWords, arrayListTranslate=$arrayListTranslate)"
    }
}

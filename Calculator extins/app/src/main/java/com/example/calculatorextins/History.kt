package com.example.calculatorextins

object History {
    val operatii = mutableListOf<CharSequence>() //lista care contine toate operatiile facute in aplicatie
    var bazaAdaugata: Boolean = false //variabila  care retine ca am adaugat  la inceputul istoricului notificarea implicita "Select baza 10"
    var isBase10 = true
    var laDeschidere = true

    var istoricIncarcat = false

}
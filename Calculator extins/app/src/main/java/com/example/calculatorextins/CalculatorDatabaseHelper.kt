package com.example.calculatorextins

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CalculatorDatabaseHelper(context: Context) :
// calculator.bd este numele fisierului bazei de date care va fi stocat local
// 1 este versiunea bd
    SQLiteOpenHelper(context, "calculator.bd", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        //creem tabela culcul si pun """ pentru ca sa pot scrie un string pe mai multe linii
        db.execSQL ("""
            CREATE TABLE calcul (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            operatie TEXT,
            operand1 TEXT,
            operand2 TEXT,
            bazanumeratie TEXT,
            rezultat TEXT,
            dataora TEXT
            )
            """.trimIndent()) //trimIndent() doar pentru formatarea frumoasa a codului, curata indentarile inutile din string

        //creem tabela pentru istoric
        db.execSQL("""
            CREATE TABLE istoric (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            linie TEXT,
            culoare TEXT
            )
            """.trimIndent())

        //creem tabela pentru email-uri
        db.execSQL("""
            CREATE TABLE emailuri (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            adresa_email TEXT)
            """.trimIndent())
    }

    fun get_all_emails(): List<String> {
        val db = this.readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT adresa_email FROM emailuri", null)

        if(cursor.moveToFirst())
        {
            do {
                val email = cursor.getString(cursor.getColumnIndexOrThrow("adresa_email"))
                list.add(email)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS calcul")
        db.execSQL("DROP TABLE IF EXISTS istoric")
        db.execSQL("DROP TABLE IF EXISTS emailuri")
        onCreate(db)
    }
}
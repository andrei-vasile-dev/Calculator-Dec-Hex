package com.example.calculatorextins

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator



class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //incarcam istoricul din baza de date
        if(History.istoricIncarcat == false)
        {
            val dbHelper = CalculatorDatabaseHelper(this)
            val db = dbHelper.readableDatabase

            incarcaIstoric(db)
            db.close()
            History.istoricIncarcat = true
        }

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        //setam adapter-ul care gestioneaza fragmentele
        viewPager.adapter = ViewPagerAdapter(this)

        //asociem tab-urile cu ViewPager-ul
        val tabTitles = listOf("Calcul", "Istoric", "Email", "Log")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position] //setează textul fiecărui tab în funcție de poziție
        }.attach() //porneste legatura dintre cele doua
    }


    //declaram o functie pentru a naviga intre taburi din alte parti ale aplicatiei
    fun goToTab(index: Int) {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.currentItem = index
    }

    //declaram o functie care la pornirea aplicatiei citeste inserarile din tabela istoric si le pune in lista mutabila "operatii"
    fun incarcaIstoric(db:SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT linie, culoare FROM istoric", null)

        if(cursor.moveToFirst()) { //daca cursorul a returnat cel putin un rand
            do {
                val linie = cursor.getString(cursor.getColumnIndexOrThrow("linie"))
                val culoare = cursor.getString(cursor.getColumnIndexOrThrow("culoare"))

                val sb = SpannableStringBuilder(linie)
                sb.setSpan(ForegroundColorSpan(Color.parseColor(culoare)),
                    0,
                    linie.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                History.operatii.add(sb)
            } while (cursor.moveToNext()) //trece la urmatorul rand pana cand nu mai sunt
        }
        cursor.close()
    }

}
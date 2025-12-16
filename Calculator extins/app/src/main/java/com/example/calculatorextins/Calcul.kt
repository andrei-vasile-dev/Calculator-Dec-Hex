package com.example.calculatorextins

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.math.BigInteger
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Calcul.newInstance] factory method to
 * create an instance of this fragment.
 */
class Calcul : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var elementSelectat: String? = null
    private var bazaElementului: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calcul, container, false)
    }


    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var buttonA: Button
    private lateinit var buttonB: Button
    private lateinit var buttonC: Button
    private lateinit var buttonD: Button
    private lateinit var buttonE: Button
    private lateinit var buttonF: Button
    private lateinit var button0: Button
    private lateinit var buttonBack: Button
    private lateinit var button1016: Button
    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonOri: Button
    private lateinit var buttonEgal: Button
    private lateinit var text_curent: EditText
    private lateinit var text_anterior: EditText

    //declaram o lista pentru butoanele hexazecimale
    private lateinit var butoane_hexa: List<Button>


    private var operatieCurenta: String? = null
    private var operandAnterior: String = "0"

    private var copieOperatieCurenta: String? = null

    private lateinit var dbHelper: CalculatorDatabaseHelper
    private var db: SQLiteDatabase? = null

    //functie pentru salvarea datelor inainte de inchiderea aplicatiei
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text_curent", text_curent.text.toString())
        outState.putString("text_anterior", text_anterior.text.toString())
        outState.putBoolean("isBase10", History.isBase10)
        outState.putString("operatieCurenta", operatieCurenta)
        outState.putString("operandAnterior", operandAnterior)
        outState.putBoolean("laDeschidere", History.laDeschidere)
        outState.putBoolean("bazaAdaugata", History.bazaAdaugata)
        outState.putBoolean("istoricIncarcat", History.istoricIncarcat)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //legam variabilele declarate de fiecare componenta din UI
        text_curent = view.findViewById(R.id.input_field)
        text_anterior = view.findViewById(R.id.anterior)
        button1 = view.findViewById(R.id.button1)
        button2 = view.findViewById(R.id.button2)
        button3 = view.findViewById(R.id.button3)
        button4 = view.findViewById(R.id.button4)
        button5 = view.findViewById(R.id.button5)
        button6 = view.findViewById(R.id.button6)
        button7 = view.findViewById(R.id.button7)
        button8 = view.findViewById(R.id.button8)
        button9 = view.findViewById(R.id.button9)
        buttonA = view.findViewById(R.id.buttonA)
        buttonB = view.findViewById(R.id.buttonB)
        buttonC = view.findViewById(R.id.buttonC)
        buttonD = view.findViewById(R.id.buttonD)
        buttonE = view.findViewById(R.id.buttonE)
        buttonF = view.findViewById(R.id.buttonF)
        button0 = view.findViewById(R.id.button0)
        buttonBack = view.findViewById(R.id.buttonBack)
        buttonPlus = view.findViewById(R.id.buttonplus)
        buttonMinus = view.findViewById(R.id.buttonminus)
        buttonOri = view.findViewById(R.id.buttonori)
        buttonEgal = view.findViewById(R.id.buttonegal)

        text_curent.error = null


        //setam campurile text_curent si text_anterior ca sa nu poata fi selectate si sa nu se poata scrie in ele cu tastatura implicita a sistemului
        text_anterior.isFocusable = false
        text_anterior.isClickable = false
        text_anterior.isLongClickable = false
        text_anterior.isCursorVisible = false
        text_anterior.inputType = InputType.TYPE_NULL
        text_curent.showSoftInputOnFocus = false
        text_curent.isFocusable = false
        text_curent.isClickable = false
        text_curent.isLongClickable = false
        text_curent.isCursorVisible = false

        //initializam lista cu butoanele hexazecimale
        butoane_hexa = listOf(buttonA, buttonB, buttonC, buttonD, buttonE, buttonF)
        button1016 = view.findViewById<Button>(R.id.button1016)


        if(History.laDeschidere || History.isBase10)
        {
            //pentru ca la inceput baza 10 este cea implicita, butoanele hexazecimale le inactivam:
            for(buton in butoane_hexa) {
                buton.isEnabled = false
                buton.alpha = 0.5f
            }

            button1016.text = "Baza 10"
            context?.let {
                button1016.setBackgroundColor(ContextCompat.getColor(it, R.color.base10_color))
            }
            History.laDeschidere = false;
        }

        //cod pentru restaurarea datelor la repornirea aplicatiei (atunci cand se intoarce ecranul):
        if(savedInstanceState != null)
        {
            text_curent.setText(savedInstanceState.getString("text_curent", ""))
            text_anterior.setText(savedInstanceState.getString("text_anterior", ""))
            History.isBase10 = savedInstanceState.getBoolean("isBase10", true)
            operatieCurenta = savedInstanceState.getString("operatieCurenta", null)
            operandAnterior = savedInstanceState.getString("operandAnterior", "0")
            History.laDeschidere = savedInstanceState.getBoolean("laDeschidere", true)
            History.bazaAdaugata = savedInstanceState.getBoolean("bazaAdaugata", true)
            History.istoricIncarcat = savedInstanceState.getBoolean("istoricIncarcat", true)

            //refacem starea butonului bazei
            button1016.text = if (History.isBase10) "Baza 10" else "Baza 16"
            val culoare = if (History.isBase10) R.color.base10_color else R.color.base16_color
            context?.let {
                button1016.setBackgroundColor(ContextCompat.getColor(it, culoare))
            }

            //refacem si starea butoanelor hexa
            for(buton in butoane_hexa) {
                buton.isEnabled = !History.isBase10
                buton.alpha = if (History.isBase10) 0.5f else 1.0f
            }

        }


        dbHelper = CalculatorDatabaseHelper(requireContext()) //folosim o instanta a clasei CalculatorDatebaseHelper
        db = dbHelper.writableDatabase //obtinem un obiect writableDatabase de la helper

        if(History.bazaAdaugata == false)
        {
            val sb = SpannableStringBuilder("Select baza 10")
            sb.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, "Select baza 10".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            History.operatii.add(sb)//primul element care apare in istoric
            val values = ContentValues().apply {
                put("linie", "Select baza 10")
                put("culoare", "#2cd406")
            }
            db?.insert("istoric", null, values)
            History.bazaAdaugata = true
        }

        //implementam un listener pentru butonul care schimba baza
        button1016.setOnClickListener {
            val bazaVeche = if (History.isBase10) 10 else 16
            History.isBase10 = !History.isBase10
            val bazaNoua = if (History.isBase10) 10 else 16
            button1016.text = if (History.isBase10) "Baza 10" else "Baza 16"

            if(History.isBase10)
            {
                val sb = SpannableStringBuilder("Select baza 10")
                sb.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, "Select baza 10".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                History.operatii.add(sb)
                //adaugam si in baza de date
                val values = ContentValues().apply {
                    put("linie", "Select baza 10")
                    put("culoare", "#2cd406")
                }
                db?.insert("istoric", null, values)
            }
            else
            {
                val sb = SpannableStringBuilder("Select baza 16")
                sb.setSpan(ForegroundColorSpan(Color.parseColor("#FF8DA1")), 0, "Select baza 16".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                History.operatii.add(sb)
                //adaugam si in baza de date
                val values = ContentValues().apply {
                    put("linie", "Select baza 16")
                    put("culoare", "#FF8DA1")
                }
                db?.insert("istoric", null, values)
            }

            val culoare = if(History.isBase10) R.color.base10_color else R.color.base16_color
            context?.let {
                button1016.setBackgroundColor(ContextCompat.getColor(it, culoare))
            }

            //Activare/dezactivare butoane hexazecimale
            for(buton in butoane_hexa) {
                buton.isEnabled = !History.isBase10
                buton.alpha = if (History.isBase10) 0.5f else 1.0f
            }

            //Convertim textul curent si anterior in noua baza
            val currentText = text_curent.text.toString()
            val anteriorText = text_anterior.text.toString()

            if(currentText.isNotEmpty())
            {
                try {
                    val valoareCurenta = currentText.toInt(bazaVeche)
                    text_curent.setText(Integer.toString(valoareCurenta, bazaNoua).uppercase())
                } catch (e: Exception) {
                    //Va ignora conversia daca nu este valida
                }
            }

            if(anteriorText.isNotEmpty() && !anteriorText.startsWith("=")) {
                //preluam intr-un vector numarul din textul anterior alaturi de operatorul care a fost selectat pentru el
                val elemente = anteriorText.trim().split(" ")

                var valoareCuOperator = ""
                if(elemente.size >= 1) { //daca avem doua elemente (in mod normal asa trebuie sa fie mereu)
                    try {
                        val valoareAnterioara = elemente[0].toInt(bazaVeche) //transformam primul element din vector (numarul) in format zecimal
                        val operatie = if (elemente.size > 1 && elemente[1] in listOf("+", "-", "*")) " ${elemente[1]}" else "" //extragem si operatorul, daca exista (normal ar fi sa existe mereu), pastrand un spatiu in fata lui
                        val valoareConvertita = Integer.toString(valoareAnterioara, bazaNoua).uppercase() //convertim din zecimal in baza noua si facem litere mari (utile pt baza 16)
                        valoareCuOperator = "$valoareConvertita$operatie"
                        text_anterior.setText(valoareCuOperator) //resetam textul din text_anterior acum cu numarul convertit in noua baza

                    } catch (e: Exception) {
                        //Ignora conversia daca nu este valida
                    }

                }

                if(elemente.size >= 1)
                {

                    var culoare = "#2cd406"
                    val sb = SpannableStringBuilder(valoareCuOperator)
                    if(History.isBase10)
                    {
                        sb.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, valoareCuOperator.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        culoare = "#2cd406"
                    }
                    else
                    {
                        sb.setSpan(ForegroundColorSpan(Color.parseColor("#FF8DA1")), 0, valoareCuOperator.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        culoare = "#FF8DA1"
                    }
                    History.operatii.add(sb)

                    //adaugam si in baza de date
                    val values = ContentValues().apply {
                        put("linie", valoareCuOperator)
                        put("culoare", culoare)
                    }
                    db?.insert("istoric", null, values)

                }

            }

        }

        //ne definim un listener pentru butoanele (numerice 0 -> F) ale tastaturii:
        val buttonClickListener = View.OnClickListener { view ->
            val button = view as Button
            val currentText = text_curent.text.toString()
            val buttonText = button.text.toString()

            //evitam 0 la inceput:
            if(currentText.isEmpty() && buttonText == "0")
            {
                return@OnClickListener
            }

            //Daca cumva textul curent este exact "0" si se apasa altceva decat operatori, il inlocuim
            if(currentText == "0" && buttonText != "0") {
                text_curent.setText(buttonText)
            }
            else {
                text_curent.append(buttonText) //adauga textul butonului la campul text curent
            }
        }

        //pentru fiecare buton in parte, la evenimentul click se apeleaza buttonclickListener-ul definit mai sus
        button1.setOnClickListener(buttonClickListener)
        button2.setOnClickListener(buttonClickListener)
        button3.setOnClickListener(buttonClickListener)
        button4.setOnClickListener(buttonClickListener)
        button5.setOnClickListener(buttonClickListener)
        button6.setOnClickListener(buttonClickListener)
        button7.setOnClickListener(buttonClickListener)
        button8.setOnClickListener(buttonClickListener)
        button9.setOnClickListener(buttonClickListener)
        buttonA.setOnClickListener(buttonClickListener)
        buttonB.setOnClickListener(buttonClickListener)
        buttonC.setOnClickListener(buttonClickListener)
        buttonD.setOnClickListener(buttonClickListener)
        buttonE.setOnClickListener(buttonClickListener)
        buttonF.setOnClickListener(buttonClickListener)
        button0.setOnClickListener(buttonClickListener)


        //implementam functia pentru butonul back, care sterge ultimul element din imput_field (input_field referit aici prin text_curent)
        buttonBack.setOnClickListener {
            val text = text_curent.text.toString()
            if(text.isNotEmpty()) {
                text_curent.setText(text.dropLast(1))
            }
        }

        text_anterior.setText("")
        text_curent.setText("")
        buttonEgal.isEnabled = false
        buttonEgal.alpha = 0.5f

        //de definim un listener pentru operatii
        val operatieClickListener = View.OnClickListener {  view ->
            val button = view as Button
            val operatie = button.text.toString()
            val curent = text_curent.text.toString()

            if(curent.isNotEmpty()) {
                operandAnterior = curent
                operatieCurenta = operatie
                text_anterior.setText("$operandAnterior $operatieCurenta")
                text_curent.setText("")
                buttonEgal.isEnabled = true
                buttonEgal.alpha = 1.0f

                //Salvam in istoric si in baza de date:
                var culoare = "#2cd406"
                val sb = SpannableStringBuilder("$operandAnterior $operatieCurenta")
                if(History.isBase10)
                {
                    sb.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, "$operandAnterior $operatieCurenta".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    culoare = "#2cd406"
                }
                else
                {
                    sb.setSpan(ForegroundColorSpan(Color.parseColor("#FF8DA1")), 0, "$operandAnterior $operatieCurenta".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    culoare = "#FF8DA1"
                }
                History.operatii.add(sb)

                //adaugam si in baza de date
                val values = ContentValues().apply {
                    put("linie", "$operandAnterior $operatieCurenta")
                    put("culoare", culoare)
                }
                db?.insert("istoric", null, values)
            }
            else if(operatieCurenta != null && operandAnterior.isNotEmpty())
            {
                //tratam cazul in care doar se schimba operatia, textul curent fiind null:
                operatieCurenta = operatie
                text_anterior.setText("$operandAnterior $operatieCurenta")

                //si inlocuim si ultima operatie din istoric:
                if(History.operatii.isNotEmpty())
                {
                    val sb = SpannableStringBuilder("$operandAnterior $operatieCurenta")
                    if(History.isBase10)
                        sb.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else
                        sb.setSpan(ForegroundColorSpan(Color.parseColor("#FF8DA1")), 0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    History.operatii[History.operatii.lastIndex] = sb

                    //si inlocuim si in tabela istoric din bd:
                    val lastId = db?.let { getLastInsertedId(it)  }//apelam functia pe care am definit-o mai jos si care ne introarce id-ul ultimului element inserat in tabela istoric
                    if(lastId!=null) {
                        val newLine = "$operandAnterior $operatieCurenta"
                        val culoare = if(History.isBase10) "#2cd406" else "#FF8DA1"

                        val updatedValues = ContentValues().apply {
                            put("linie", newLine)
                            put("culoare", culoare)
                        }
                        db?.update("istoric", updatedValues, "id = ?", arrayOf(lastId.toString()))
                    }
                }
            }
        }

        buttonPlus.setOnClickListener(operatieClickListener)
        buttonMinus.setOnClickListener(operatieClickListener)
        buttonOri.setOnClickListener(operatieClickListener)

        //configuram butonul egal, creeand un listener pentru el:
        buttonEgal.setOnClickListener {
            val numarAnteriorsioperator = text_anterior.text.toString()//preluam textul anterior

            if(!numarAnteriorsioperator.contains(" "))
            {
                text_anterior.setText("Eroare")
                return@setOnClickListener
            }
            val numarAnterior = numarAnteriorsioperator.split(" ")[0]

            val operand2 = text_curent.text.toString() //preluam textul curent introdus
            if(numarAnterior.isNotEmpty() && operatieCurenta != null && operand2.isNotEmpty())
            {
                try {
                    val baza = if(History.isBase10) 10 else 16
                    //transformam din baza 16 in baza 10 daca este cazul, pentru a putea efectua calculele
                    val op1 = BigInteger(numarAnterior, baza)
                    val op2  = BigInteger(operand2, baza)

                    val rezultat = when (operatieCurenta)
                    {
                        "+" -> op1 + op2
                        "-" -> op1 - op2
                        "*" -> op1 * op2
                        else -> BigInteger.ZERO
                    }

                    //transformam rezultatul in text ce contine doar litere mari:
                    val rezultatText = rezultat.toString(baza).uppercase()

                    //adaugam in istoric intai al doilea operand, apoi tot calculul:
                    val operand2CuEgal = "$operand2 ="
                    val sbOP1 = SpannableStringBuilder(operand2CuEgal)
                    if(History.isBase10)
                        sbOP1.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, operand2CuEgal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else
                        sbOP1.setSpan(ForegroundColorSpan(Color.parseColor("#FF8DA1")), 0, operand2CuEgal.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    History.operatii.add(sbOP1)

                    //adaugam rezultatul si in tabela istoric din baza de date
                    val linie1 = operand2CuEgal
                    val culoare1 = if(History.isBase10) "#2cd406" else "#FF8DA1"
                    val values1 = ContentValues().apply {
                        put("linie", linie1)
                        put("culoare", culoare1)
                    }
                    db?.insert("istoric", null, values1)



                    val calculComplet = "$rezultatText = $numarAnterior $operatieCurenta $operand2"
                    val sbOP2 = SpannableStringBuilder(calculComplet)
                    if(History.isBase10)
                        sbOP2.setSpan(ForegroundColorSpan(Color.parseColor("#2cd406")), 0, calculComplet.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    else
                        sbOP2.setSpan(ForegroundColorSpan(Color.parseColor("#FF8DA1")), 0, calculComplet.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    History.operatii.add(sbOP2)

                    //adaugam rezultatul in tabela istoric din baza de date
                    val linie2 = calculComplet
                    val culoare2 = if(History.isBase10) "#2cd406" else "#FF8DA1"
                    val values2 = ContentValues().apply {
                        put("linie", linie2)
                        put("culoare", culoare2)
                    }
                    db?.insert("istoric", null, values2)


                    //------------------------------------------------------------------------------------
                    //adaugam calculul si in tabela calcul a bazei de date


                    val values = ContentValues().apply {
                        put("operatie", operatieCurenta)
                        put("operand1", numarAnterior)
                        put("operand2", operand2)
                        put("bazanumeratie", if(History.isBase10) "10" else "16")
                        put("rezultat", rezultatText)
                        put("dataora", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                    }

                    //inseram calculul in tabela
                    db?.insert("calcul", null, values)
                    //----------------------------------------------------------------------------------


                    //adaugam in campul text_antrior rezultatul, cu egal inainte:
                    val rezultatCuEgal = "= $rezultatText"
                    text_anterior.setText(rezultatCuEgal)

                    //curatam operatia curenta si textul curent:
                    text_curent.setText("")
                    copieOperatieCurenta = operatieCurenta
                    operatieCurenta = null

                    //dezactivam din nou butonul egal:
                    buttonEgal.isEnabled = false
                    buttonEgal.alpha = 0.5f

                } catch (e: Exception) {
                    Log.e("Calculator", "Eroare: ${e.message}", e)
                    text_anterior.setText("Eroare")

                }

                //URMEAZA IMPLEMENTAREA INTERACTIUNII CU SERVERUL
                //====================================================================================================
                val user = "user49"
                val bazaText = if(History.isBase10) "dec" else "hex"
                val mesajTrimis = "$user $bazaText $numarAnterior $copieOperatieCurenta $operand2"
                val urlEncoded = Uri.encode(mesajTrimis) //codificam textul intr-un format sigur pentru a fi transmis intr-un URL
                val url = "https://timf.upg-ploiesti.ro/idp/z/zdemo9/calc?q=$urlEncoded"

                //Adaugam in Log incercarea de conectare
                scrieInLog("Connect server: attempting.")

                val coadaRaspunsuri = Volley.newRequestQueue(context)
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->

                        //Loguri
                        scrieInLog("Connect server: succes.")
                        scrieInLog("Operation send: $bazaText $numarAnterior $copieOperatieCurenta $operand2.")

                        //extragem valoarea rezultatului
                        val parti = response.trim().split(Regex("\\s+")) //imparte sirul la unul sau mai multe spatii, taburi sau newline-uri
                        if (parti.size >= 3 && parti[0] == "result") {
                            val rezultatServer = parti[2]
                            text_curent.setText("$rezultatServer".uppercase()) //afisam rezultatul primit in campul text_curent

                            //scriem si logurile
                            scrieInLog("Result received: $response")
                            val rezultatLocalCuEgal = text_anterior.text.toString()
                            val rezultatLocal = rezultatLocalCuEgal.split(" ")[1]
                            if(rezultatServer.trim().uppercase() == rezultatLocal.trim())
                                scrieInLog("Result correct.")
                            else
                                scrieInLog("Result wrong.")
                        }
                        else
                        {
                            Toast.makeText(requireContext(), "Serverul nu raspunde bine.", Toast.LENGTH_SHORT).show()
                        }

                        //Log
                        scrieInLog("Disconnect.")
                    },
                    {
                        error ->
                        scrieInLog("Connect server: failed - ${error.message}.")
                        scrieInLog("Disconnect.")
                    }
                )

                coadaRaspunsuri.add(stringRequest) //adaugam cererea HTTPS in coada de executie, solicitand bibliotecii Volley sa o trimita catre server

                //==================================================================================================
            }
        }

        //preluam elementul selectat din istoric alaturi de baza in care este numarul din el
        //practic ascultam pentru rezultatul trimis de fragmentul istoric
        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { requestKey, bundle ->
            //preluam si procesam datele
            elementSelectat = bundle.getString("element_selectat")
            bazaElementului = bundle.getInt("baza", 10)

            elementSelectat?.let {
                val tablouElemente = it.trim().split(" ")
                if (tablouElemente.size >= 1) {
                    val valoare = when {
                        History.isBase10 && bazaElementului == 16 -> tablouElemente[0].toIntOrNull(16)?.toString()
                        History.isBase10 && bazaElementului == 10 -> tablouElemente[0] //elementul ramane neschimbat
                        !History.isBase10 && bazaElementului == 16 -> tablouElemente[0] //elementul ramane neschimbat
                        !History.isBase10 && bazaElementului == 10 -> {
                            val numarInt = tablouElemente[0].toIntOrNull(10)
                            numarInt?.let { Integer.toString(it, 16).uppercase() }
                        }
                        else -> null
                    }

                    valoare?.let { text_curent.setText(it) } //punem valoarea in campul text curent
                }
            }
        }
    }

    //definim o functie pentru a afla id-ul ultimului element selectat in tabela istoric din bd
    //functia primeste ca parametru un obiect de tip SQLiteDatabase
    //returneaza un int (reprezentand id-ul) sau null, daca tabela este goala
    private fun getLastInsertedId(db: SQLiteDatabase): Int? {
        //cursor este un obiect care contine rezultatul interogarii
        val cursor = db.rawQuery("SELECT id FROM istoric ORDER BY id DESC LIMIT 1", null)
        var lastId: Int? = null
        if(cursor.moveToFirst()) { //verificam daca cursor are cel putin un rand si il mutam pe primul rand
            lastId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))//obtine valoarea din coloana id a primului rand gasit
        }
        cursor.close()
        return lastId //returnam ultimul id gasit sau null daca tabela este goala
    }

    //DEFINIM O FUNCTIE CARE PRIMESTE CA SI ARGUMENT LOG-UL SI IL SALVEAZA IN FISIER
    private fun scrieInLog(mesaj: String) {
        val timestamp = SimpleDateFormat(
            "yyyyMMdd HH:mm:ss",
            Locale.getDefault()
        ).format(Date()) //preluam data si ora curenta
        val logFinal = "$timestamp $mesaj\n" //formam mesajul final cu newline la sfarsit

        try {
            val file = File(context?.filesDir, "log.txt") //se creează (sau se acceseaza daca deja este creat) fisierul „log.txt” in directorul filesDir
            file.appendText(logFinal) //adaugam mesajul la sfarsitul fisierului
        } catch (e: IOException) {
            //gestionam erorile (spatiu insuficient, fisier blocat, ...)
            Log.e("LogFragment", "Eroare la scriere log: ${e.message}", e)
        }
    }

    //inchidem baza de date la distrugerea fragmentului
    override fun onDestroyView() {
        super.onDestroyView()
        db?.let {
            if(it.isOpen) {
                it.close()
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Calcul.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Calcul().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
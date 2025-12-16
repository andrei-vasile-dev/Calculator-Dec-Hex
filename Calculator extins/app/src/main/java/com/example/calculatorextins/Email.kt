package com.example.calculatorextins

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Email.newInstance] factory method to
 * create an instance of this fragment.
 */
class Email : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var emailField: AutoCompleteTextView
    private lateinit var subjectField: EditText
    private var bodyTextView: TextView? = null
    private var scrollView: ScrollView? = null
    private lateinit var sendButton: Button

    private lateinit var emailAdapter: ArrayAdapter<String>
    private val emailuriList = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailField = view.findViewById(R.id.emailAdress)
        subjectField = view.findViewById(R.id.emailSubject)
        bodyTextView = view.findViewById(R.id.emailBody)
        scrollView = view.findViewById(R.id.emailScrollView)
        sendButton = view.findViewById(R.id.sendButton)

        //instantiem baza de date
        val dbHelper = CalculatorDatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase //obtinem o referinta la baza de date in modul de scriere

        //citim toate email-urile din tabela si le punem in lista de sugestii
        val cursor = db.rawQuery("SELECT adresa_email FROM emailuri", null)
        cursor.use {
            while (it.moveToNext()) { //cat timp mai avem randuri in tabela
                emailuriList.add(it.getString(0))
            }
        }

        //setam adapterul pentru AutoCompleteTextView
        emailAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, emailuriList)
        emailField.setAdapter(emailAdapter)

        afiseazaIstoric() //afisam istoricul


        //la apasarea butonului Trimite:
        sendButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val subject = subjectField.text.toString().trim()
            val mesaj = bodyTextView?.text.toString().trim()

            //verificam daca email-ul este gol sau invalid
            if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Te rugăm să introduci o adresa de email validă.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //verificam daca subiectul este gol
            if(subject.isEmpty()) {
                Toast.makeText(requireContext(), "Subiectul nu poate fi gol.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //verificam daca istoricul este gol pentru precautie, desi acest lucru nu ar trebui sa se intample niciodata
            if(mesaj.isEmpty()) {
                Toast.makeText(requireContext(), "Istoricul este gol, nu aveți ce să trimiteți.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //salvam adresa de email daca nu exista deja
            val emailExistCursor = db.rawQuery("SELECT COUNT(*) FROM emailuri WHERE adresa_email = ?", arrayOf(email)) //cautam adresa din campul email in tabela, sa vedem daca deja o avem
            var exists = false
            emailExistCursor.use {
                if(it.moveToFirst()) {
                    exists = it.getInt(0) > 0
                }
            }

            //daca nu exista o adaugam
            if(!exists) {
                db.execSQL("INSERT INTO emailuri(adresa_email) VALUES(?)", arrayOf(email))
                val toast = Toast.makeText(requireContext(), "Email salvat cu succes!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0) //setam sa apara la centrul ecranului
                toast.show()

                //actualizam si lista de sugestii
                val updateEmailList = dbHelper.get_all_emails()
                val updatedAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, updateEmailList)
                emailField.setAdapter(updatedAdapter)

            }

            //trimitem email-ul prin aplicatii de tip email
            val uri = Uri.parse("mailto:$email") //facem un URL unde punem adresa de email a destinatarului
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, mesaj)
            }

            try {
                startActivity(Intent.createChooser(intent, "Trimite email..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(requireContext(), "Nicio aplicație de email găsită.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        afiseazaIstoric()
    }

    private fun afiseazaIstoric() {
        val continutIstoric = History.operatii.joinToString(separator = "\n")

        if(bodyTextView != null && scrollView !=null) {
            bodyTextView?.text = continutIstoric

            scrollView?.post {
                scrollView?.fullScroll(View.FOCUS_DOWN)
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
         * @return A new instance of fragment Email.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Email().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.calculatorextins

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.appbar.MaterialToolbar
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Istoric.newInstance] factory method to
 * create an instance of this fragment.
 */
class Istoric : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_istoric, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        val adapter = object : ArrayAdapter<CharSequence> (
            requireContext(),
            android.R.layout.simple_list_item_1,
            History.operatii
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).text = getItem(position)
                return view
            }
        }
        listView.adapter = adapter
        listView.setSelection(adapter.count - 1) // pentru a se vedea in istoric ultimele rezultate introduse (auto-scroll)
        Log.d("IstoricFragment", "Nr. elemente în istoric: ${History.operatii.size}")


        listView.setOnItemClickListener { _, _, position, _ ->
            val elementSelectat = History.operatii[position].toString()
            val tablouElemente = elementSelectat.trim().split(" ") //spargem elementul selectat, ca sa extragem doar numarul din el
            if(tablouElemente.size != 3) //evitam selectarea elementelor de tip "Select baza 10 (sau 16)" din istoric. Pentru acele situatii, tablouElemente va avea lungimea 3.
            {
                var baza = 10
                val listaIstoric = History.operatii //facem o copie a listei de istoric ca sa o parcurgem mai usor
                for (i in position downTo 0) //parcurg lista de la pozitia elementului selectat spre primul element al listei
                {
                    val vector = listaIstoric[i].toString().trim().split(" ") //sparg fiecare element intr-un vector
                    //val vector = listaIstoric[i].toString().trim().split(" ") //sparg fiecare element intr-un vector
                    if(vector.size == 3) //cand gasesc primul vector de lungime 3 ma opresc pentru ca sigur este textul pe care eu il caut ("Select baza 10/16")
                    {
                        baza = vector[2].toInt() //stiu ca baza este pe ultima pozitie si o extrag
                        break
                    }

                }

                val bundle = Bundle()
                bundle.putString("element_selectat", elementSelectat)
                bundle.putInt("baza", baza)

                // Trimitem rezultatul la fragmentul Calcul
                parentFragmentManager.setFragmentResult("requestKey", bundle)

                //navigam la fragmentul calcul
                (activity as? MainActivity)?.goToTab(0)

            }
            else {
                //daca utilizatorul vrea sa selecteze din istoric exact elementele de tip "Select baza 10/16" o sa ii apara mesajul de mai jos:
                Toast.makeText(requireContext(), "Selectează un element valid!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //facem un adapter personalizat pentru listview
        val adapter = object : ArrayAdapter<CharSequence>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            History.operatii
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).text = getItem(position)
                return  view
            }
        }

        listView.adapter = adapter
        //selectam ultimul element din lista pentru a vedea cele mai recente operatii
        listView.setSelection(adapter.count - 1)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Istoric.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Istoric().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
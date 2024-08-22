package com.leiteup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.leiteup.R
import com.leiteup.model.Cow

class CowDetail : Fragment() {

    private lateinit var cow: Cow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Usa CowDetailArgs para obter o objeto Cow
            cow = CowDetailArgs.fromBundle(it).cow
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout para este fragmento
        return inflater.inflate(R.layout.fragment_cow_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.setEarring).text = cow.earring.toString()
        view.findViewById<TextView>(R.id.setName).text = cow.name
        view.findViewById<TextView>(R.id.setGender).text = cow.gender
        view.findViewById<TextView>(R.id.setBreed).text = cow.breed
        view.findViewById<TextView>(R.id.setWeight).text = cow.weight.toString() + " Kilos"
        view.findViewById<TextView>(R.id.setBirthDay).text = "12/01/2015"
        view.findViewById<TextView>(R.id.setIatf).text = if (cow.isIATF) "Sim" else "Não"
        view.findViewById<TextView>(R.id.setFather).text = cow.father
        view.findViewById<TextView>(R.id.setMother).text = cow.mother
    }
}

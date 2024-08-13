package com.leiteup.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.leiteup.R
import com.leiteup.adapter.CowAdapter
import com.leiteup.databinding.FragmentCowBinding
import com.leiteup.helper.FirebaseHelper
import com.leiteup.model.Cow
import java.lang.Exception


class CowFragment : Fragment() {

    private var _binding: FragmentCowBinding? = null
    private val binding get() = _binding!!

    private lateinit var cowAdapter: CowAdapter

    private val cowList = mutableListOf<Cow>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        getCows()
    }

    private fun initClicks() {
        binding.fabAddCow.setOnClickListener {
            findNavController().navigate(R.id.action_cowFragment_to_formCowFragment)
        }
    }

    private fun getCows() {

        val userId = FirebaseHelper.getIdUser()
        if (userId.isNullOrEmpty()) {
            return
        }

        val cowReference = FirebaseHelper.getDatabase().child("cow").child(userId)
        Log.d("Firebase", "Referência: ${cowReference.path}")

        cowReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("Firebase", "onDataChange chamado")
                // Verifique se existe algum dado no nó
                if (dataSnapshot.exists()) {
                    cowList.clear()
                    for (childSnapshot in dataSnapshot.children) {
                        // Obtenha o valor dentro do nó filho (-O0ChkfwP05ppq-n3D98)
                        val cow = childSnapshot.getValue(Cow::class.java)
                        if (cow != null) {
                            cowList.add(cow)
                            Log.d("Firebase", "Cow: ${cow.name}, Breed: ${cow.breed}, Earring: ${cow.earring}, Gender: ${cow.gender}")
                        } else {
                            Log.w("Firebase", "Objeto Cow é nulo")
                        }
                    }
                    initAdapter()
                } else {
                    Log.d("Firebase", "Nenhum dado encontrado para o usuário $userId")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("Firebase", "Falha ao ler o valor.", databaseError.toException())
            }
        })

    }

    private fun initAdapter() {
        if (_binding == null) {
            return
        }
        binding.cowList.layoutManager = LinearLayoutManager(requireContext())
        binding.cowList.setHasFixedSize(true)

        cowAdapter = CowAdapter(requireContext(), cowList) {cow ->

        }
        binding.cowList.adapter = cowAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
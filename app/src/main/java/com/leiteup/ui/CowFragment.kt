package com.leiteup.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding.fabAddCow.setOnClickListener() {
            findNavController().navigate(R.id.action_cowFragment_to_formCowFragment)
        }
    }

    private fun getCows() {
        FirebaseHelper
            .getDatabase()
            .child("cow")
            .child(FirebaseHelper.getIdUser() ?: " ")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        for(snap in snapshot.children) {
                            val cow = snap.getValue(Cow::class.java) as Cow
                            cowList.add(cow)
                        }

                        initAdapter()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Ocorreu um erro!", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initAdapter() {
        binding.cowList.layoutManager = LinearLayoutManager(requireContext())
        binding.cowList.setHasFixedSize(true)

        cowAdapter = CowAdapter(requireContext(), cowList) {task, int ->
            binding.cowList.adapter = cowAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
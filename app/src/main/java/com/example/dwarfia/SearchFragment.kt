package com.example.dwarfia
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dwarfia.adapters.DwarfListWideAdapter
import com.example.dwarfia.database.Dwarf2
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    private lateinit var dbref: DatabaseReference
    private lateinit var adp: DwarfListWideAdapter
    private lateinit var dwarfAllList: ArrayList<Dwarf2>
    private lateinit var tempAllList: ArrayList<Dwarf2>
    private lateinit var user_id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user_id = (activity as MainActivity?)!!.getUserId()
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adp = DwarfListWideAdapter()
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerview_all)
        val horizontalLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerView.adapter = adp
        recyclerView.layoutManager = horizontalLayoutManager

        dwarfAllList = arrayListOf()
        tempAllList = arrayListOf()

        dbref = FirebaseDatabase.getInstance(BuildConfig.API_KEY).getReference("Dwarfs")
        dbref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dwarfSnapshot in snapshot.children) {
                        val dwarf = dwarfSnapshot.getValue(Dwarf2::class.java)
                        dwarfAllList.add(dwarf!!)
                    }
                    tempAllList.addAll(dwarfAllList)
                    adp.submitList(tempAllList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                tempAllList.clear()
                adp.notifyDataSetChanged()
                val searchText = query!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    dwarfAllList.forEach {
                        if (it.name!!.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempAllList.add(it)
                        }
                    }

                    adp.submitList(tempAllList)
                } else {
                    tempAllList.clear()
                    tempAllList.addAll(dwarfAllList)
                    adp.submitList(tempAllList)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempAllList.clear()
                adp.notifyDataSetChanged()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    dwarfAllList.forEach {
                        if (it.name!!.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempAllList.add(it)
                        }
                    }

                    adp.submitList(tempAllList)
                } else {
                    tempAllList.clear()
                    tempAllList.addAll(dwarfAllList)
                    adp.submitList(tempAllList)
                }
                return false
            }
        })
    }
}
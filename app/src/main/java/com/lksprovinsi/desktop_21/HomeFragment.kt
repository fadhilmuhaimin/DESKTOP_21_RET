package com.lksprovinsi.desktop_21

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.lksprovinsi.desktop_21.adapters.ItemGridAdapter
import com.lksprovinsi.desktop_21.databinding.ActivityHomeBinding
import com.lksprovinsi.desktop_21.databinding.FragmentHomeBinding
import com.lksprovinsi.desktop_21.libraries.AsyncWorker
import com.lksprovinsi.desktop_21.libraries.Dialogs
import com.lksprovinsi.desktop_21.libraries.Network
import org.json.JSONArray
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentBinding: ActivityHomeBinding
    private val list: MutableList<JSONObject> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeActivity){
            parentBinding = context.binding
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.titleTv.text = "Hello ${requireContext().getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE).getString("name", "")}"

        binding.itemsRv.layoutManager = GridLayoutManager(requireContext(), 2)
        loadItems()

        return binding.root
    }

    private fun loadItems(){
        list.clear()
        val dialog = Dialogs.loading(requireContext())
        val network = Network("/api/home/item", "GET")

        AsyncWorker().apply {
            before{
                dialog.show()
            }
            background{
                network.withConnection {
                    if(network.isSuccess){
                        val res = JSONArray(network.responseBody)
                        for(i in 0 until res.length()){
                            list.add(res.getJSONObject(i))
                        }
                    }
                }
            }
            after{
                dialog.dismiss()
                if(network.isSuccess){
                    val adapter = ItemGridAdapter(list)
                    adapter.setOnClick{
                        Toast.makeText(requireContext(), "testing", Toast.LENGTH_SHORT).show()
                        (requireActivity() as HomeActivity).openDetail(it.getInt("id"))
                    }
                    binding.itemsRv.adapter = adapter
                }else{
                    Toast.makeText(requireContext(), "Cannot load items", Toast.LENGTH_SHORT).show()

                }
            }
        }.execute()
    }
}
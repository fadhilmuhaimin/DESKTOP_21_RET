package com.lksprovinsi.desktop_21

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lksprovinsi.desktop_21.adapters.CartItemAdapter
import com.lksprovinsi.desktop_21.adapters.ItemGridAdapter
import com.lksprovinsi.desktop_21.databinding.FragmentCartBinding
import com.lksprovinsi.desktop_21.libraries.AsyncWorker
import com.lksprovinsi.desktop_21.libraries.Dialogs
import com.lksprovinsi.desktop_21.libraries.Network
import org.json.JSONArray
import org.json.JSONObject

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val list: MutableList<JSONObject> = mutableListOf()
    private val services: MutableList<JSONObject> = mutableListOf()
    private var total = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.cartList.layoutManager = LinearLayoutManager(requireContext())
        binding.chkBtn.setOnClickListener{
            checkout()
        }
        loadItems()

        loadServices()

        return binding.root
    }

    private fun loadItems(){
        list.clear()
        var jsonArray = JSONArray("[]")

        val cart = requireContext().getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE).getString("cart", "")
        if(cart != ""){
            jsonArray = JSONArray(cart)
        }

        for(i in 0 until jsonArray.length()){
            list.add(jsonArray.getJSONObject(i))
            total += jsonArray.getJSONObject(i).getInt("count") * jsonArray.getJSONObject(i).getInt("price")
        }

        val adapter = CartItemAdapter(list)
        binding.cartList.adapter = adapter
        binding.totalTv.text = "Total: ${total}"
    }

    private fun loadServices(){
        services.clear()
        val dialog = Dialogs.loading(requireContext())
        val network = Network("/api/checkout/service", "GET")

        AsyncWorker().apply {
            before{
                dialog.show()
            }
            background{
                network.withConnection {
                    if(network.isSuccess){
                        val res = JSONArray(network.responseBody)
                        val list = mutableListOf<String>()
                        for(i in 0 until res.length()){
                            val json = res.getJSONObject(i)
                            services.add(json)
                            list.add("${json.getString("name")} - Rp. ${json.getInt("price")} (${json.getInt("duration")} days)")
                        }
                        binding.spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
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
                    binding.cartList.adapter = adapter
                }else{
                    Toast.makeText(requireContext(), "Cannot load items", Toast.LENGTH_SHORT).show()

                }
            }
        }.execute()
    }

    fun checkout(){
        val json = JSONObject()
        val jsonArray = JSONArray()

        for (l in list){
            val j = JSONObject()
            j.put("itemId", l.getString("id"))
            j.put("count", l.getString("count"))
            jsonArray.put(j)
        }

        json.put("userId", requireContext().getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE).getInt("id", 1))
        json.put("serviceId", services.get(binding.spinner.selectedItemPosition))
        json.put("totalPrice", total)
        json.put("orderDate", "2023-05-24")
        json.put("acceptanceDate", "2023-05-24")
        json.put("detail", jsonArray)

        val dialog = Dialogs.loading(requireContext())
        val network = Network("/api/checkout/transaction", "POST")

        AsyncWorker().apply {
            before{
                dialog.show()
            }
            background{
                network.withConnection {
                    setRequestProperty("Content-Type", "application/json")
                    network.body = json.toString()
                    outputStream.flush()
                    outputStream.close()
                    network.isSuccess
                }
            }
            after{
                dialog.dismiss()
                if(network.isSuccess){

                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()

                }
            }
        }.execute()
    }
}
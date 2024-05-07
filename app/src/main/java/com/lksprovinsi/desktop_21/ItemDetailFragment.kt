package com.lksprovinsi.desktop_21

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lksprovinsi.desktop_21.databinding.FragmentItemDetailBinding
import com.lksprovinsi.desktop_21.libraries.AsyncWorker
import com.lksprovinsi.desktop_21.libraries.Dialogs
import com.lksprovinsi.desktop_21.libraries.Network
import org.json.JSONArray
import org.json.JSONObject

class ItemDetailFragment : Fragment() {
    var itemID: Int = -1
    private lateinit var binding: FragmentItemDetailBinding
    private var item: JSONObject? = null
    var itemCount: Int = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(itemID != -1) loadItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailBinding.inflate(inflater, container, false)

        if(itemID != -1) loadItem()

        binding.totalEt.hint = itemCount.toString()

        binding.plusBtn.setOnClickListener{
            if(itemCount + 1 <= item!!.getInt("stock")){
                itemCount += 1
            }
            binding.totalEt.hint = itemCount.toString()
            countPrice()
        }

        binding.minusBtn.setOnClickListener{
            if(itemCount - 1 >= 1){
                itemCount -= 1
            }
            binding.totalEt.hint = itemCount.toString()
            countPrice()
        }

        binding.addBtn.setOnClickListener{
            add()
        }
        countPrice()

        return binding.root
    }

    private fun add(){
        val sp = requireContext().getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE)
        var data = JSONArray()
        if(sp.getString("cart", "") != ""){
            data = JSONArray(sp.getString("cart", "[]"))
        }

        var dt = JSONObject()

        for(i in 0 until data.length()){
            if(data.getJSONObject(i).getInt("id") == itemID){
                dt = data.getJSONObject(i)
            }
        }

        dt.put("id", itemID)
        dt.put("count", itemCount)
        dt.put("name", item!!.getString("name"))
        dt.put("price", item!!.getInt("price"))
        data.put(dt)
        sp.edit()
            .putString("cart", data.toString())
            .apply()
        Toast.makeText(requireContext(), "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show()
    }

    private fun countPrice(){
        val price = item!!.getInt("price")
        val total = price * itemCount
        binding.totalPriceTv.text = "Total: Rp. $total"
    }

    private fun loadItem(){
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
                                Log.d("NETWORK", "loadItem: " + res.getJSONObject(i).getInt("id"))
                                val id = res.getJSONObject(i).getInt("id")
                                if( id == itemID){
                                    item = res.getJSONObject(i)
                                }
                            }
                    }
                }
            }
            after{
                dialog.dismiss()
                if(network.isSuccess){
                    Log.d("NETWORK", "loadItem: " + item.toString())
                    binding.itemNameTv.text = item?.getString("name")
                    binding.itemDescTv.text = item?.getString("description")
                    binding.itemPriceTv.text = "Rp. ${item?.getInt("price")}, 00"
                    binding.itemStockTv.text = "Stock ${item?.getInt("stock")}"
                    loadImage(itemID)
                }
            }
        }.execute()
    }

    private fun loadImage(id: Int){
        val network = Network("/api/home/item/photo/$id", "GET")
        AsyncWorker().apply {
            background{
                network.withConnection {
                    if(network.isSuccess){
                        try{
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.itemPhotoIv.setImageBitmap(bitmap)
                        }catch (_: Exception){
                            binding.itemPhotoIv.setImageResource(R.drawable.pic_default)
                        }
                    }else{
                        binding.itemPhotoIv.setImageResource(R.drawable.pic_default)
                    }
                }
            }
        }.execute()
    }
}
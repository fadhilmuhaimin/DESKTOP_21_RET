package com.lksprovinsi.desktop_21.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lksprovinsi.desktop_21.R
import com.lksprovinsi.desktop_21.databinding.WidgetCardItemBinding
import com.lksprovinsi.desktop_21.databinding.WidgetCartCardBinding
import com.lksprovinsi.desktop_21.libraries.AsyncWorker
import com.lksprovinsi.desktop_21.libraries.Network
import org.json.JSONObject

class CartItemAdapter(private val items: MutableList<JSONObject>) : RecyclerView.Adapter<CartItemAdapter.ItemGridHolder>() {

    private var onClick: OnClick? = null

    fun setOnClick(handler: OnClick){
        onClick = handler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGridHolder {
        val binding = WidgetCartCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ItemGridHolder(binding)

        return holder
    }

    override fun onBindViewHolder(holder: ItemGridHolder, position: Int) {
        holder.setContent(items[position])
        holder.binding.itemPriceTv.setOnClickListener{ onClick?.handle(items[position]) }
        holder.binding.root.setOnClickListener{ onClick?.handle(items[position]) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemGridHolder(val binding: WidgetCartCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setContent(json: JSONObject){
            binding.itemNameTv.text = json.getString("name")
            binding.itemCountTv.text = "Count: " + json.getInt("count")
            binding.itemPriceTv.text = "Price: Rp. ${json.getInt("price")},00"
            loadImage(json.getInt("id"))
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

    fun interface OnClick{
        fun handle(item: JSONObject)
    }
}
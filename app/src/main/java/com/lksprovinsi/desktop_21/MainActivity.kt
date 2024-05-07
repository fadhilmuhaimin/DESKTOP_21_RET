package com.lksprovinsi.desktop_21

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.lksprovinsi.desktop_21.databinding.ActivityMainBinding
import com.lksprovinsi.desktop_21.libraries.AsyncWorker
import com.lksprovinsi.desktop_21.libraries.Dialogs
import com.lksprovinsi.desktop_21.libraries.Network
import com.lksprovinsi.desktop_21.network.ApiClient
import com.lksprovinsi.desktop_21.network.LoginRequest
import com.lksprovinsi.desktop_21.network.ResponseLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.loginBtn.setOnClickListener{
            login()
        }

        supportActionBar?.title = "EsemkaStore"

//        if(getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE).getInt("id", -1) != -1){
//
//            val intent = Intent(this@MainActivity, HomeActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun login(){
        val dialog = Dialogs.loading(this);
        val email: String = binding.emailEt.text.toString()
        val password: String = binding.passwordEt.text.toString()

        val call = ApiClient.apiService.login(LoginRequest(email,password))
        dialog.show()


        call.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    Log.d("TAG", "onResponse: $post")

                    post?.id?.let {
                        getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE)
                            .edit()
                            .putInt("id", it)
                            .putString("name", post.name)
                            .apply()
                    }

                    Toast.makeText(this@MainActivity, "Login success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    dialog.dismiss()
                    startActivity(intent)
                } else {

                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                // Handle failure
            }
        })





        
//        val network = Network("/api/login", "POST")
//        val json = JSONObject().apply {
//            put("email", email);
//            put("password", password);
//        }
//
//        AsyncWorker().apply {
//            before{
//                dialog.show()
//            }
//            background{
//                network.withConnection {
//                    setRequestProperty("Content-Type", "application/json")
//                    network.body = json.toString()
//                    outputStream.flush()
//                    outputStream.close()
//
//                    if (network.isSuccess){
//
//                        val res = JSONObject(network.responseBody)
//                        getSharedPreferences("DESKTOP21", Context.MODE_PRIVATE)
//                            .edit()
//                            .putInt("id", res.getInt("id"))
//                            .putString("name", res.getString("name"))
//                            .apply()
//                    }
//                }
//            }
//            after{
//                dialog.dismiss()
//                Log.d("NETWORK", "login: " + network.connection.responseCode)
//                if(network.isSuccess){
//                    Toast.makeText(this@MainActivity, "Login success", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
//                    startActivity(intent)
//                }else{
//                    Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }.execute()
    }
}
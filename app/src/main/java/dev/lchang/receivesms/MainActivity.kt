package dev.lchang.receivesms

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dev.lchang.receivesms.model.RequestModel
import dev.lchang.receivesms.model.ResultModel
import dev.lchang.receivesms.service.ResultClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var valueTextMessage: String = ""
    private var valuePhoneNumber: String = ""
    private var tvTextMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         val btnSmishing: Button = findViewById(R.id.btnSmishing)
         val tvResult: TextView = findViewById(R.id.tvResult)
        tvTextMessage = findViewById(R.id.tvTextMessage)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),111)
        }else {
            receiveMsg()
        }

        btnSmishing.setOnClickListener {
            valueTextMessage= ""
            valuePhoneNumber= ""
            tvResult.text = ""
            val requestModel = RequestModel("Título del SMS con el número $valuePhoneNumber", valueTextMessage)
            var request: Call<ResultModel>
                    = ResultClient.retrofitService.postResultSmishing(requestModel)

            request.enqueue(object: Callback<ResultModel> {
                override fun onResponse(
                    call: Call<ResultModel>,
                    response: Response<ResultModel>
                ) {
                    val isSmishing = response.body()!!.isSmishing
                    val result = response.body()!!.description
                    valueTextMessage = "$valueTextMessage $valueTextMessage"
                    tvResult.text = "El resultado fue $isSmishing - $result"
                    var colorText = if(isSmishing) Color.BLUE else Color.RED
                    tvResult.setTextColor(colorText)
                }

                override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                    Log.d("REQUEST", t.toString())
                    Toast.makeText(applicationContext,"Error al consultar",Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            receiveMsg()
    }

    private fun receiveMsg() {
        var br = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
                    for(sms in Telephony.Sms.Intents.getMessagesFromIntent(p1)){
                        //Get number and message
                        valuePhoneNumber = sms.displayOriginatingAddress
                        valueTextMessage = "$valueTextMessage ${sms.displayMessageBody}"
                        //Toast.makeText(applicationContext,number,Toast.LENGTH_LONG).show()
                    }
                    tvTextMessage?.text = valueTextMessage
                }
            }
        }
        registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }
}
package com.nibodev.statussaver.ui.screen

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker
import com.nibodev.statussaver.MainActivity
import com.nibodev.statussaver.R
import com.nibodev.statussaver.isWhatsappInstalled


@Composable
private fun DirectChatContent(modifier: Modifier = Modifier) {
    val activity = (LocalContext.current as MainActivity)
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            LayoutInflater.from(it).inflate(R.layout.whatsapp_msg_sender, null)
        },
        update = { view ->
            val phoneno = view.findViewById<EditText>(R.id.phonenumber)
            val message = view.findViewById<EditText>(R.id.messages)
            val ccpp = view.findViewById<CountryCodePicker>(R.id.ccp)
            val sendButton = view.findViewById<ImageView>(R.id.sendbutton)

            sendButton.setOnClickListener {
                val messageStr = message.text.toString().trim { it <= ' ' }
                var phoneStr = phoneno.text.toString().trim { it <= ' ' }
                if (phoneStr.isEmpty()) {
                    phoneno.error = "Please Enter Phone"
                } else if (messageStr.isEmpty()) {
                    message.error = "Please Enter Message"
                } else {
                    ccpp.registerCarrierNumberEditText(phoneno)
                    phoneStr = ccpp.fullNumber
                    val isInstalled: Boolean =
                        isWhatsappInstalled("com.whatsapp", activity.packageManager)
                    //if whatsapp is installed it will be true otherwise false
                    if (isInstalled) {
                        //Whatsapp send message using Intent
                        val whatsapp = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://api.whatsapp.com/send?phone=$phoneStr&text=$messageStr")
                        )
                        activity.startActivity(whatsapp)
                        phoneno.setText("")
                        message.setText("")
                    } else {
                        Toast.makeText(
                            activity,
                            "Whatsapp is not Installed on your Device",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    )
}




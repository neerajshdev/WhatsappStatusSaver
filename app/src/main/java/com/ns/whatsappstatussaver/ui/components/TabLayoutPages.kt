package com.ns.whatsappstatussaver.ui.components

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker
import com.ns.whatsappstatussaver.MainActivity
import com.ns.whatsappstatussaver.MainViewModel
import com.ns.whatsappstatussaver.R
import com.ns.whatsappstatussaver.models.StatusImage
import com.ns.whatsappstatussaver.models.StatusVideo
import com.ns.whatsappstatussaver.ui.layout.ScrollableRecentMediaList
import com.ns.whatsappstatussaver.ui.layout.ScrollableSavedMediaList
import com.ns.whatsappstatussaver.loadInterstitialAd
import com.ns.whatsappstatussaver.ui.router.Screen
import com.ns.whatsappstatussaver.ui.router.ScreenType
import java.io.File


@Composable
fun TabScreenOne(
    model: MainViewModel
) {
    val context = LocalContext.current
    ScrollableRecentMediaList(
        media = model.recentMedia,
        onItemChosen = {
            if (it is StatusImage) {
                model.imageEntry = it
                Screen.setScreen(ScreenType.SCREEN_IMAGE)
            } else if (it is StatusVideo) {
                model.videoEntry = it
                Screen.setScreen(ScreenType.SCREEN_VIDEO)
            }
        },
        onDownloadClick = { media ->
            model.saveFile(File(media.path), onSuccess = { str ->
                media.isSaved.value = true
                Toast.makeText(context, "File saved to: $str", Toast.LENGTH_SHORT).show()
                loadInterstitialAd(context){it.show(context as Activity)}
            })
        },
        modifier = Modifier.fillMaxSize()
    )
}


@Composable
fun TabScreenTwo(model: MainViewModel) {
    ScrollableSavedMediaList(
        media = model.savedMedia,
        onItemChosen = {
            if (it is StatusImage) {
                model.imageEntry = it
                Screen.setScreen(ScreenType.SCREEN_IMAGE)
            } else if (it is StatusVideo) {
                model.videoEntry = it
                Screen.setScreen(ScreenType.SCREEN_VIDEO)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun TabScreenThree() {
    val activity = (LocalContext.current as MainActivity)
    val composeView: View  = LocalView.current
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            LayoutInflater.from(it).inflate(R.layout.whatsapp_msg_sender, null)
        },
        update = { view ->
            val phoneno = view.findViewById<EditText>(R.id.phonenumber);
            val message = view.findViewById<EditText>(R.id.messages);
            val ccpp = view.findViewById<CountryCodePicker>(R.id.ccp);
            val sendButton = view.findViewById<ImageView>(R.id.sendbutton);

            sendButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
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
                            WhatsappAvailable("com.whatsapp", activity.packageManager)
                        //if whatsapp is installed it will be true or else isInstalled will become false
                        if (isInstalled) {
                            //Whatsapp send message using Instant
                            val whatsapp = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://api.whatsapp.com/send?phone=" + phoneStr.toString() + "&text=" + messageStr)
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
            })
        }
    )
}


private fun WhatsappAvailable(uri: String, pm: PackageManager): Boolean {
    val isInstalled: Boolean
    isInstalled = try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return isInstalled
}
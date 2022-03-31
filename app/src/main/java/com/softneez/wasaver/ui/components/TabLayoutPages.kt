package com.softneez.wasaver.ui.components

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker
import com.softneez.wasaver.MainActivity
import com.softneez.wasaver.MainViewModel
import com.softneez.wasaver.R
import com.softneez.wasaver.models.StatusImage
import com.softneez.wasaver.models.StatusVideo
import com.softneez.wasaver.ui.layout.ScrollableRecentMediaList
import com.softneez.wasaver.ui.layout.ScrollableSavedMediaList
import com.softneez.wasaver.loadInterstitialAd
import com.softneez.wasaver.ui.router.Screen
import com.softneez.wasaver.ui.router.ScreenType
import java.io.File


@Composable
fun TabScreenOne(
    model: MainViewModel
) {
    val context = LocalContext.current

    // show the media files if the path to media is knows
    // else ask the user to select the media folder
    // this is all required for android 11
    if(model.mediaSelected.value) {
        val isModelInit = remember {
            mutableStateOf(false)
        }
        // init the model content only once
        if (!isModelInit.value) {
            model.initMedia()
            isModelInit.value = true
        }

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
                model.saveFile(media.path, onSuccess = { str ->
                    media.isSaved.value = true
                    Toast.makeText(context, "File saved to: $str", Toast.LENGTH_SHORT).show()
                    loadInterstitialAd(context){it.show(context as Activity)}
                })
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        MediaSelectDiaLog() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    model.selectMedia()
                }
            }
        }
    }
}


@Composable
fun MediaSelectDiaLog(onMediaSelectClick: () -> Unit) {
    Box (modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column (horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
            Text(
                text = "OOPs! Click on Locate and select the Media folder.",
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )

            Button(onClick = onMediaSelectClick) {
                Text(
                    text = "Locate",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
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
    val isInstalled: Boolean = try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return isInstalled
}
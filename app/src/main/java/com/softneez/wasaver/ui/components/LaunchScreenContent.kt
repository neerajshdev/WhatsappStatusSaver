package com.softneez.wasaver.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softneez.wasaver.R
import com.softneez.wasaver.ui.theme.brightWhite

@Composable
fun SplashContent() {
    Box(modifier = Modifier.fillMaxSize().background(
        color = brightWhite
    )) {
        Image(
            painter = painterResource(id = R.drawable.splash_image),
            contentDescription = null,
            modifier = Modifier
                .size(144.dp, 144.dp)
                .align(alignment = Alignment.Center)
        )
        Text(
            text = "Status Saver\nfor Whatsapp",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}
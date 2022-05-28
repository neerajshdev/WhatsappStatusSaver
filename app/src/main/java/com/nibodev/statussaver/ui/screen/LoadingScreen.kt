package com.nibodev.statussaver.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.nibodev.statussaver.R
import com.nibodev.statussaver.ui.components.OnBackgroundImage
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme
import com.nibodev.statussaver.ui.theme.brightWhite

@Composable
fun LoadingScreen() {
    val lottieLoading by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.lottie_loading))
    OnBackgroundImage(
        painter = painterResource(R.drawable.bg),
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = brightWhite
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.drawable_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 32.dp)
                .size(144.dp, 144.dp)
                .align(alignment = Alignment.TopCenter)
        )

        LottieAnimation(
            composition = lottieLoading,
            iterations = Int.MAX_VALUE,
        )

        Text(
            text = "Status Saver\nfor Whatsapp",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 42.dp)
        )
    }
}

@Preview
@Composable
fun LoadingScreenPreview() {
    WhatsappStatusSaverTheme {
        LoadingScreen()
    }
}
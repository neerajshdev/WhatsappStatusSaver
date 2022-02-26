//package com.ns.whatsappstatussaver.ui.components
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.viewinterop.AndroidView
//import com.hbb20.CountryCodePicker
//import com.ns.whatsappstatussaver.ui.theme.WhatsappStatusSaverTheme
//
//@Composable
//fun PhoneNumberView(
//    phone: String,
//    countyCode: String,
//) {
//    AndroidView(factory = { context ->
//        val ccp = CountryCodePicker(context)
//        ccp.registerCarrierNumberEditText(phone)
//        ccp
//    })
//}
//
//@Preview
//@Composable
//private fun PhoneNumberViewPrev() {
//    WhatsappStatusSaverTheme {
//        PhoneNumberView(phone = "8851940485", countyCode = "91")
//    }
//}
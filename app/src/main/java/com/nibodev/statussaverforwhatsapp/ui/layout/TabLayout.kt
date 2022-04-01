package com.nibodev.statussaverforwhatsapp.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.nibodev.statussaverforwhatsapp.MainViewModel
import com.nibodev.statussaverforwhatsapp.ui.components.TabScreenOne
import com.nibodev.statussaverforwhatsapp.ui.components.TabScreenThree
import com.nibodev.statussaverforwhatsapp.ui.components.TabScreenTwo
import kotlinx.coroutines.launch


@ExperimentalPagerApi
@Composable
fun TabScreen(model: MainViewModel) {
    val pagerState = rememberPagerState(pageCount = 3)
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Tabs(pagerState = pagerState)
        TabsContent(pagerState = pagerState, model)
    }
}


@ExperimentalPagerApi
@Composable
private fun Tabs(pagerState: PagerState) {
    val list = listOf("Recent", "Saved", "Send")
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        divider = {
            TabRowDefaults.Divider(
                thickness = 4.dp,
                color = Color.White
            )
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 4.dp,
                color = MaterialTheme.colors.primary
            )
        }
    ) {
        list.forEachIndexed { index, _->
            Tab(
                text = {
                    Text(
                        list[index],
                        color = if (pagerState.currentPage == index) Color.White else Color.LightGray
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}


@ExperimentalPagerApi
@Composable
private fun TabsContent(pagerState: PagerState, model: MainViewModel) {
    HorizontalPager(state = pagerState) { page ->
        when(page) {
            0 -> TabScreenOne(model)
            1 -> TabScreenTwo(model)
            2 -> TabScreenThree()
        }
    }
}


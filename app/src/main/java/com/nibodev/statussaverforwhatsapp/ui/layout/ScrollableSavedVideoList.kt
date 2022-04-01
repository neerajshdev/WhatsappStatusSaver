package com.nibodev.statussaverforwhatsapp.ui.layout

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.nibodev.statussaverforwhatsapp.models.StatusVideo
import com.nibodev.statussaverforwhatsapp.ui.components.NativeAdUnit
import com.nibodev.statussaverforwhatsapp.ui.components.SavedVideoCard
import kotlin.random.Random

@Composable
fun ScrollableSavedVideoList(
    videos: List<StatusVideo>,
    modifier: Modifier = Modifier,
    onItemChosen: (StatusVideo) -> Unit,
    state: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = state,
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(videos.size, key = { videos[it].path }) {
            val video = videos[it]
            val itemModifier = Modifier
                .padding(vertical = 24.dp)
                .shadow(2.dp, MaterialTheme.shapes.medium)

            if (Random.nextBoolean()) NativeAdUnit(itemModifier)

            SavedVideoCard(
                image = video.thumbnail,
                label = video.duration,
                onImageClick = { onItemChosen(video) },
                modifier = itemModifier
            )
        }
        // some extra space in the end
        item {
            Spacer(modifier = Modifier.size(160.dp))
        }
    }
}
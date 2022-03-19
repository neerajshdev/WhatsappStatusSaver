package com.softneez.wasaver.ui.layout

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
import com.softneez.wasaver.models.Media
import com.softneez.wasaver.models.StatusImage
import com.softneez.wasaver.models.StatusVideo
import com.softneez.wasaver.ui.components.RecentImageCard
import com.softneez.wasaver.ui.components.NativeAdUnit
import com.softneez.wasaver.ui.components.RecentVideoCard
import kotlin.random.Random


@Composable
fun ScrollableRecentMediaList(
    media: List<Media>,
    modifier: Modifier = Modifier,
    onItemChosen: (Media) -> Unit,
    onDownloadClick: (Media) -> Unit,
    state: LazyListState = rememberLazyListState()
    ) {
    LazyColumn(
        state = state,
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(media.size, key = { (media[it]).path }) {
            val mediaItem = media[it]
            val itemModifier = Modifier
                .padding(vertical = 12.dp)
                .padding(horizontal = 8.dp)
                .shadow(4.dp, shape = MaterialTheme.shapes.medium)


            if (Random.nextBoolean()) NativeAdUnit(itemModifier)
            if (mediaItem is StatusImage) {
                RecentImageCard(
                    image = mediaItem.image,
                    isSaved = mediaItem.isSaved.value,
                    onImageClick = { onItemChosen(mediaItem) },
                    onDownloadClick = {
                        onDownloadClick(mediaItem)
                    },
                    modifier = itemModifier
                )
            } else if (mediaItem is StatusVideo) {
                RecentVideoCard(
                    image = mediaItem.thumbnail,
                    isSaved = mediaItem.isSaved.value,
                    label = mediaItem.duration,
                    onImageClick = { onItemChosen(mediaItem) },
                    onDownloadClick = {
                        onDownloadClick(mediaItem)
                    },
                    modifier = itemModifier
                )
            }

        }
        item {
            Spacer(modifier = Modifier.size(160.dp))
        }
    }
}





package com.ns.whatsappstatussaver.ui.layout

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
import com.ns.whatsappstatussaver.models.Media
import com.ns.whatsappstatussaver.models.StatusImage
import com.ns.whatsappstatussaver.models.StatusVideo
import com.ns.whatsappstatussaver.ui.components.*


@Composable
fun ScrollableSavedMediaList(
    media: List<Media>,
    modifier: Modifier = Modifier,
    onItemChosen: (Media) -> Unit,
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


            if (mediaItem is StatusImage) {
                SavedImageCard(
                    image = mediaItem.image,
                    onImageClick = { onItemChosen(mediaItem) },
                    modifier = itemModifier
                )
            } else if (mediaItem is StatusVideo) {
                SavedVideoCard(
                    image = mediaItem.thumbnail,
                    label = mediaItem.duration,
                    onImageClick = { onItemChosen(mediaItem) },
                    modifier = itemModifier
                )
            }

        }
        item {
            Spacer(modifier = Modifier.size(160.dp))
        }
    }
}
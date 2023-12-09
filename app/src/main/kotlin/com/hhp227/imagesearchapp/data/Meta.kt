package com.hhp227.imagesearchapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("is_end") val isEnd: Boolean,
    @SerialName("pageable_count") val pageableCount: Int,
    @SerialName("total_count") val totalCount: Int
)
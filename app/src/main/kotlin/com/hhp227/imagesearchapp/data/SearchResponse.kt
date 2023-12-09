package com.hhp227.imagesearchapp.data

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(val documents: List<Document>, val meta: Meta)
package com.loc.newsapp.presentation.details

import com.loc.newsapp.domain.model.Article

sealed class DetailsEvent {
    data class UpsertDeleteArticle(val article: Article) : DetailsEvent()
    object SaveArticle : DetailsEvent()
    object RemoveSideEffect : DetailsEvent()
    data class BookmarkArticle(val article: Article) : DetailsEvent()
}

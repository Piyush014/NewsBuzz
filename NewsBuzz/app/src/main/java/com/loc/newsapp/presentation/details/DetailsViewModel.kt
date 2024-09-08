package com.loc.newsapp.presentation.details

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loc.newsapp.domain.model.Article
import com.loc.newsapp.domain.usecases.news.GetSavedArticle
import com.loc.newsapp.domain.usecases.news.SelectArticle
import com.loc.newsapp.domain.usecases.news.UpsertArticle
import com.loc.newsapp.domain.usecases.news.DeleteArticle // Add this use case
import com.loc.newsapp.util.UIComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getSavedArticleUseCase: GetSavedArticle,
    private val selectArticleUseCase: SelectArticle,
    private val upsertArticleUseCase: UpsertArticle,
    private val deleteArticleUseCase: DeleteArticle // Inject the delete use case
) : ViewModel() {

    var sideEffect by mutableStateOf<UIComponent?>(null)
        private set

    var isBookmarked by mutableStateOf(false)
        private set

    fun initialize(articleUrl: String) {
        viewModelScope.launch {
            isBookmarked = getSavedArticleUseCase(url = articleUrl) != null
        }
    }

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.BookmarkArticle -> {
                viewModelScope.launch {
                    val article = event.article
                    val existingArticle = getSavedArticleUseCase(url = article.url)
                    if (existingArticle == null) {
                        upsertArticleUseCase(article = article)
                        isBookmarked = true
                        sideEffect = UIComponent.Toast("Article Bookmarked")
                    } else {
                        deleteArticleUseCase(article = article) // Remove bookmark
                        isBookmarked = false
                        sideEffect = UIComponent.Toast("Article Unbookmarked")
                    }
                }
            }
            else -> {}
        }
    }
}

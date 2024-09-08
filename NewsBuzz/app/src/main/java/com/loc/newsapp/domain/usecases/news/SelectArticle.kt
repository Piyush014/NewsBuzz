package com.loc.newsapp.domain.usecases.news

import com.loc.newsapp.data.local.NewsDao
import com.loc.newsapp.domain.model.Article
import javax.inject.Inject

class SelectArticle @Inject constructor(private val newsDao: NewsDao) {

    suspend operator fun invoke(url: String): Article? {
        return newsDao.getArticle(url)
    }
}

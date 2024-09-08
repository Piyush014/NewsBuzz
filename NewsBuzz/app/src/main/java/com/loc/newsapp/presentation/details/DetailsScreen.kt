package com.loc.newsapp.presentation.details

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.loc.newsapp.R
import com.loc.newsapp.domain.model.Article
import com.loc.newsapp.presentation.Dimens.ArticleImageHeight
import com.loc.newsapp.presentation.Dimens.MediumPadding1
import com.loc.newsapp.presentation.details.components.DetailsTopBar
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient

@Composable
fun DetailsScreen(
    article: Article,
    event: (DetailsEvent) -> Unit,
    navigateUp: () -> Unit,
    onBookmarkClick: (Article) -> Unit,
    isBookmarked: Boolean  // Accept the bookmark status
) {
    val context = LocalContext.current
    var showWebView by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        DetailsTopBar(
            onBrowsingClick = {
                showWebView = true // Toggle WebView display
            },
            onShareClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, article.url)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, null))
            },
            onBookMarkClick = {
                onBookmarkClick(article)
            },
            onBackClick = navigateUp,
            bookmarkIcon = if (isBookmarked) R.drawable.ic_dark_bookmark else R.drawable.ic_bookmark
        )

        if (showWebView) {
            AndroidView(
                factory = {
                    WebView(it).apply {
                        webViewClient = WebViewClient() // Handle navigation within WebView
                        loadUrl(article.url)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { webView ->
                    webView.loadUrl(article.url)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = MediumPadding1,
                    end = MediumPadding1,
                    top = MediumPadding1
                )
            ) {
                item {
                    AsyncImage(
                        model = ImageRequest.Builder(context = context).data(article.urlToImage)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ArticleImageHeight)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(MediumPadding1))
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.displaySmall,
                        color = colorResource(id = R.color.text_title)
                    )
                    Text(
                        text = article.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.body)
                    )
                }
            }
        }
    }
}

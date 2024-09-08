package com.loc.newsapp.presentation.onboarding

import androidx.annotation.DrawableRes
import com.loc.newsapp.R

data class Page(
    val title: String,
    val description: String,
    @DrawableRes val image: Int,
)

val pages = listOf(
    Page(
        title = "Stay Informed with Global Insights",
        description = "Explore top headlines, breaking news, and detailed reports from around the world, customized just for you.",
        image = R.drawable.onboarding1
    ),
    Page(
        title = "Personalized News Feed",
        description = "Get news that matters to you with our smart personalization. Follow your interests and receive updates on your favorite topics.",
        image = R.drawable.onboarding2
    ),
    Page(
        title = "Seamless Browsing Experience",
        description = "Enjoy a smooth and intuitive news reading experience with our user-friendly interface and advanced search options.",
        image = R.drawable.onboarding3
    )
)

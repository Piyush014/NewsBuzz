package com.loc.newsapp.presentation.news_navigator

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.loc.newsapp.R
import com.loc.newsapp.domain.model.Article
import com.loc.newsapp.presentation.bookmark.BookmarkScreen
import com.loc.newsapp.presentation.bookmark.BookmarkViewModel
import com.loc.newsapp.presentation.details.DetailsEvent
import com.loc.newsapp.presentation.details.DetailsScreen
import com.loc.newsapp.presentation.details.DetailsViewModel
import com.loc.newsapp.presentation.home.HomeScreen
import com.loc.newsapp.presentation.home.HomeViewModel
import com.loc.newsapp.presentation.navgraph.Route
import com.loc.newsapp.presentation.news_navigator.components.BottomNavigationItem
import com.loc.newsapp.presentation.news_navigator.components.NewsBottomNavigation
import com.loc.newsapp.presentation.search.SearchScreen
import com.loc.newsapp.presentation.search.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsNavigator() {
    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(icon = R.drawable.ic_home, text = "Home"),
            BottomNavigationItem(icon = R.drawable.ic_search, text = "Search"),
            BottomNavigationItem(icon = R.drawable.ic_bookmark, text = "Bookmark")
        )
    }

    val navController = rememberNavController()
    val backStackState by navController.currentBackStackEntryAsState()
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    selectedItem = when (backStackState?.destination?.route) {
        Route.HomeScreen.route -> 0
        Route.SearchScreen.route -> 1
        Route.BookmarkScreen.route -> 2
        else -> 0
    }

    val isBottomBarVisible by remember {
        derivedStateOf {
            backStackState?.destination?.route in listOf(
                Route.HomeScreen.route,
                Route.SearchScreen.route,
                Route.BookmarkScreen.route
            )
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
        if (isBottomBarVisible) {
            NewsBottomNavigation(
                items = bottomNavigationItems,
                selectedItem = selectedItem,
                onItemClick = { index ->
                    val route = when (index) {
                        0 -> Route.HomeScreen.route
                        1 -> Route.SearchScreen.route
                        2 -> Route.BookmarkScreen.route
                        else -> Route.HomeScreen.route
                    }
                    navigateToTab(navController, route)
                }
            )
        }
    }) { paddingValues ->
        val bottomPadding = paddingValues.calculateBottomPadding()
        NavHost(
            navController = navController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
            composable(route = Route.HomeScreen.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val articles = viewModel.news.collectAsLazyPagingItems()
                HomeScreen(
                    articles = articles,
                    navigateToSearch = {
                        navigateToTab(navController, Route.SearchScreen.route)
                    },
                    navigateToDetails = { article ->
                        navigateToDetails(navController, article)
                    }
                )
            }
            composable(route = Route.SearchScreen.route) {
                val viewModel: SearchViewModel = hiltViewModel()
                val state = viewModel.state.value
                OnBackClickStateSaver(navController)
                SearchScreen(
                    state = state,
                    event = viewModel::onEvent,
                    navigateToDetails = { article ->
                        navigateToDetails(navController, article)
                    }
                )
            }
            composable(route = Route.DetailsScreen.route) {
                val viewModel: DetailsViewModel = hiltViewModel()
                val article = navController.previousBackStackEntry?.savedStateHandle?.get<Article?>("article")
                article?.let {
                    LaunchedEffect(article.url) {
                        viewModel.initialize(article.url)
                    }
                    DetailsScreen(
                        article = it,
                        event = viewModel::onEvent,
                        navigateUp = { navController.navigateUp() },
                        onBookmarkClick = {
                            viewModel.onEvent(DetailsEvent.BookmarkArticle(it))
                        },
                        isBookmarked = viewModel.isBookmarked
                    )
                } ?: run {
                    Log.e("NewsNavigator", "Article is null")
                    navController.navigateUp()
                }
            }

            composable(route = Route.BookmarkScreen.route) {
                val viewModel: BookmarkViewModel = hiltViewModel()
                val state = viewModel.state.value
                OnBackClickStateSaver(navController)
                BookmarkScreen(
                    state = state,
                    navigateToDetails = { article ->
                        navigateToDetails(navController, article)
                    }
                )
            }
        }
    }
}

@Composable
fun OnBackClickStateSaver(navController: NavController) {
    BackHandler {
        navigateToTab(navController, Route.HomeScreen.route)
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun navigateToDetails(navController: NavController, article: Article) {
    navController.currentBackStackEntry?.savedStateHandle?.set("article", article)
    navController.navigate(Route.DetailsScreen.route)
}

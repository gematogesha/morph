package com.wheatley.morph.presentation.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
class StatisticsScreen: Screen{

    @Composable
    override fun Content() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text("Статистика")
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            content = { innerPadding ->
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        LazyColumn(
                            contentPadding = innerPadding,
                            modifier = Modifier
                                .fillMaxSize()
                        ){
                            item {
                                Text ("Text")
                            }
                        }
                    }
                }
            }
        )
    }
}





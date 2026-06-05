package com.example.myapplication.ui.search

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.SearchHistoryRepository
import com.example.myapplication.database.AppDatabase
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    viewModel: SearchViewModel? = null,
) {
    val context = LocalContext.current
    val actualViewModel = viewModel ?: remember {
        val dao = AppDatabase.getDatabase(context).searchHistoryDao()
        SearchViewModel(SearchHistoryRepository(dao))
    }
    val history by actualViewModel.history.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var keyword by remember { mutableStateOf("") }

    fun submit(input: String) {
        val savedKeyword = actualViewModel.submitSearch(input) ?: return
        keyboardController?.hide()
        onSearch(Uri.encode(savedKeyword))
    }

    LaunchedEffect(Unit) {
        delay(250)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 10.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = Color(0xFF222222),
                )
            }
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                placeholder = {
                    Text(text = "搜索视频")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF888888),
                    )
                },
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        submit(keyword)
                    }
                ),
            )
            Button(
                onClick = { submit(keyword) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD81E06),
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(text = "搜索")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── 热门搜索 ──
        Text(
            text = "热门搜索",
            color = Color(0xFF222222),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            listOf("今日头条" to "1", "旅行攻略" to "2", "美食探店" to "3", "城市漫步" to "4", "科技数码" to "5", "电影推荐" to "6")
                .forEach { (word, rank) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { submit(word) }
                            .padding(vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val rankColor = when (rank) {
                            "1" -> Color(0xFFD81E06)
                            "2" -> Color(0xFFFF6B35)
                            "3" -> Color(0xFFFFB347)
                            else -> Color(0xFF999999)
                        }
                        Text(
                            text = rank,
                            color = rankColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = word,
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
        }

        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "搜索历史",
                color = Color(0xFF222222),
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
            )
            if (history.isNotEmpty()) {
                TextButton(onClick = actualViewModel::clearHistory) {
                    Text(text = "清空")
                }
            }
        }

        if (history.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "暂无搜索历史",
                    color = Color(0xFF888888),
                    fontSize = 16.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                items(
                    items = history,
                    key = { it.id },
                ) { item ->
                    SearchHistoryRow(
                        item = item,
                        onClick = { submit(it) },
                        onDeleteClick = actualViewModel::deleteHistory,
                    )
                    HorizontalDivider(color = Color(0xFFE8E8E8))
                }
            }
        }
    }
}

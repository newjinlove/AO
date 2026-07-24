package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.FeedPostEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: AeoViewModel) {
    val posts by viewModel.feedPosts.collectAsState()
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var selectedPostForDetail by remember { mutableStateOf<FeedPostEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreatePostDialog = true },
                icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White) },
                text = { Text("피드 쓰기", fontWeight = FontWeight.Bold) },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color(0xFFF2F2F2)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts, key = { it.id }) { post ->
                FeedPostCardItem(
                    post = post,
                    onClick = { selectedPostForDetail = post },
                    onToggleFollow = { viewModel.toggleFollowUser(post.authorDid) }
                )
            }
        }
    }

    if (selectedPostForDetail != null) {
        Dialog(
            onDismissRequest = { selectedPostForDetail = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                FeedPostDetailScreen(
                    post = selectedPostForDetail!!,
                    onBack = { selectedPostForDetail = null }
                )
            }
        }
    }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onConfirm = { content ->
                viewModel.createPost(content)
                showCreatePostDialog = false
            }
        )
    }
}

@Composable
fun FeedPostCardItem(
    post: FeedPostEntity,
    onClick: () -> Unit,
    onToggleFollow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column {
                        Text(
                            text = post.authorName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = "${post.authorHandle} · DID 인증됨",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, CircleShape)
                        .clip(CircleShape)
                        .clickable { onToggleFollow() }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "팔로우",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color(0xFF1A1C1E),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "좋아요",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${post.likeCount}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "댓글",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "댓글 보기",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPostDetailScreen(
    post: FeedPostEntity,
    onBack: () -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val comments = remember {
        mutableStateListOf(
            "역삼동 이웃님 좋은 정보 감사합니다! 👍",
            "AO 탈중앙 네트워크 활성화 응원합니다."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("피드 상세", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "공유", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("따뜻한 댓글을 남겨보세요...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                comments.add(commentText)
                                commentText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("등록", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Author Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827))
                    Text("${post.authorHandle} · DID 검증됨 · 10분 전", fontSize = 12.sp, color = Color(0xFF6B7280))
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            // Post Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = post.content,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF1F2937)
                )

                // Attached Photo Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_marketplace_camera_1784694609353),
                        contentDescription = "첨부 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = { isLiked = !isLiked }) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "좋아요",
                                tint = if (isLiked) Color.Red else Color(0xFF6B7280)
                            )
                        }
                        Text("${post.likeCount + if (isLiked) 1 else 0} 좋아요", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                    }

                    Text("댓글 ${comments.size}개", fontSize = 13.sp, color = Color(0xFF6B7280))
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)

            // Comments List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("💬 이웃들의 댓글", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))

                comments.forEachIndexed { index, comment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E7EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B5563))
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text("역삼동 이웃 ${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(comment, fontSize = 13.sp, color = Color(0xFF1F2937))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onConfirm: (content: String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("동네소식") }
    var hasPhoto by remember { mutableStateOf(false) }

    val tags = listOf("동네소식", "자유게시판", "정보공유", "질문답변")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 피드 작성", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Topic chips
                Text("🏷️ 주제 태그 선택", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { tag ->
                        val isSelected = tag == selectedTag
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTag = tag },
                            label = { Text(tag, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF4B5563)
                            )
                        )
                    }
                }

                // Text field
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("역삼동 지역 소식이나 이웃들과 나누고 싶은 이야기를 적어보세요...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 4
                )

                // Attach Photo button
                OutlinedButton(
                    onClick = { hasPhoto = !hasPhoto },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (hasPhoto) Color(0xFFF3F4F6) else Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (hasPhoto) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hasPhoto) "사진 1장 첨부됨" else "사진/이미지 첨부하기",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (content.isNotBlank()) onConfirm("[$selectedTag] $content") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("게시하기", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = Color(0xFF6B7280)) }
        }
    )
}

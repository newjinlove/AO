package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.local.UserEntity
import com.example.ui.theme.MonoMetaMaskGold
import com.example.ui.viewmodel.AeoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDialog(
    user: UserEntity,
    viewModel: AeoViewModel,
    onDismiss: () -> Unit
) {
    BackHandler(enabled = true) {
        onDismiss()
    }

    val followedDids by viewModel.followedUserDids.collectAsState()
    val isFollowing = followedDids.contains(user.did)

    val fundings by viewModel.profileFundings.collectAsState()
    val items by viewModel.profileMarketplaceItems.collectAsState()
    val posts by viewModel.profileFeedPosts.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: 모금, 1: 거래, 2: 피드

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("프로필 정보", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "닫기", tint = Color.Black)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                containerColor = Color.White
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Profile Header Card
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFFE5E7EB), CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = user.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )

                            Text(
                                text = "${user.handle} · ${user.location}",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "지갑: ${user.walletAddress}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF374151),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = user.bio,
                                fontSize = 13.sp,
                                color = Color(0xFF4B5563),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons Row (친구추가 & 채팅하기)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleFollowUser(user.did) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, if (isFollowing) Color(0xFF9CA3AF) else Color.Black),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isFollowing) Color(0xFFF3F4F6) else Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                                        contentDescription = null,
                                        tint = if (isFollowing) Color(0xFF4B5563) else Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isFollowing) "친구 해제" else "친구 추가",
                                        color = if (isFollowing) Color(0xFF4B5563) else Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.openDirectChatWith(user)
                                        onDismiss()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubble,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "1:1 채팅하기",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 8.dp)
                    }

                    // Content Section Sub-Tabs (모금 / 거래 / 피드)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("개설 모금 (${fundings.size})", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.Black,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE5E7EB),
                                    labelColor = Color(0xFF374151)
                                )
                            )

                            FilterChip(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                label = { Text("판매 거래 (${items.size})", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.Black,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE5E7EB),
                                    labelColor = Color(0xFF374151)
                                )
                            )

                            FilterChip(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                label = { Text("작성 피드 (${posts.size})", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.Black,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE5E7EB),
                                    labelColor = Color(0xFF374151)
                                )
                            )
                        }
                    }

                    // List Items according to selected tab
                    if (selectedTab == 0) {
                        if (fundings.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("개설한 모금 프로젝트가 없습니다.", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(fundings, key = { "prof_fund_${it.id}" }) { funding ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectFunding(funding.id)
                                                viewModel.selectTab(com.example.ui.viewmodel.AeoTab.FUNDING)
                                                onDismiss()
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(funding.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(funding.description, maxLines = 2, fontSize = 12.sp, color = Color(0xFF6B7280))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("목표금액: ${String.format("%,d", funding.targetAmount)} sats", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MonoMetaMaskGold)
                                        }
                                    }
                                }
                            }
                        }
                    } else if (selectedTab == 1) {
                        if (items.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("등록한 중고 거래 물품이 없습니다.", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(items, key = { "prof_item_${it.id}" }) { item ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.img_marketplace_camera_1784694609353),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .clip(RoundedCornerShape(10.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("${item.category} · ${item.location}", fontSize = 11.sp, color = Color(0xFF6B7280))
                                                Text("${String.format("%,d", item.price)} 원", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (posts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("작성한 피드 글이 없습니다.", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(posts, key = { "prof_post_${it.id}" }) { post ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(post.content, fontSize = 13.sp, color = Color(0xFF1F2937))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("좋아요 ${post.likeCount} · ${post.location}", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

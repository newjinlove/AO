package com.example.ui.screens

import com.example.util.AquaWalletUtil
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.WalletModal
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoViewModel

@Composable
fun ProfileScreen(viewModel: AeoViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val myFundings by viewModel.myFundings.collectAsState()
    val myMarketplaceItems by viewModel.myMarketplaceItems.collectAsState()
    val myFeedPosts by viewModel.myFeedPosts.collectAsState()

    var showWalletModal by remember { mutableStateOf(false) }
    var selectedActivityTab by remember { mutableStateOf(0) } // 0: 모금, 1: 거래, 2: 피드

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User Profile Header Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
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

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUser?.name ?: "최민준",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1C1E)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentUser?.handle ?: "@ao_leader",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF6B7280)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentUser?.location ?: "서울 강남구 역삼1동",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentUser?.bio?.replace("DID & ", "") ?: "역삼동 지역공동체 기획자 | AO Decentralized Governance",
                            fontSize = 13.sp,
                            color = Color(0xFF1A1C1E)
                        )
                    }
                }
            }

            // Aqua Wallet Status Card
            item {
                val context = LocalContext.current
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MonoMetaMaskGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Aqua Wallet 연동됨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Bitcoin & Liquid Network", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                                }
                            }

                            Text(
                                text = "아쿠아 바로가기 ↗",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MonoMetaMaskGold,
                                modifier = Modifier.clickable {
                                    AquaWalletUtil.launchAquaWallet(context)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1F2937), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("보유 자산 (Aqua)", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                                Text("125,000 sats", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoMetaMaskGold, fontFamily = FontFamily.Monospace)
                            }

                            Text(
                                text = currentUser?.walletAddress ?: "0x7a83F99b2C1d445E81A912",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // My Activity Header & Sub-Tabs
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "나의 활동 내역",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedActivityTab == 0,
                            onClick = { selectedActivityTab = 0 },
                            modifier = Modifier.weight(1f),
                            label = {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("나의 모금 (${myFundings.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF4B5563)
                            )
                        )

                        FilterChip(
                            selected = selectedActivityTab == 1,
                            onClick = { selectedActivityTab = 1 },
                            modifier = Modifier.weight(1f),
                            label = {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("나의 거래 (${myMarketplaceItems.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF4B5563)
                            )
                        )

                        FilterChip(
                            selected = selectedActivityTab == 2,
                            onClick = { selectedActivityTab = 2 },
                            modifier = Modifier.weight(1f),
                            label = {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("나의 피드 (${myFeedPosts.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF4B5563)
                            )
                        )
                    }
                }
            }

            // Tab Content Items
            when (selectedActivityTab) {
                0 -> { // 나의 모금
                    if (myFundings.isEmpty()) {
                        item {
                            EmptyActivityCard(text = "개설 또는 기여한 모금이 없습니다.")
                        }
                    } else {
                        items(myFundings, key = { it.id }) { funding ->
                            FundingCardItem(
                                funding = funding,
                                onClick = {
                                    viewModel.selectFunding(funding.id)
                                    viewModel.selectedTab.value = com.example.ui.viewmodel.AeoTab.FUNDING
                                },
                                onCreatorClick = { },
                                onDirectFund = {
                                    viewModel.selectFunding(funding.id)
                                    viewModel.showContributeDialog.value = true
                                }
                            )
                        }
                    }
                }
                1 -> { // 나의 거래
                    if (myMarketplaceItems.isEmpty()) {
                        item {
                            EmptyActivityCard(text = "등록한 중고거래 상품이 없습니다.")
                        }
                    } else {
                        items(myMarketplaceItems, key = { it.id }) { item ->
                            MarketplaceCardItem(
                                item = item,
                                onClick = {
                                    viewModel.selectedMarketplaceItem.value = item
                                    viewModel.selectedTab.value = com.example.ui.viewmodel.AeoTab.MARKETPLACE
                                }
                            )
                        }
                    }
                }
                2 -> { // 나의 피드
                    if (myFeedPosts.isEmpty()) {
                        item {
                            EmptyActivityCard(text = "작성한 피드 글이 없습니다.")
                        }
                    } else {
                        items(myFeedPosts, key = { it.id }) { post ->
                            FeedPostCardItem(
                                post = post,
                                onClick = {
                                    viewModel.selectedFeedPost.value = post
                                    viewModel.selectedTab.value = com.example.ui.viewmodel.AeoTab.FEED
                                },
                                onAuthorClick = { },
                                onToggleFollow = { }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showWalletModal) {
        WalletModal(
            currentUser = currentUser,
            onDismiss = { showWalletModal = false },
            onBuyCryptoClick = { viewModel.showBuyCryptoDialog.value = true }
        )
    }
}

@Composable
fun EmptyActivityCard(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
        }
    }
}



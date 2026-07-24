package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.WalletModal
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoViewModel

@Composable
fun ProfileScreen(viewModel: AeoViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    var showWalletModal by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            text = currentUser?.bio ?: "역삼동 지역공동체 기획자 | DID & Decentralized Governance",
                            fontSize = 13.sp,
                            color = Color(0xFF1A1C1E)
                        )
                    }
                }
            }

            // MetaMask DID Status Card
            item {
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
                                Text("메타마스크 DID 연동됨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = { showWalletModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("지갑 상세", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DID: ${currentUser?.did ?: "did:ao:0x7a83f99b2c1d"}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // My Governance & Activity Summary Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "나의 참정권 및 모금 내역",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("총 모금 기여", fontSize = 11.sp, color = Color(0xFF6B7280))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("800,000원", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            Divider(modifier = Modifier.height(32.dp).width(1.dp), color = Color(0xFFE0E0E0))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("보유 참정권", fontSize = 11.sp, color = Color(0xFF6B7280))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("800,000 VP", fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black)
                            }

                            Divider(modifier = Modifier.height(32.dp).width(1.dp), color = Color(0xFFE0E0E0))

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("참여 안건", fontSize = 11.sp, color = Color(0xFF6B7280))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("12건", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }

            // Action Lists
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedCard(
                        onClick = { viewModel.selectedTab.value = com.example.ui.viewmodel.AeoTab.FUNDING },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("나의 모금 및 오픈채팅 목록", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF6B7280))
                        }
                    }

                    OutlinedCard(
                        onClick = { viewModel.selectedTab.value = com.example.ui.viewmodel.AeoTab.MARKETPLACE },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("나의 중고거래 내역", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF6B7280))
                        }
                    }

                    OutlinedCard(
                        onClick = { viewModel.selectedTab.value = com.example.ui.viewmodel.AeoTab.FEED },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DynamicFeed, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("내가 쓴 피드 글", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF6B7280))
                        }
                    }
                }
            }
        }
    }

    if (showWalletModal) {
        WalletModal(
            currentUser = currentUser,
            onDismiss = { showWalletModal = false }
        )
    }
}


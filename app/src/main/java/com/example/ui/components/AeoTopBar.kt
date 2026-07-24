package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeoTopBar(
    selectedTab: AeoTab,
    currentUser: UserEntity?,
    onWalletClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable { /* Location switch dialog */ }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ao_anarchist),
                    contentDescription = "AO Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(26.dp)
                        .aspectRatio(1408f / 768f)
                        .clip(RoundedCornerShape(6.dp))
                )
                Text(
                    text = "에이오",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "• ${currentUser?.location ?: "역삼1동"}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "동네 선택",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Right Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // High Density Wallet / DID Pill
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFE8E8E8))
                        .border(1.dp, Color(0xFFD0D0D0), CircleShape)
                        .clickable { onWalletClick() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = if (currentUser?.walletAddress != null && currentUser.walletAddress.length > 8)
                                "${currentUser.walletAddress.take(5)}...${currentUser.walletAddress.takeLast(3)}"
                            else "0x71C...4F2",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun AeoBottomNavBar(
    selectedTab: AeoTab,
    onTabSelected: (AeoTab) -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE5E7EB))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AeoTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val icon = when (tab) {
                        AeoTab.FUNDING -> if (isSelected) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance
                        AeoTab.MARKETPLACE -> if (isSelected) Icons.Filled.Storefront else Icons.Outlined.Storefront
                        AeoTab.FEED -> if (isSelected) Icons.Filled.DynamicFeed else Icons.Outlined.DynamicFeed
                        AeoTab.CHAT -> if (isSelected) Icons.Filled.Forum else Icons.Outlined.Forum
                        AeoTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) Color.Black else Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}


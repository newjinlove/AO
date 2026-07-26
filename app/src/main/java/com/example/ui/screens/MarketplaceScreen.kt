package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import com.example.data.local.MarketplaceItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(viewModel: AeoViewModel) {
    val items by viewModel.marketplaceItems.collectAsState()
    val selectedItemFromVm by viewModel.selectedMarketplaceItem.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var showPostDialog by remember { mutableStateOf(false) }
    var selectedItemForDetail by remember { mutableStateOf<MarketplaceItemEntity?>(null) }

    LaunchedEffect(selectedItemFromVm) {
        if (selectedItemFromVm != null) {
            selectedItemForDetail = selectedItemFromVm
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showPostDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = { Text("글쓰기", fontWeight = FontWeight.Bold) },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color(0xFFF2F2F2)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Input - Compact Top Padding
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchQuery.value = it
                },
                placeholder = { Text("역삼동 중고 물품, 디지털, 가구 검색...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(Color.White, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                singleLine = true
            )

            // Category Chips (Monochrome)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text("전체", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Black,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text("디지털/가전") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFE5E7EB),
                        labelColor = Color(0xFF374151)
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text("가구/인테리어") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFE5E7EB),
                        labelColor = Color(0xFF374151)
                    )
                )
            }

            // Marketplace Item List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    MarketplaceCardItem(
                        item = item,
                        onClick = { selectedItemForDetail = item }
                    )
                }
            }
        }
    }

    if (selectedItemForDetail != null) {
        Dialog(
            onDismissRequest = {
                selectedItemForDetail = null
                viewModel.selectedMarketplaceItem.value = null
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                MarketplacePostDetailScreen(
                    item = selectedItemForDetail!!,
                    onBack = {
                        selectedItemForDetail = null
                        viewModel.selectedMarketplaceItem.value = null
                    },
                    onStartChat = {
                        val sellerUser = com.example.data.local.UserEntity(
                            did = selectedItemForDetail!!.sellerDid,
                            handle = "@seller",
                            name = selectedItemForDetail!!.sellerName,
                            bio = "판매자",
                            avatarUri = selectedItemForDetail!!.sellerAvatar,
                            walletAddress = "0x..."
                        )
                        viewModel.openDirectChatWith(sellerUser)
                        selectedItemForDetail = null
                    },
                    onSellerClick = {
                        viewModel.showUserProfileByDid(selectedItemForDetail!!.sellerDid)
                    }
                )
            }
        }
    }

    // Post Item Dialog
    if (showPostDialog) {
        CreateMarketplaceItemDialog(
            onDismiss = { showPostDialog = false },
            onConfirm = { title, cat, price, desc ->
                viewModel.postItem(title, cat, price, desc)
                showPostDialog = false
            }
        )
    }
}

@Composable
fun MarketplaceCardItem(
    item: MarketplaceItemEntity,
    onClick: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Thumbnail
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(90.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_marketplace_camera_1784694609353),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.location} · ${item.sellerName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${numberFormat.format(item.price)}원",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val satsPrice = (item.price * 100 / 120)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MonoDarkCard
                    ) {
                        Text(
                            text = "${numberFormat.format(satsPrice)} sats",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoMetaMaskGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(" ${item.chatCount}  ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(" ${item.likeCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplacePostDetailScreen(
    item: MarketplaceItemEntity,
    onBack: () -> Unit,
    onStartChat: () -> Unit,
    onSellerClick: () -> Unit = {}
) {
    BackHandler(enabled = true) {
        onBack()
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    val btcPriceFormatted = String.format(Locale.US, "%.5f", item.price / 120000000.0)
    var isLiked by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("거래 물품 상세", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "공유",
                            tint = Color.Black
                        )
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
                    IconButton(
                        onClick = { isLiked = !isLiked }
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "관심",
                            tint = if (isLiked) Color.Red else Color(0xFF9CA3AF)
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(28.dp)
                            .padding(horizontal = 8.dp),
                        color = Color(0xFFE5E7EB)
                    )

                    Column {
                        Text(
                            text = "${numberFormat.format(item.price)}원",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        val satsPriceDetail = (item.price * 100 / 120)
                        Text(
                            text = "${numberFormat.format(satsPriceDetail)} sats (Aqua Wallet P2P)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onStartChat,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
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
            // Main Product Image Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFFF3F4F6))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_marketplace_camera_1784694609353),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Seller Profile Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSellerClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.sellerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "검증된 이웃",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.location,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            // Item Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Text(
                    text = "${item.category} · 등록 2시간 전",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )

                Text(
                    text = item.description,
                    fontSize = 15.sp,
                    color = Color(0xFF374151),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Safe Trade Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "📍 거래 희망 장소: 역삼1동 주민센터 앞 또는 역삼역 3번 출구",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = "🤝 거래 방식: 현금 또는 Aqua Wallet sats / USDt P2P 에스크로 결제 가능",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563)
                        )
                        Text(
                            text = "🛡️ AO 이웃 인증을 완료한 안전 거래 상대입니다.",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "관심 ${item.likeCount + if (isLiked) 1 else 0} · 채팅 ${item.chatCount} · 조회 142",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateMarketplaceItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, price: Long, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("디지털/가전") }
    var priceText by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var hasPhotoAttached by remember { mutableStateOf(true) }

    val categories = listOf("디지털/가전", "가구/인테리어", "의류/잡화", "도서/티켓", "기타 중고")
    val numberFormat = remember { java.text.NumberFormat.getNumberInstance(java.util.Locale.KOREA) }
    val estimatedBtc = (priceText.toLongOrNull() ?: 0L) / 120000000.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("중고거래 물품 등록", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Photo upload box
                Text("📷 물품 사진 첨부", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6))
                            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(12.dp))
                            .clickable { hasPhotoAttached = !hasPhotoAttached },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(if (hasPhotoAttached) "1/10" else "0/10", fontSize = 10.sp, color = Color(0xFF6B7280))
                        }
                    }

                    if (hasPhotoAttached) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_marketplace_camera_1784694609353),
                                contentDescription = "대표 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                            ) {
                                Text("대표사진", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 2.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("글 제목") },
                    placeholder = { Text("예: 소니 A7M4 카메라 커스텀 렌즈 세트") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Category selector
                Text("🏷️ 카테고리 선택", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF374151))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF4B5563)
                            )
                        )
                    }
                }

                // Price
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("판매 가격 (원)") },
                    placeholder = { Text("0") },
                    trailingIcon = { Text("원", modifier = Modifier.padding(end = 12.dp), fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                val estimatedSats = ((priceText.toLongOrNull() ?: 0L) * 100 / 120)
                if (estimatedSats > 0) {
                    Text(
                        text = "≈ ${numberFormat.format(estimatedSats)} sats (Aqua Wallet 자동환산)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706)
                    )
                }

                // Description
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("자세한 설명") },
                    placeholder = { Text("구매시기, 상태, 거래 희망 장소(역삼동 등)를 적어주세요.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceText.toLongOrNull() ?: 0L
                    if (title.isNotBlank()) onConfirm(title, selectedCategory, p, desc)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) { Text("물품 등록하기", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = Color(0xFF6B7280)) }
        }
    )
}

package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AgendaEntity
import com.example.data.local.FundingEntity
import com.example.data.local.PromiseEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundingScreen(viewModel: AeoViewModel) {
    val fundingList by viewModel.fundingList.collectAsState()
    val selectedFunding by viewModel.selectedFunding.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val showContributeDialog by viewModel.showContributeDialog.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    if (selectedFunding != null) {
        FundingDetailScreen(
            funding = selectedFunding!!,
            viewModel = viewModel,
            onBack = { viewModel.selectFunding("") }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.showCreateFundingDialog.value = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                    text = { Text("모금 개설", fontWeight = FontWeight.Bold) },
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
                // High Density Search Input - Compact Top Margin
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchQuery.value = it
                    },
                    placeholder = { Text("모금 제목, 설명 또는 @아이디 검색...", fontSize = 12.sp) },
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

                // Main Content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Section Header: Local Fundings
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "지역기반 모금 목록",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "전체 ${fundingList.size}개",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // Funding List Items
                    items(fundingList, key = { it.id }) { funding ->
                        FundingCardItem(
                            funding = funding,
                            onClick = { viewModel.selectFunding(funding.id) },
                            onDirectFund = {
                                viewModel.selectFunding(funding.id)
                                viewModel.showContributeDialog.value = true
                            }
                        )
                    }

                    // AO Developer Donation Card (Item 2)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("AO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(
                                            text = "AO 코어 개발자 후원",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A1C1E)
                                        )
                                        Text(
                                            text = "수익모델: 자유 후원 (BTC / ETH / TRX)",
                                            fontSize = 10.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.showDevDonationDialog.value = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AO 개발자 후원하기", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // Wallet Balance Snapshot Card
                    item {
                        val context = LocalContext.current
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "METAMASK CONNECTED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MonoMetaMaskGold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = currentUser?.walletAddress ?: "0x7a83F99b2C1d445E81A912",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "지갑 잔액",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )

                                    // Money Balance Anchor Text -> Opens MetaMask on click
                                    Text(
                                        text = "12.450 ETH (≈ 43,570,000 KRW) ↗",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MonoMetaMaskGold,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier.clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://metamask.app.link"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "MetaMask 지갑 앱으로 연결합니다...", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (viewModel.showCreateFundingDialog.collectAsState().value) {
        CreateFundingDialog(
            onDismiss = { viewModel.showCreateFundingDialog.value = false },
            onConfirm = { title, desc, target, handle ->
                viewModel.createFunding(title, desc, target, handle)
            }
        )
    }

    if (showContributeDialog && selectedFunding != null) {
        ContributeDialog(
            funding = selectedFunding!!,
            currentUser = currentUser,
            onDismiss = { viewModel.showContributeDialog.value = false },
            onConfirm = { amount, method ->
                viewModel.contribute(selectedFunding!!.id, amount, method)
            }
        )
    }
}

@Composable
fun FundingCardItem(
    funding: FundingEntity,
    onClick: () -> Unit,
    onDirectFund: () -> Unit = {}
) {
    val progress = (funding.currentAmount.toFloat() / funding.targetAmount.toFloat()).coerceIn(0f, 1f)
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
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
                            text = funding.creatorName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = "DID 연동됨 · ${funding.location}",
                            fontSize = 10.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "\"${funding.title}\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar Section
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% 달성",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${numberFormat.format(funding.currentAmount)} / ${numberFormat.format(funding.targetAmount)} 원",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                    )
                }

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape),
                    color = Color.Black,
                    trackColor = Color(0xFFE8E8E8)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Direct Fund Button + Open Detail Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Direct Fund Button (Item 4)
                Button(
                    onClick = onDirectFund,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MonoMetaMaskGold, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "모금하기",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Detail & Chat Button
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(
                        text = "입장 / 채팅",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundingDetailScreen(
    funding: FundingEntity,
    viewModel: AeoViewModel,
    onBack: () -> Unit
) {
    var selectedDetailTab by remember { mutableStateOf(0) } // 0: Overview, 1: Open Chat, 2: Agendas, 3: Promise & Vault
    val userVotingPower by viewModel.selectedFundingUserVotingPower.collectAsState()
    val agendas by viewModel.selectedFundingAgendas.collectAsState()
    val agreedAgendas by viewModel.selectedFundingAgreedAgendas.collectAsState()
    val promise by viewModel.selectedFundingPromise.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val progress = (funding.currentAmount.toFloat() / funding.targetAmount.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color.Black)
                        }
                        Column {
                            Text(
                                text = funding.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E),
                                maxLines = 1
                            )
                            Text(
                                text = "${funding.handle} · ${funding.creatorName}",
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Surface(
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${(progress * 100).toInt()}% 달성",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoMetaMaskGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color(0xFFF2F2F2)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ultra Compact Sub Tab Row
            TabRow(
                selectedTabIndex = selectedDetailTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.Black
            ) {
                Tab(selected = selectedDetailTab == 0, onClick = { selectedDetailTab = 0 }) {
                    Text("개요", modifier = Modifier.padding(vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedDetailTab == 1, onClick = { selectedDetailTab = 1 }) {
                    Text("오픈채팅", modifier = Modifier.padding(vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedDetailTab == 2, onClick = { selectedDetailTab = 2 }) {
                    Text("안건게시판", modifier = Modifier.padding(vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedDetailTab == 3, onClick = { selectedDetailTab = 3 }) {
                    Text("약속&시드키", modifier = Modifier.padding(vertical = 10.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Sub Tab Content
            when (selectedDetailTab) {
                0 -> OverviewTabContent(
                    funding = funding,
                    userVotingPower = userVotingPower,
                    onContributeClick = { viewModel.showContributeDialog.value = true }
                )
                1 -> OpenChatTabContent(funding, viewModel)
                2 -> AgendasTabContent(funding, agendas, agreedAgendas, userVotingPower, viewModel)
                3 -> PromiseAndVaultTabContent(funding, promise, viewModel)
            }
        }
    }

    // Dialogs
    if (viewModel.showContributeDialog.collectAsState().value) {
        ContributeDialog(
            funding = funding,
            currentUser = currentUser,
            onDismiss = { viewModel.showContributeDialog.value = false },
            onConfirm = { amt, method ->
                viewModel.contribute(funding.id, amt, method)
            }
        )
    }

    if (viewModel.showCreateAgendaDialog.collectAsState().value) {
        CreateAgendaDialog(
            onDismiss = { viewModel.showCreateAgendaDialog.value = false },
            onConfirm = { title, content ->
                viewModel.createAgenda(funding.id, title, content)
            }
        )
    }

    if (viewModel.showCreatePromiseDialog.collectAsState().value) {
        CreatePromiseDialog(
            onDismiss = { viewModel.showCreatePromiseDialog.value = false },
            onConfirm = { title, place, desc ->
                viewModel.createPromise(funding.id, title, place, desc)
            }
        )
    }

    if (viewModel.showConsensusVoteDialog.collectAsState().value) {
        ConsensusVoteDialog(
            fundingTitle = funding.title,
            promiseTitle = promise?.title ?: "현장 모임 및 자금 집행",
            onDismiss = { viewModel.showConsensusVoteDialog.value = false },
            onVote = { agree ->
                viewModel.voteConsensus(funding.id, agree)
            }
        )
    }
}

@Composable
fun OverviewTabContent(
    funding: FundingEntity,
    userVotingPower: Long,
    onContributeClick: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    val progress = (funding.currentAmount.toFloat() / funding.targetAmount.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress & Voting Power Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(20.dp),
                onClick = onContributeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("모금 진행 현황 (터치하여 참여)", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Text("${(progress * 100).toInt()}% 달성", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MonoMetaMaskGold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = MonoMetaMaskGold,
                        trackColor = Color(0xFF374151)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${numberFormat.format(funding.currentAmount)}원 / ${numberFormat.format(funding.targetAmount)}원",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${funding.backerCount}명 참여",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF374151))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("나의 참정권 (Voting Power)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${numberFormat.format(userVotingPower)} VP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MonoMetaMaskGold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("참여 / 충전하기", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 모금 목적 및 거버넌스 안내", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(funding.description, fontSize = 13.sp, color = Color(0xFF4B5563))
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔒 탈중앙 시드구문 보관 메커니즘", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "생성된 모금 지갑의 비밀복구구문은 서버에 안전하게 암호화 보관되며, 모금 목표 달성 후 약속 현장 모임에서 참정권 과반 동의 시 방장에게 자동 전달됩니다.",
                        fontSize = 12.sp,
                        color = Color(0xFF4B5563)
                    )
                }
            }
        }
    }
}

@Composable
fun OpenChatTabContent(funding: FundingEntity, viewModel: AeoViewModel) {
    val chatRoomId = "funding_${funding.id}"
    viewModel.activeChatRoomId.value = chatRoomId
    val messages by viewModel.activeChatMessages.collectAsState()

    var textInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MonoDarkCard)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = MonoMetaMaskGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("XMTP E2E 종단간 암호화 오픈채팅", fontSize = 11.sp, color = MonoTextPrimaryDark)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderDid == viewModel.repository.currentUserDid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (!isMe) {
                                Text(msg.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(msg.messageText, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("메시지 입력...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    viewModel.sendChatMessage(chatRoomId, textInput)
                    textInput = ""
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "전송")
            }
        }
    }
}

@Composable
fun AgendasTabContent(
    funding: FundingEntity,
    agendas: List<AgendaEntity>,
    agreedAgendas: List<AgendaEntity>,
    userVotingPower: Long,
    viewModel: AeoViewModel
) {
    var agendaSubTab by remember { mutableStateOf(0) } // 0: All, 1: Agreed (합의됨)
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = agendaSubTab == 0,
                    onClick = { agendaSubTab = 0 },
                    label = { Text("전체 안건 (${agendas.size})", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Black,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFE5E7EB),
                        labelColor = Color(0xFF374151)
                    )
                )
                FilterChip(
                    selected = agendaSubTab == 1,
                    onClick = { agendaSubTab = 1 },
                    label = { Text("합의됨 (${agreedAgendas.size})", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Black,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFE5E7EB),
                        labelColor = Color(0xFF374151)
                    )
                )
            }

            Button(
                onClick = { viewModel.showCreateAgendaDialog.value = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("안건 제안", fontSize = 12.sp)
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.agendaSearchQuery.value = it
            },
            placeholder = { Text("안건 검색...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        val listToShow = if (agendaSubTab == 1) agreedAgendas else agendas

        if (listToShow.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (agendaSubTab == 1) "아직 '합의됨'으로 승인된 안건이 없습니다." else "등록된 안건이 없습니다. 안건을 제안해보세요!",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(listToShow, key = { it.id }) { agenda ->
                    AgendaItemCard(
                        agenda = agenda,
                        funding = funding,
                        userVotingPower = userVotingPower,
                        onVote = { choice -> viewModel.voteAgenda(agenda.id, choice, userVotingPower) },
                        onApprove = { viewModel.approveAgendaByCreator(agenda.id) },
                        isCreator = funding.creatorDid == viewModel.repository.currentUserDid
                    )
                }
            }
        }
    }
}

@Composable
fun AgendaItemCard(
    agenda: AgendaEntity,
    funding: FundingEntity,
    userVotingPower: Long,
    onVote: (Boolean) -> Unit,
    onApprove: () -> Unit,
    isCreator: Boolean
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    val total = if (agenda.totalVotes == 0L) 1L else agenda.totalVotes
    val yesRatio = (agenda.yesVotes.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("제안자: ${agenda.creatorName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (agenda.isFinalized) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MonoSuccessBadge) {
                        Text("🤝 합의됨 (Finalized)", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                } else if (agenda.isApprovedByCreator) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MonoDarkCard) {
                        Text("방장 승인완료", fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(agenda.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(agenda.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))

            // Voting Progress
            LinearProgressIndicator(
                progress = yesRatio,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("찬성: ${numberFormat.format(agenda.yesVotes)} VP (${(yesRatio * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("반대: ${numberFormat.format(agenda.noVotes)} VP", fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!agenda.isFinalized) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onVote(true) },
                        enabled = userVotingPower > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("찬성 투표")
                    }

                    OutlinedButton(
                        onClick = { onVote(false) },
                        enabled = userVotingPower > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("반대 투표")
                    }
                }

                if (isCreator && !agenda.isApprovedByCreator) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MonoDarkCard)
                    ) {
                        Text("방장 승인 (과반 찬성 시 합의됨으로 전환)")
                    }
                }
            } else {
                Text("🔒 이 안건은 승인되어 '합의됨' 페이지로 이동하였으며 투표가 종료되었습니다.", fontSize = 11.sp, color = MonoAccentSubtle)
            }
        }
    }
}

@Composable
fun PromiseAndVaultTabContent(
    funding: FundingEntity,
    promise: PromiseEntity?,
    viewModel: AeoViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (promise == null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📌 약속 (Meeting & Release Event)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("목표 금액 달성 시 방장이 약속을 생성할 수 있습니다.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (funding.isGoalReached && funding.creatorDid == viewModel.repository.currentUserDid) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = { viewModel.showCreatePromiseDialog.value = true }) {
                            Text("약속 생성하기")
                        }
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🤝 현장 약속: ${promise.title}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("장소: ${promise.place}", fontSize = 13.sp)
                    Text("설명: ${promise.description}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("자금 집행 동의 현황 (과반 동의 필요)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("찬성: ${promise.yesConsensusVotes}명 / 반대: ${promise.noConsensusVotes}명 (총 ${promise.totalConsensusParticipants}명)", fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.showConsensusVoteDialog.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("자금 사용 동의 / 비동의 투표 참여")
                    }
                }
            }
        }

        // Secret Vault Section
        Card(
            colors = CardDefaults.cardColors(containerColor = MonoDarkCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = MonoMetaMaskGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("모금 풀 비밀복구구문 Vault", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MonoTextPrimaryDark)
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (funding.isSecretUnlocked) {
                    Text("🔓 과반 동의 통과 완료! 3.3% 운영 수수료 자동 정산 전송 및 펀딩 시드구문 개봉되었습니다:", fontSize = 12.sp, color = MonoTextPrimaryDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    val feeAmount = (funding.currentAmount * 0.033).toLong()
                    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "⚡ 서비스 운영유지보수 수수료 (3.3%) 자동 정산 내역",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA)
                            )
                            Text(
                                text = "정산 수수료: ${numberFormat.format(feeAmount)} KRW (에이오 개발자 지갑 0xAO_DEV_3300... 로 자동 전송 완료)",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(colors = CardDefaults.cardColors(containerColor = Color.Black)) {
                        Text(
                            text = funding.seedPhrase,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = MonoMetaMaskGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "🔒 비밀복구구문은 방장에게도 현재 가려져 있습니다. 목표금액 달성 후 약속 현장 모임에서 과반수가 자금 사용에 동의하면, 서비스 운영유지보수 수수료 3.3%가 에이오 개발자 메타마스크 지갑(0xAO_DEV_3300...)으로 자동 정산 전송된 직후 방장에게 시드구문이 오픈됩니다.",
                        fontSize = 12.sp,
                        color = MonoTextSecondaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePromiseDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, place: String, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 약속 생성", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("약속 제목") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = place, onValueChange = { place = it }, label = { Text("만남 장소 및 시각") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("약속 내용 및 집행 안건 설명") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, place, desc) }) { Text("생성") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}


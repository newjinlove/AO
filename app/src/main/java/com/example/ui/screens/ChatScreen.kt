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
import com.example.R
import com.example.data.local.ChatRoomEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AeoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: AeoViewModel) {
    val chatRooms by viewModel.chatRooms.collectAsState()
    val activeRoomId by viewModel.activeChatRoomId.collectAsState()
    val messages by viewModel.activeChatMessages.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val followedDids by viewModel.followedUserDids.collectAsState()

    val searchUserQuery by viewModel.userSearchQuery.collectAsState()
    val searchResults by viewModel.userSearchResults.collectAsState()

    var showSearchUserDialog by remember { mutableStateOf(false) }
    var chatSubTab by remember { mutableStateOf(1) } // 0: 친구, 1: 채팅 (채팅방 기본값)

    if (activeRoomId != null) {
        val currentRoom = chatRooms.find { it.roomId == activeRoomId }
        ChatRoomThreadScreen(
            room = currentRoom ?: ChatRoomEntity(activeRoomId!!, "채팅방", "DIRECT_NOSTR"),
            messages = messages,
            viewModel = viewModel,
            onBack = { viewModel.activeChatRoomId.value = null }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showSearchUserDialog = true },
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.PersonSearch, contentDescription = "사용자 찾기")
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = Color(0xFFF2F2F2)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Sub-Tabs Header (친구 / 채팅)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = chatSubTab == 0,
                        onClick = { chatSubTab = 0 },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("친구 목록", fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.Black,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFE5E7EB),
                            labelColor = Color(0xFF374151)
                        )
                    )

                    FilterChip(
                        selected = chatSubTab == 1,
                        onClick = { chatSubTab = 1 },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("채팅방 (${chatRooms.size})", fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color.Black,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFE5E7EB),
                            labelColor = Color(0xFF374151)
                        )
                    )
                }

                // E2E Security Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Nostr 종단간 암호화 (NIP-04/17) 메시징", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                            Text("중앙서버 없이 Nostr Relay를 통해 개별 암호화 저장됩니다", fontSize = 10.sp, color = Color(0xFF6B7280))
                        }
                    }
                }

                if (chatSubTab == 0) {
                    // Friends Tab Content
                    val friendsList = allUsers.filter { it.did != currentUser?.did }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // My Profile Section
                        if (currentUser != null) {
                            item {
                                Text("내 프로필", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280), modifier = Modifier.padding(top = 4.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.showUserProfile(currentUser!!) },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(currentUser!!.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFFEFF6FF),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text("나", fontSize = 10.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                }
                                            }
                                            Text(currentUser!!.bio, fontSize = 11.sp, color = Color(0xFF6B7280), maxLines = 1)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("친구 (${friendsList.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                            }
                        }

                        items(friendsList, key = { "friend_${it.did}" }) { friend ->
                            FriendListItem(
                                friend = friend,
                                isFollowing = followedDids.contains(friend.did),
                                onCardClick = { viewModel.showUserProfile(friend) },
                                onChatClick = { viewModel.openDirectChatWith(friend) }
                            )
                        }
                    }
                } else {
                    // Chat Rooms Tab Content
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatRooms, key = { it.roomId }) { room ->
                            ChatRoomListItem(
                                room = room,
                                onClick = { viewModel.activeChatRoomId.value = room.roomId },
                                onAvatarClick = {
                                    if (!room.partnerDid.isNullOrEmpty()) {
                                        viewModel.showUserProfileByDid(room.partnerDid)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSearchUserDialog) {
        SearchUserDialog(
            query = searchUserQuery,
            results = searchResults,
            onQueryChange = { viewModel.searchUsers(it) },
            onDismiss = { showSearchUserDialog = false },
            onSelectUser = { user ->
                viewModel.openDirectChatWith(user)
                showSearchUserDialog = false
            }
        )
    }
}

@Composable
fun FriendListItem(
    friend: UserEntity,
    isFollowing: Boolean,
    onCardClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(friend.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(friend.handle, fontSize = 11.sp, color = Color(0xFF9CA3AF))
                }
                Text(friend.bio, fontSize = 11.sp, color = Color(0xFF6B7280), maxLines = 1)
            }

            IconButton(
                onClick = onChatClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFF3F4F6))
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = "채팅하기",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatRoomListItem(
    room: ChatRoomEntity,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    .clickable { onAvatarClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    if (room.type == "FUNDING_OPEN_CHAT") {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MonoDarkCard
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Paid,
                                    contentDescription = "모금",
                                    tint = MonoMetaMaskGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text("모금", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MonoMetaMaskGold)
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "채팅",
                                    tint = Color(0xFF4B5563),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text("채팅", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4B5563))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = room.lastMessage,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomThreadScreen(
    room: ChatRoomEntity,
    messages: List<com.example.data.local.ChatMessageEntity>,
    viewModel: AeoViewModel,
    onBack: () -> Unit
) {
    BackHandler(enabled = true) {
        onBack()
    }

    var textInput by remember { mutableStateOf("") }

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
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color.Black)
                        }

                        // Partner / Room Avatar in TopBar
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                                .clickable {
                                    if (!room.partnerDid.isNullOrEmpty()) {
                                        viewModel.showUserProfileByDid(room.partnerDid)
                                    }
                                }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(room.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Icon(Icons.Default.Lock, contentDescription = null, tint = MonoMetaMaskGold, modifier = Modifier.padding(end = 12.dp).size(18.dp))
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderDid == viewModel.repository.currentUserDid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        if (!isMe) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                                    .clickable {
                                        viewModel.showUserProfileByDid(msg.senderDid)
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_default_avatar_1784694633189),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                if (!isMe) {
                                    Text(
                                        text = msg.senderName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB),
                                        modifier = Modifier.clickable {
                                            viewModel.showUserProfileByDid(msg.senderDid)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(msg.messageText, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("E2E 암호화 메시지 전송...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendChatMessage(room.roomId, textInput)
                        textInput = ""
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "전송")
                }
            }
        }
    }
}

@Composable
fun SearchUserDialog(
    query: String,
    results: List<com.example.data.local.UserEntity>,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSelectUser: (com.example.data.local.UserEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("이름 또는 핸들로 상대방 검색", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("예: @gangnam_citizen 또는 이름...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectUser(user) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(user.handle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("닫기") } }
    )
}

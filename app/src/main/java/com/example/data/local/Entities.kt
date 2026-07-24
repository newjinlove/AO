package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val did: String, // e.g. "did:aeo:0x7a83f99b2c1d"
    val handle: String,          // e.g. "@aeo_leader"
    val name: String,            // e.g. "최민준"
    val bio: String,             // e.g. "역삼동 지역공동체 기획자 | DID & Decentralized Governance"
    val avatarUri: String,       // Drawable name or URI
    val walletAddress: String,   // e.g. "0x7a83F99b2C1d445E81A912"
    val isConnectedMetaMask: Boolean = true,
    val location: String = "서울 강남구 역삼1동"
)

@Entity(tableName = "fundings")
data class FundingEntity(
    @PrimaryKey val id: String,         // e.g. "fund_1"
    val title: String,                  // e.g. "역삼1동 공유도서관 및 시민토론공간 설립 모금"
    val description: String,            // e.g. "주민들이 함께 읽고 토론할 수 있는 지역 공유 공간 마련을 위한 공동 모금입니다."
    val creatorDid: String,             // User DID
    val creatorName: String,
    val creatorAvatar: String,
    val targetAmount: Long,             // e.g. 5,000,000 KRW
    val currentAmount: Long,            // e.g. 5,200,000 KRW
    val backerCount: Int,               // e.g. 48
    val handle: String,                 // e.g. "@fund_yeoksam_library"
    val seedPhrase: String,             // Vault recovery phrase hidden until unlock condition met
    val isSecretUnlocked: Boolean = false,
    val isGoalReached: Boolean = false,
    val status: String = "ACTIVE",      // ACTIVE, PROMISE_STAGE, COMPLETED
    val location: String = "서울 강남구 역삼동",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "funding_contributions")
data class FundingContributionEntity(
    @PrimaryKey val id: String,
    val fundingId: String,
    val userDid: String,
    val userName: String,
    val amount: Long,                   // KRW or Token equivalent value
    val votingPower: Long,              // Proportional参政権 (参政権 = amount)
    val paymentMethod: String,          // METAMASK_ETH, CARD, EASY_PAY
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "agendas")
data class AgendaEntity(
    @PrimaryKey val id: String,
    val fundingId: String,
    val title: String,                  // e.g. "도서관 위치를 역삼역 3번출구 근처 상가로 결정하는 안건"
    val content: String,                // Detailed explanation
    val creatorDid: String,
    val creatorName: String,
    val yesVotes: Long = 0,             // Sum of voting power
    val noVotes: Long = 0,              // Sum of voting power
    val totalVotes: Long = 0,
    val isApprovedByCreator: Boolean = false, // 방장 승인
    val isFinalized: Boolean = false,         // '합의됨' status
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "agenda_votes")
data class AgendaVoteEntity(
    @PrimaryKey val id: String,
    val agendaId: String,
    val userDid: String,
    val voteChoice: Boolean,           // true = YES, false = NO
    val votingPowerUsed: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "promises")
data class PromiseEntity(
    @PrimaryKey val id: String,
    val fundingId: String,
    val title: String,                  // e.g. "역삼 공유도서관 장소 현장모임 및 펀딩 집행 승인 미팅"
    val place: String,                  // e.g. "역삼동 주민센터 3층 회의실"
    val eventTime: Long,                // Timestamp when the meeting takes place
    val description: String,
    val isConsensusTriggered: Boolean = false,
    val yesConsensusVotes: Int = 0,
    val noConsensusVotes: Int = 0,
    val totalConsensusParticipants: Int = 0,
    val isUnlocked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketplace_items")
data class MarketplaceItemEntity(
    @PrimaryKey val id: String,
    val title: String,                  // e.g. "클래식 필름 카메라 & 렌즈 키트"
    val category: String,               // 디지털/가전, 의류, 도서, 기타
    val price: Long,                    // KRW
    val priceInEth: Double,             // Crypto price e.g. 0.08 ETH
    val sellerDid: String,
    val sellerName: String,
    val sellerAvatar: String,
    val location: String = "서울 강남구 역삼1동",
    val description: String,
    val imageRes: String,               // Image resource name
    val status: String = "SELLING",     // SELLING, RESERVED, SOLD
    val likeCount: Int = 0,
    val chatCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feed_posts")
data class FeedPostEntity(
    @PrimaryKey val id: String,
    val authorDid: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String,
    val content: String,
    val imageRes: String,               // Image resource name or blank
    val location: String = "서울 강남구 역삼동",
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "follows")
data class FollowEntity(
    @PrimaryKey val id: String,          // followerDid_followingDid
    val followerDid: String,
    val followingDid: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val chatRoomId: String,             // DM e.g. "dm_did1_did2" or "funding_1"
    val senderDid: String,
    val senderName: String,
    val senderAvatar: String,
    val messageText: String,
    val isE2EEncrypted: Boolean = true, // XMTP protocol E2E
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_rooms")
data class ChatRoomEntity(
    @PrimaryKey val roomId: String,
    val title: String,                  // Contact name or Funding title
    val type: String,                   // "DIRECT_XMTP", "FUNDING_OPEN_CHAT"
    val partnerDid: String? = null,
    val fundingId: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class AeoRepository(private val db: AppDatabase) {
    val userDao = db.userDao()
    val fundingDao = db.fundingDao()
    val contributionDao = db.fundingContributionDao()
    val agendaDao = db.agendaDao()
    val agendaVoteDao = db.agendaVoteDao()
    val promiseDao = db.promiseDao()
    val marketplaceDao = db.marketplaceDao()
    val feedPostDao = db.feedPostDao()
    val followDao = db.followDao()
    val chatDao = db.chatDao()

    // Default Current Logged In User DID
    val currentUserDid = "did:ao:0x7a83f99b2c1d"

    fun getCurrentUserFlow(): Flow<UserEntity?> = userDao.getUserFlow(currentUserDid)

    suspend fun seedInitialDataIfEmpty() {
        val existingUser = userDao.getUser(currentUserDid)
        if (existingUser == null) {
            // Seed Default User
            val defaultUser = UserEntity(
                did = currentUserDid,
                handle = "@ao_leader",
                name = "최민준",
                bio = "역삼동 지역공동체 기획자 | AO Decentralized Governance",
                avatarUri = "img_default_avatar_1784694633189",
                walletAddress = "0x7a83F99b2C1d445E81A912",
                isConnectedMetaMask = true,
                location = "서울 강남구 역삼1동"
            )
            userDao.insertUser(defaultUser)

            // Seed Second User for Nostr DM testing
            userDao.insertUser(
                UserEntity(
                    did = "did:ao:0x91c4d8e20f3b",
                    handle = "@gangnam_citizen",
                    name = "김수현",
                    bio = "역삼동 주민 | 환경 및 지역 중고거래 관심",
                    avatarUri = "img_default_avatar_1784694633189",
                    walletAddress = "0x91c4D8E20f3B774A12B8",
                    isConnectedMetaMask = true,
                    location = "서울 강남구 역삼2동"
                )
            )

            userDao.insertUser(
                UserEntity(
                    did = "did:ao:0x3f12a9c48e71",
                    handle = "@jimin_design",
                    name = "박지민",
                    bio = "강남 커뮤니티 디자이너 | 제로웨이스트 나눔",
                    avatarUri = "img_default_avatar_1784694633189",
                    walletAddress = "0x3f12A9C48E7101A299",
                    isConnectedMetaMask = true,
                    location = "서울 강남구 역삼1동"
                )
            )

            userDao.insertUser(
                UserEntity(
                    did = "did:ao:0x88e7b1a901c2",
                    handle = "@dohyun_tech",
                    name = "이도현",
                    bio = "Web3 개발자 | 탈중앙 모금 스마트계약 검증인",
                    avatarUri = "img_default_avatar_1784694633189",
                    walletAddress = "0x88E7B1A901C209F441",
                    isConnectedMetaMask = true,
                    location = "서울 강남구 논현동"
                )
            )

            // Seed Initial Follows (Friends)
            followDao.insertFollow(
                FollowEntity(
                    id = "${currentUserDid}_did:ao:0x91c4d8e20f3b",
                    followerDid = currentUserDid,
                    followingDid = "did:ao:0x91c4d8e20f3b"
                )
            )
            followDao.insertFollow(
                FollowEntity(
                    id = "${currentUserDid}_did:ao:0x3f12a9c48e71",
                    followerDid = currentUserDid,
                    followingDid = "did:ao:0x3f12a9c48e71"
                )
            )

            // Seed Initial Fundings
            val seedPhrase1 = "ridge velvet crystal horizon oak subtle venture flame myth shadow pulse zenith"
            val funding1 = FundingEntity(
                id = "fund_1",
                title = "역삼1동 주민 공유도서관 및 시민토론공간 조성 펀딩",
                description = "주민들이 자율적으로 관리하는 탈중앙형 시민 공간을 마련합니다. 모금에 참여하시면 해당 오픈채팅방과 안건 참정권이 부여됩니다.",
                creatorDid = currentUserDid,
                creatorName = "최민준",
                creatorAvatar = "img_default_avatar_1784694633189",
                targetAmount = 5000000L,
                currentAmount = 4200000L,
                backerCount = 38,
                handle = "@fund_yeoksam_library",
                seedPhrase = seedPhrase1,
                isSecretUnlocked = false,
                isGoalReached = false,
                status = "ACTIVE",
                location = "서울 강남구 역삼1동",
                createdAt = System.currentTimeMillis() - 86400000L * 5
            )

            val seedPhrase2 = "forest whisper golden harbor beacon stable unity anchor digital shield craft lunar"
            val funding2 = FundingEntity(
                id = "fund_2",
                title = "강남역 보행자 친화 그린로드 및 공공 벤치 설치 project",
                description = "강남역 번화가 골목의 보행 환경을 개선하고 시민 휴식 공간을 조성하기 위한 기금 모금입니다.",
                creatorDid = "did:ao:0x91c4d8e20f3b",
                creatorName = "김수현",
                creatorAvatar = "img_default_avatar_1784694633189",
                targetAmount = 3000000L,
                currentAmount = 3000000L, // Reached target!
                backerCount = 52,
                handle = "@fund_green_road",
                seedPhrase = seedPhrase2,
                isSecretUnlocked = false,
                isGoalReached = true,
                status = "PROMISE_STAGE",
                location = "서울 강남구 역삼동",
                createdAt = System.currentTimeMillis() - 86400000L * 10
            )

            fundingDao.insertFunding(funding1)
            fundingDao.insertFunding(funding2)

            // Contributions
            contributionDao.insertContribution(
                FundingContributionEntity(
                    id = "contrib_1",
                    fundingId = "fund_1",
                    userDid = currentUserDid,
                    userName = "최민준",
                    amount = 500000L,
                    votingPower = 500000L,
                    paymentMethod = "METAMASK_ETH",
                    timestamp = System.currentTimeMillis() - 86400000L * 4
                )
            )

            contributionDao.insertContribution(
                FundingContributionEntity(
                    id = "contrib_2",
                    fundingId = "fund_2",
                    userDid = currentUserDid,
                    userName = "최민준",
                    amount = 300000L,
                    votingPower = 300000L,
                    paymentMethod = "CARD",
                    timestamp = System.currentTimeMillis() - 86400000L * 8
                )
            )

            // Initial Agendas for Funding 1
            agendaDao.insertAgenda(
                AgendaEntity(
                    id = "agenda_1",
                    fundingId = "fund_1",
                    title = "공유도서관 입지 선정: 역삼역 3번 출구 도보 3분 상가 2층",
                    content = "지하철 접근성이 양호하고 임대료가 합리적인 역삼역 3번출구 50m 지점을 도서관 입지로 최종 승인하는 안건입니다.",
                    creatorDid = currentUserDid,
                    creatorName = "최민준",
                    yesVotes = 2400000L,
                    noVotes = 300000L,
                    totalVotes = 2700000L,
                    isApprovedByCreator = true,
                    isFinalized = true // Finalized / 合意됨!
                )
            )

            agendaDao.insertAgenda(
                AgendaEntity(
                    id = "agenda_2",
                    fundingId = "fund_1",
                    title = "도서관 인테리어 자재 친환경 목재 우선 구매 안건",
                    content = "지역 목공소와 협력하여 친환경 삼나무 책상과 선반을 제작·구매하는 안에 대한 투표입니다.",
                    creatorDid = "did:aeo:0x91c4d8e20f3b",
                    creatorName = "김수현",
                    yesVotes = 1800000L,
                    noVotes = 400000L,
                    totalVotes = 2200000L,
                    isApprovedByCreator = false,
                    isFinalized = false
                )
            )

            // Initial Promise for Funding 2 (Goal reached funding)
            promiseDao.insertPromise(
                PromiseEntity(
                    id = "promise_1",
                    fundingId = "fund_2",
                    title = "강남 그린로드 현장 시공계획 발표 및 펀딩 시드문구 개봉 동의 모임",
                    place = "서울 강남구 역삼동 735-1 (강남역 4번출구 앞)",
                    eventTime = System.currentTimeMillis() - 3600000L, // Time reached!
                    description = "목표금액 3,000,000원 달성에 따른 자금 집행 동의 현장 모임입니다. 참정권자 과반 동의 시 펀딩 시드문구가 방장에게 전송됩니다.",
                    isConsensusTriggered = true,
                    yesConsensusVotes = 28,
                    noConsensusVotes = 2,
                    totalConsensusParticipants = 30,
                    isUnlocked = false
                )
            )

            // Seed Marketplace Items (거래)
            marketplaceDao.insertItem(
                MarketplaceItemEntity(
                    id = "item_1",
                    title = "클래식 메카닉 수동 필름 카메라 (상태 최상)",
                    category = "디지털/가전",
                    price = 180000L,
                    priceInEth = 0.05,
                    sellerDid = "did:aeo:0x91c4d8e20f3b",
                    sellerName = "김수현",
                    sellerAvatar = "img_default_avatar_1784694633189",
                    location = "서울 강남구 역삼2동",
                    description = "보관 상태 매우 좋은 수동 필름 카메라입니다. ETH 직거래 또는 카드 결제 가능합니다.",
                    imageRes = "img_marketplace_camera_1784694609353",
                    status = "SELLING",
                    likeCount = 12,
                    chatCount = 3
                )
            )

            marketplaceDao.insertItem(
                MarketplaceItemEntity(
                    id = "item_2",
                    title = "원목 3단 미니 책장 (역삼동 직거래)",
                    category = "가구/인테리어",
                    price = 45000L,
                    priceInEth = 0.012,
                    sellerDid = currentUserDid,
                    sellerName = "최민준",
                    sellerAvatar = "img_default_avatar_1784694633189",
                    location = "서울 강남구 역삼1동",
                    description = "이사 관계로 깨끗한 원목 책장 급매합니다. 직접 가지러 오시면 기름값 빼드립니다.",
                    imageRes = "img_feed_community_1784694622250",
                    status = "SELLING",
                    likeCount = 5,
                    chatCount = 1
                )
            )

            // Seed Feed Posts (피드)
            feedPostDao.insertPost(
                FeedPostEntity(
                    id = "post_1",
                    authorDid = currentUserDid,
                    authorName = "최민준",
                    authorHandle = "@aeo_leader",
                    authorAvatar = "img_default_avatar_1784694633189",
                    content = "역삼1동 공유도서관 모금이 목표액의 84%에 도달했습니다! 참정권을 가진 모금 참여자분들은 안건 게시판에서 입지 선정 투표에 꼭 참여해주세요 🗳️✨ #에이오 #지역모금 #탈중앙거버넌스",
                    imageRes = "img_funding_hero_1784694599319",
                    location = "서울 강남구 역삼1동",
                    likeCount = 24,
                    commentCount = 5,
                    isLikedByMe = true
                )
            )

            feedPostDao.insertPost(
                FeedPostEntity(
                    id = "post_2",
                    authorDid = "did:aeo:0x91c4d8e20f3b",
                    authorName = "김수현",
                    authorHandle = "@gangnam_citizen",
                    authorAvatar = "img_default_avatar_1784694633189",
                    content = "오늘 그린로드 공공벤치 약속 모임에서 많은 분들과 직접 인사눌 수 있어 뜻깊었습니다. 투표를 통해 시드 문구 해제가 승인되었습니다! 🎉",
                    imageRes = "img_feed_community_1784694622250",
                    location = "서울 강남구 역삼동",
                    likeCount = 31,
                    commentCount = 8,
                    isLikedByMe = false
                )
            )

            // Seed Chat Rooms & Messages
            val openChatRoom = ChatRoomEntity(
                roomId = "funding_1",
                title = "[모금 오픈채팅] 역삼1동 공유도서관",
                type = "FUNDING_OPEN_CHAT",
                fundingId = "fund_1",
                lastMessage = "최민준: 입지 선정 안건이 통과되었습니다!",
                lastMessageTime = System.currentTimeMillis() - 1800000L,
                unreadCount = 2
            )

            val dmRoom = ChatRoomEntity(
                roomId = "dm_did:aeo:0x7a83f99b2c1d_did:aeo:0x91c4d8e20f3b",
                title = "김수현 (@gangnam_citizen)",
                type = "DIRECT_NOSTR",
                partnerDid = "did:aeo:0x91c4d8e20f3b",
                lastMessage = "카메라 ETH 거래 문의드립니다.",
                lastMessageTime = System.currentTimeMillis() - 3600000L,
                unreadCount = 0
            )

            chatDao.insertChatRoom(openChatRoom)
            chatDao.insertChatRoom(dmRoom)

            chatDao.insertMessage(
                ChatMessageEntity(
                    id = "msg_1",
                    chatRoomId = "funding_1",
                    senderDid = "did:aeo:0x91c4d8e20f3b",
                    senderName = "김수현",
                    senderAvatar = "img_default_avatar_1784694633189",
                    messageText = "안녕하세요! 도서관 모금에 10만 원 기여했습니다. 안건 게시판 잘 살펴보겠습니다.",
                    isE2EEncrypted = true,
                    timestamp = System.currentTimeMillis() - 7200000L
                )
            )

            chatDao.insertMessage(
                ChatMessageEntity(
                    id = "msg_2",
                    chatRoomId = "funding_1",
                    senderDid = currentUserDid,
                    senderName = "최민준",
                    senderAvatar = "img_default_avatar_1784694633189",
                    messageText = "감사합니다 김수현님! 참정권이 부여되었으니 역삼역 입지 안건 투표 참여 부탁드립니다.",
                    isE2EEncrypted = true,
                    timestamp = System.currentTimeMillis() - 1800000L
                )
            )

            chatDao.insertMessage(
                ChatMessageEntity(
                    id = "msg_3",
                    chatRoomId = dmRoom.roomId,
                    senderDid = "did:aeo:0x91c4d8e20f3b",
                    senderName = "김수현",
                    senderAvatar = "img_default_avatar_1784694633189",
                    messageText = "안녕하세요, 등록하신 필름 카메라 ETH 거래 문의드립니다.",
                    isE2EEncrypted = true,
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )
        }
    }

    // --- Actions ---

    suspend fun contributeToFunding(
        fundingId: String,
        amount: Long,
        paymentMethod: String
    ) {
        val user = userDao.getUser(currentUserDid) ?: return
        val funding = fundingDao.getFundingById(fundingId) ?: return

        val newCurrentAmount = funding.currentAmount + amount
        val isGoalReached = newCurrentAmount >= funding.targetAmount
        val newStatus = if (isGoalReached && funding.status == "ACTIVE") "PROMISE_STAGE" else funding.status

        val updatedFunding = funding.copy(
            currentAmount = newCurrentAmount,
            backerCount = funding.backerCount + 1,
            isGoalReached = isGoalReached,
            status = newStatus
        )
        fundingDao.updateFunding(updatedFunding)

        val contrib = FundingContributionEntity(
            id = "contrib_" + UUID.randomUUID().toString().take(8),
            fundingId = fundingId,
            userDid = currentUserDid,
            userName = user.name,
            amount = amount,
            votingPower = amount, // 1 KRW = 1 Voting power
            paymentMethod = paymentMethod
        )
        contributionDao.insertContribution(contrib)

        // Ensure user is in funding open chat
        val chatRoomId = "funding_$fundingId"
        var chatRoom = chatDao.getChatRoom(chatRoomId)
        if (chatRoom == null) {
            chatRoom = ChatRoomEntity(
                roomId = chatRoomId,
                title = "[모금 오픈채팅] ${funding.title}",
                type = "FUNDING_OPEN_CHAT",
                fundingId = fundingId,
                lastMessage = "${user.name}님이 모금에 기여하여 참정권을 획득하셨습니다.",
                lastMessageTime = System.currentTimeMillis()
            )
            chatDao.insertChatRoom(chatRoom)
        }

        chatDao.insertMessage(
            ChatMessageEntity(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                chatRoomId = chatRoomId,
                senderDid = currentUserDid,
                senderName = user.name,
                senderAvatar = user.avatarUri,
                messageText = "${user.name}님이 ${amount}원 모금 기여 완료 (${paymentMethod}) 🎉",
                isE2EEncrypted = true
            )
        )
    }

    suspend fun createAgenda(fundingId: String, title: String, content: String) {
        val user = userDao.getUser(currentUserDid) ?: return
        val agenda = AgendaEntity(
            id = "agenda_" + UUID.randomUUID().toString().take(8),
            fundingId = fundingId,
            title = title,
            content = content,
            creatorDid = currentUserDid,
            creatorName = user.name,
            yesVotes = 0L,
            noVotes = 0L,
            totalVotes = 0L,
            isApprovedByCreator = false,
            isFinalized = false
        )
        agendaDao.insertAgenda(agenda)
    }

    suspend fun voteOnAgenda(agendaId: String, voteChoice: Boolean, votingPower: Long) {
        val agenda = agendaDao.getAgendaById(agendaId) ?: return
        if (agenda.isFinalized) return // Finalized agendas cannot be voted on anymore!

        val existingVote = agendaVoteDao.getUserVote(agendaId, currentUserDid)
        if (existingVote != null) return // Already voted

        var yes = agenda.yesVotes
        var no = agenda.noVotes
        if (voteChoice) {
            yes += votingPower
        } else {
            no += votingPower
        }
        val total = yes + no

        val updated = agenda.copy(
            yesVotes = yes,
            noVotes = no,
            totalVotes = total
        )
        agendaDao.updateAgenda(updated)

        agendaVoteDao.insertAgendaVote(
            AgendaVoteEntity(
                id = "vote_" + UUID.randomUUID().toString().take(8),
                agendaId = agendaId,
                userDid = currentUserDid,
                voteChoice = voteChoice,
                votingPowerUsed = votingPower
            )
        )
    }

    suspend fun approveAgendaByCreator(agendaId: String) {
        val agenda = agendaDao.getAgendaById(agendaId) ?: return
        val funding = fundingDao.getFundingById(agenda.fundingId) ?: return

        // Check if caller is room creator (방장)
        if (funding.creatorDid != currentUserDid) return

        val isFinalized = agenda.yesVotes > agenda.noVotes // 과반 이상 찬성시 최종 '합의됨'
        val updated = agenda.copy(
            isApprovedByCreator = true,
            isFinalized = isFinalized
        )
        agendaDao.updateAgenda(updated)
    }

    suspend fun createPromise(fundingId: String, title: String, place: String, description: String) {
        val promise = PromiseEntity(
            id = "promise_" + UUID.randomUUID().toString().take(8),
            fundingId = fundingId,
            title = title,
            place = place,
            eventTime = System.currentTimeMillis() + 60000L, // 1 min in future or now
            description = description
        )
        promiseDao.insertPromise(promise)
    }

    suspend fun voteConsensusOnPromise(fundingId: String, agree: Boolean) {
        val promise = promiseDao.getPromiseForFundingSync(fundingId) ?: return
        var yes = promise.yesConsensusVotes
        var no = promise.noConsensusVotes
        val total = promise.totalConsensusParticipants + 1

        if (agree) yes += 1 else no += 1

        val isMajorityAgree = yes > (total / 2)
        var isUnlocked = promise.isUnlocked

        if (isMajorityAgree) {
            isUnlocked = true
            // Unlock funding seed phrase vault to creator!
            val funding = fundingDao.getFundingById(fundingId)
            if (funding != null) {
                fundingDao.updateFunding(
                    funding.copy(
                        isSecretUnlocked = true,
                        status = "COMPLETED"
                    )
                )
            }
        }

        promiseDao.updatePromise(
            promise.copy(
                yesConsensusVotes = yes,
                noConsensusVotes = no,
                totalConsensusParticipants = total,
                isUnlocked = isUnlocked
            )
        )
    }

    suspend fun createNewFunding(
        title: String,
        description: String,
        targetAmount: Long,
        handle: String
    ) {
        val user = userDao.getUser(currentUserDid) ?: return
        val generatedSeed = (1..12).map {
            listOf("alpha", "bravo", "cyber", "delta", "echo", "flame", "grant", "horizon", "index", "pulse", "shield", "zenith").random()
        }.joinToString(" ")

        val funding = FundingEntity(
            id = "fund_" + UUID.randomUUID().toString().take(8),
            title = title,
            description = description,
            creatorDid = currentUserDid,
            creatorName = user.name,
            creatorAvatar = user.avatarUri,
            targetAmount = targetAmount,
            currentAmount = 0L,
            backerCount = 1,
            handle = if (handle.startsWith("@")) handle else "@$handle",
            seedPhrase = generatedSeed,
            isSecretUnlocked = false,
            isGoalReached = false,
            status = "ACTIVE",
            location = user.location
        )
        fundingDao.insertFunding(funding)

        // Creator automatically contributes initial 10,000 KRW to get initial voting right
        contributeToFunding(funding.id, 10000L, "METAMASK_ETH")
    }

    suspend fun postMarketplaceItem(
        title: String,
        category: String,
        price: Long,
        description: String
    ) {
        val user = userDao.getUser(currentUserDid) ?: return
        val priceInEth = price.toDouble() / 3600000.0 // approx ETH rate
        val item = MarketplaceItemEntity(
            id = "item_" + UUID.randomUUID().toString().take(8),
            title = title,
            category = category,
            price = price,
            priceInEth = priceInEth,
            sellerDid = currentUserDid,
            sellerName = user.name,
            sellerAvatar = user.avatarUri,
            location = user.location,
            description = description,
            imageRes = "img_marketplace_camera_1784694609353"
        )
        marketplaceDao.insertItem(item)
    }

    suspend fun createFeedPost(content: String) {
        val user = userDao.getUser(currentUserDid) ?: return
        val post = FeedPostEntity(
            id = "post_" + UUID.randomUUID().toString().take(8),
            authorDid = currentUserDid,
            authorName = user.name,
            authorHandle = user.handle,
            authorAvatar = user.avatarUri,
            content = content,
            imageRes = "img_feed_community_1784694622250",
            location = user.location
        )
        feedPostDao.insertPost(post)
    }

    suspend fun sendChatMessage(chatRoomId: String, text: String) {
        val user = userDao.getUser(currentUserDid) ?: return
        val msg = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            chatRoomId = chatRoomId,
            senderDid = currentUserDid,
            senderName = user.name,
            senderAvatar = user.avatarUri,
            messageText = text,
            isE2EEncrypted = true
        )
        chatDao.insertMessage(msg)

        val room = chatDao.getChatRoom(chatRoomId)
        if (room != null) {
            chatDao.updateChatRoom(
                room.copy(
                    lastMessage = "${user.name}: $text",
                    lastMessageTime = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun startDirectChatWithUser(targetUser: UserEntity): String {
        val roomId = "dm_${currentUserDid}_${targetUser.did}"
        var room = chatDao.getChatRoom(roomId)
        if (room == null) {
            room = ChatRoomEntity(
                roomId = roomId,
                title = "${targetUser.name} (${targetUser.handle})",
                type = "DIRECT_NOSTR",
                partnerDid = targetUser.did,
                lastMessage = "Nostr 암호화 P2P 채팅이 시작되었습니다.",
                lastMessageTime = System.currentTimeMillis()
            )
            chatDao.insertChatRoom(room)
        }
        return roomId
    }
}

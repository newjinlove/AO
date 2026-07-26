package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.AeoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AeoTab(val title: String) {
    FUNDING("모금"),
    MARKETPLACE("거래"),
    FEED("피드"),
    CHAT("채팅"),
    PROFILE("내 프로필")
}

class AeoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = AeoRepository(db)

    val currentUser: StateFlow<UserEntity?> = repository.getCurrentUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedTab = MutableStateFlow(AeoTab.FUNDING) // App launch default screen is FUNDING!

    val searchQuery = MutableStateFlow("")

    // Funding State
    val fundingList: StateFlow<List<FundingEntity>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.fundingDao.getAllFundings()
            else repository.fundingDao.searchFundings(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFundingId = MutableStateFlow<String?>(null)

    val selectedFunding: StateFlow<FundingEntity?> = selectedFundingId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.fundingDao.getFundingByIdFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val agendaSearchQuery = MutableStateFlow("")

    val selectedFundingAgendas: StateFlow<List<AgendaEntity>> = combine(
        selectedFundingId,
        agendaSearchQuery
    ) { id, query -> Pair(id, query) }
        .flatMapLatest { (id, query) ->
            if (id == null) flowOf(emptyList())
            else if (query.isBlank()) repository.agendaDao.getAgendasForFunding(id)
            else repository.agendaDao.searchAgendas(id, query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFundingAgreedAgendas: StateFlow<List<AgendaEntity>> = selectedFundingId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.agendaDao.getAgreedAgendasForFunding(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFundingPromise: StateFlow<PromiseEntity?> = selectedFundingId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.promiseDao.getPromiseForFunding(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedFundingUserVotingPower: StateFlow<Long> = selectedFundingId
        .flatMapLatest { id ->
            if (id == null) flowOf(0L)
            else repository.contributionDao.getUserVotingPowerForFunding(id, repository.currentUserDid)
                .map { it ?: 0L }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Marketplace State
    val marketplaceItems: StateFlow<List<MarketplaceItemEntity>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.marketplaceDao.getAllItems()
            else repository.marketplaceDao.searchItems(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedMarketplaceItem = MutableStateFlow<MarketplaceItemEntity?>(null)

    // Feed State
    val feedPosts: StateFlow<List<FeedPostEntity>> = repository.feedPostDao.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFeedPost = MutableStateFlow<FeedPostEntity?>(null)

    // Chat State
    val chatRooms: StateFlow<List<ChatRoomEntity>> = repository.chatDao.getAllChatRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChatRoomId = MutableStateFlow<String?>(null)

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = activeChatRoomId
        .flatMapLatest { roomId ->
            if (roomId == null) flowOf(emptyList())
            else repository.chatDao.getMessagesForRoom(roomId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Search for Nostr DM
    val userSearchQuery = MutableStateFlow("")
    val userSearchResults = MutableStateFlow<List<UserEntity>>(emptyList())

    // All Users & Friends (Followed Users) Flow
    val allUsers: StateFlow<List<UserEntity>> = repository.userDao.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followedUserDids: StateFlow<Set<String>> = repository.followDao.getFollowsForUser(repository.currentUserDid)
        .map { follows -> follows.map { it.followingDid }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Selected User Profile State for Profile Modal
    val selectedUserProfile = MutableStateFlow<UserEntity?>(null)

    val profileFundings: StateFlow<List<FundingEntity>> = combine(fundingList, selectedUserProfile) { list, profile ->
        if (profile == null) emptyList()
        else list.filter { it.creatorDid == profile.did }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profileMarketplaceItems: StateFlow<List<MarketplaceItemEntity>> = combine(marketplaceItems, selectedUserProfile) { list, profile ->
        if (profile == null) emptyList()
        else list.filter { it.sellerDid == profile.did }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profileFeedPosts: StateFlow<List<FeedPostEntity>> = combine(feedPosts, selectedUserProfile) { list, profile ->
        if (profile == null) emptyList()
        else list.filter { it.authorDid == profile.did }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myFundings: StateFlow<List<FundingEntity>> = combine(fundingList, currentUser) { list, user ->
        if (user == null) emptyList()
        else list.filter { it.creatorDid == user.did }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myMarketplaceItems: StateFlow<List<MarketplaceItemEntity>> = combine(marketplaceItems, currentUser) { list, user ->
        if (user == null) emptyList()
        else list.filter { it.sellerDid == user.did }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myFeedPosts: StateFlow<List<FeedPostEntity>> = combine(feedPosts, currentUser) { list, user ->
        if (user == null) emptyList()
        else list.filter { it.authorDid == user.did }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun showUserProfileByDid(did: String) {
        viewModelScope.launch {
            val user = repository.userDao.getUser(did)
            if (user != null) {
                selectedUserProfile.value = user
            } else {
                selectedUserProfile.value = UserEntity(
                    did = did,
                    handle = "@community_member",
                    name = "커뮤니티 회원",
                    bio = "AO 탈중앙 네트워크 검증 참여자",
                    avatarUri = "img_default_avatar_1784694633189",
                    walletAddress = "0x..."
                )
            }
        }
    }

    fun showUserProfile(user: UserEntity) {
        selectedUserProfile.value = user
    }

    // UI Dialog & Sheet Visibility
    val showContributeDialog = MutableStateFlow(false)
    val showCreateFundingDialog = MutableStateFlow(false)
    val showCreateAgendaDialog = MutableStateFlow(false)
    val showCreatePromiseDialog = MutableStateFlow(false)
    val showCreateMarketplaceItemDialog = MutableStateFlow(false)
    val showCreatePostDialog = MutableStateFlow(false)
    val showConsensusVoteDialog = MutableStateFlow(false)
    val showWalletModal = MutableStateFlow(false)
    val showSeedVaultModal = MutableStateFlow(false)
    val showBuyCryptoDialog = MutableStateFlow(false)
    val showDevDonationDialog = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectTab(tab: AeoTab) {
        selectedTab.value = tab
    }

    fun selectFunding(fundingId: String) {
        selectedFundingId.value = fundingId
    }

    fun searchUsers(query: String) {
        userSearchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                userSearchResults.value = emptyList()
            } else {
                userSearchResults.value = repository.userDao.searchUsers(query)
            }
        }
    }

    fun contribute(fundingId: String, amount: Long, method: String) {
        viewModelScope.launch {
            repository.contributeToFunding(fundingId, amount, method)
            showContributeDialog.value = false
        }
    }

    fun createAgenda(fundingId: String, title: String, content: String) {
        viewModelScope.launch {
            repository.createAgenda(fundingId, title, content)
            showCreateAgendaDialog.value = false
        }
    }

    fun voteAgenda(agendaId: String, voteChoice: Boolean, power: Long) {
        viewModelScope.launch {
            repository.voteOnAgenda(agendaId, voteChoice, power)
        }
    }

    fun approveAgendaByCreator(agendaId: String) {
        viewModelScope.launch {
            repository.approveAgendaByCreator(agendaId)
        }
    }

    fun createPromise(fundingId: String, title: String, place: String, desc: String) {
        viewModelScope.launch {
            repository.createPromise(fundingId, title, place, desc)
            showCreatePromiseDialog.value = false
        }
    }

    fun voteConsensus(fundingId: String, agree: Boolean) {
        viewModelScope.launch {
            repository.voteConsensusOnPromise(fundingId, agree)
            showConsensusVoteDialog.value = false
        }
    }

    fun createFunding(title: String, desc: String, target: Long, handle: String) {
        viewModelScope.launch {
            repository.createNewFunding(title, desc, target, handle)
            showCreateFundingDialog.value = false
        }
    }

    fun postItem(title: String, category: String, price: Long, desc: String) {
        viewModelScope.launch {
            repository.postMarketplaceItem(title, category, price, desc)
            showCreateMarketplaceItemDialog.value = false
        }
    }

    fun createPost(content: String) {
        viewModelScope.launch {
            repository.createFeedPost(content)
            showCreatePostDialog.value = false
        }
    }

    fun sendChatMessage(roomId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(roomId, text)
        }
    }

    fun openDirectChatWith(targetUser: UserEntity) {
        viewModelScope.launch {
            val roomId = repository.startDirectChatWithUser(targetUser)
            activeChatRoomId.value = roomId
            selectedTab.value = AeoTab.CHAT
        }
    }

    fun toggleFollowUser(targetDid: String) {
        viewModelScope.launch {
            val existing = repository.followDao.getFollow(repository.currentUserDid, targetDid)
            if (existing != null) {
                repository.followDao.deleteFollow(repository.currentUserDid, targetDid)
            } else {
                repository.followDao.insertFollow(
                    FollowEntity(
                        id = "${repository.currentUserDid}_$targetDid",
                        followerDid = repository.currentUserDid,
                        followingDid = targetDid
                    )
                )
            }
        }
    }

    fun buyCrypto(amountKrw: Long) {
        viewModelScope.launch {
            showBuyCryptoDialog.value = false
        }
    }

    fun donateToDev(network: String = "이더리움 네트워크", amount: Double) {
        viewModelScope.launch {
            showDevDonationDialog.value = false
        }
    }
}

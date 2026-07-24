package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE did = :did LIMIT 1")
    fun getUserFlow(did: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE did = :did LIMIT 1")
    suspend fun getUser(did: String): UserEntity?

    @Query("SELECT * FROM users WHERE handle LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface FundingDao {
    @Query("SELECT * FROM fundings ORDER BY createdAt DESC")
    fun getAllFundings(): Flow<List<FundingEntity>>

    @Query("SELECT * FROM fundings WHERE id = :id LIMIT 1")
    fun getFundingByIdFlow(id: String): Flow<FundingEntity?>

    @Query("SELECT * FROM fundings WHERE id = :id LIMIT 1")
    suspend fun getFundingById(id: String): FundingEntity?

    @Query("SELECT * FROM fundings WHERE title LIKE '%' || :query || '%' OR handle LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchFundings(query: String): Flow<List<FundingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunding(funding: FundingEntity)

    @Update
    suspend fun updateFunding(funding: FundingEntity)
}

@Dao
interface FundingContributionDao {
    @Query("SELECT * FROM funding_contributions WHERE fundingId = :fundingId ORDER BY timestamp DESC")
    fun getContributionsForFunding(fundingId: String): Flow<List<FundingContributionEntity>>

    @Query("SELECT SUM(votingPower) FROM funding_contributions WHERE fundingId = :fundingId AND userDid = :userDid")
    fun getUserVotingPowerForFunding(fundingId: String, userDid: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: FundingContributionEntity)
}

@Dao
interface AgendaDao {
    @Query("SELECT * FROM agendas WHERE fundingId = :fundingId ORDER BY totalVotes DESC, createdAt DESC")
    fun getAgendasForFunding(fundingId: String): Flow<List<AgendaEntity>>

    @Query("SELECT * FROM agendas WHERE fundingId = :fundingId AND isFinalized = 1 ORDER BY totalVotes DESC")
    fun getAgreedAgendasForFunding(fundingId: String): Flow<List<AgendaEntity>>

    @Query("SELECT * FROM agendas WHERE fundingId = :fundingId AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY totalVotes DESC")
    fun searchAgendas(fundingId: String, query: String): Flow<List<AgendaEntity>>

    @Query("SELECT * FROM agendas WHERE id = :id LIMIT 1")
    suspend fun getAgendaById(id: String): AgendaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgenda(agenda: AgendaEntity)

    @Update
    suspend fun updateAgenda(agenda: AgendaEntity)
}

@Dao
interface AgendaVoteDao {
    @Query("SELECT * FROM agenda_votes WHERE agendaId = :agendaId AND userDid = :userDid LIMIT 1")
    suspend fun getUserVote(agendaId: String, userDid: String): AgendaVoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgendaVote(vote: AgendaVoteEntity)
}

@Dao
interface PromiseDao {
    @Query("SELECT * FROM promises WHERE fundingId = :fundingId ORDER BY eventTime DESC LIMIT 1")
    fun getPromiseForFunding(fundingId: String): Flow<PromiseEntity?>

    @Query("SELECT * FROM promises WHERE fundingId = :fundingId ORDER BY eventTime DESC LIMIT 1")
    suspend fun getPromiseForFundingSync(fundingId: String): PromiseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromise(promise: PromiseEntity)

    @Update
    suspend fun updatePromise(promise: PromiseEntity)
}

@Dao
interface MarketplaceDao {
    @Query("SELECT * FROM marketplace_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<MarketplaceItemEntity>>

    @Query("SELECT * FROM marketplace_items WHERE id = :id LIMIT 1")
    fun getItemByIdFlow(id: String): Flow<MarketplaceItemEntity?>

    @Query("SELECT * FROM marketplace_items WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchItems(query: String): Flow<List<MarketplaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MarketplaceItemEntity)

    @Update
    suspend fun updateItem(item: MarketplaceItemEntity)
}

@Dao
interface FeedPostDao {
    @Query("SELECT * FROM feed_posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<FeedPostEntity>>

    @Query("SELECT * FROM feed_posts WHERE authorDid = :did ORDER BY createdAt DESC")
    fun getPostsByAuthor(did: String): Flow<List<FeedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: FeedPostEntity)

    @Update
    suspend fun updatePost(post: FeedPostEntity)
}

@Dao
interface FollowDao {
    @Query("SELECT * FROM follows WHERE followerDid = :followerDid AND followingDid = :followingDid LIMIT 1")
    suspend fun getFollow(followerDid: String, followingDid: String): FollowEntity?

    @Query("SELECT COUNT(*) FROM follows WHERE followerDid = :followerDid AND followingDid = :followingDid")
    fun isFollowingFlow(followerDid: String, followingDid: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: FollowEntity)

    @Query("DELETE FROM follows WHERE followerDid = :followerDid AND followingDid = :followingDid")
    suspend fun deleteFollow(followerDid: String, followingDid: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_rooms ORDER BY lastMessageTime DESC")
    fun getAllChatRooms(): Flow<List<ChatRoomEntity>>

    @Query("SELECT * FROM chat_rooms WHERE roomId = :roomId LIMIT 1")
    suspend fun getChatRoom(roomId: String): ChatRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(room: ChatRoomEntity)

    @Update
    suspend fun updateChatRoom(room: ChatRoomEntity)

    @Query("SELECT * FROM chat_messages WHERE chatRoomId = :chatRoomId ORDER BY timestamp ASC")
    fun getMessagesForRoom(chatRoomId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
}

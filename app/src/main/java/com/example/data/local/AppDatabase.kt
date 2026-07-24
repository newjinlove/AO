package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        FundingEntity::class,
        FundingContributionEntity::class,
        AgendaEntity::class,
        AgendaVoteEntity::class,
        PromiseEntity::class,
        MarketplaceItemEntity::class,
        FeedPostEntity::class,
        FollowEntity::class,
        ChatMessageEntity::class,
        ChatRoomEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun fundingDao(): FundingDao
    abstract fun fundingContributionDao(): FundingContributionDao
    abstract fun agendaDao(): AgendaDao
    abstract fun agendaVoteDao(): AgendaVoteDao
    abstract fun promiseDao(): PromiseDao
    abstract fun marketplaceDao(): MarketplaceDao
    abstract fun feedPostDao(): FeedPostDao
    abstract fun followDao(): FollowDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aeo_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

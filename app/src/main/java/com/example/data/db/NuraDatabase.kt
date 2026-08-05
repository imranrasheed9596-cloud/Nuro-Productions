package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PostEntity::class,
        ReelEntity::class,
        StoryEntity::class,
        NoteEntity::class,
        DirectMessageEntity::class,
        CommunityGroupEntity::class,
        QuranBookmarkEntity::class,
        ZakatLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NuraDatabase : RoomDatabase() {
    abstract fun nuraDao(): NuraDao

    companion object {
        @Volatile
        private var INSTANCE: NuraDatabase? = null

        fun getDatabase(context: Context): NuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NuraDatabase::class.java,
                    "nura_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

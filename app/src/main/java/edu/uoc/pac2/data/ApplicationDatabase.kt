package edu.uoc.pac2.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room Application Database
 */

@Database(entities = [Book::class], version = 1)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
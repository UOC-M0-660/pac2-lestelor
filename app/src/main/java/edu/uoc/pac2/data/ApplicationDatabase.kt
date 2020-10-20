package edu.uoc.pac2.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.chrono.IsoChronology.INSTANCE

/**
 * Room Application Database
 */
private const val DATABASE = "Book"
@Database(
        entities = [Book::class],
        version = 1,
        exportSchema = false
)
public abstract class ApplicationDatabase: RoomDatabase() {
    abstract fun bookDao(): BookDao
    private class BookDatabaseCallback(
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val bookDao = database.bookDao()

                    // Delete all content here.
                    //bookDao.

                    // Add sample books.
                    val book = Book(0,"title","author","description","date","url")
                    bookDao.saveBook(book)

                }
            }
        }
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var INSTANCE: ApplicationDatabase? = null

        fun getInstance(
                context: Context,
                scope: CoroutineScope
        ): ApplicationDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ApplicationDatabase::class.java,
                        "word_database"
                )
                        .addCallback(BookDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        /*fun getInstance(context: Context, scope: CoroutineScope): ApplicationDatabase {
            return instance ?: synchronized(this) {
                instance
                        ?: buildDatabase(context, scope).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, scope:CoroutineScope): ApplicationDatabase {
            return Room.databaseBuilder(context, ApplicationDatabase::class.java, DATABASE)
                    //.addCallback(BookDatabaseCallback(scope))
                    .build()
        }*/
    }

}

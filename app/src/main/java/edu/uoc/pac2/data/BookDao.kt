package edu.uoc.pac2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Book Dao (Data Access Object) for accessing Book Table functions.
 */

@Dao
interface BookDao {
    @Query("SELECT * FROM Book")
    fun getAllBooks(): List<Book>

    @Query("SELECT * FROM Book WHERE uid LIKE :id LIMIT 1")
    fun getBookById(id: Int): Book?

    @Query("SELECT * FROM Book WHERE title LIKE :titleBook LIMIT 1")
    fun getBookByTitle(titleBook: String): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveBook(book: Book): Long
}
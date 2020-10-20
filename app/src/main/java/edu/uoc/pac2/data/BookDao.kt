package edu.uoc.pac2.data

import androidx.lifecycle.LiveData
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
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE uid = :id")
    fun getBookById(id: Int): LiveData<Book>?

    @Query("SELECT * FROM Book WHERE title = :titleBook")
    fun getBookByTitle(titleBook: String): LiveData<Book>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBook(book: Book): Long
}
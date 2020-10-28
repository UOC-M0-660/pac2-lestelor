package edu.uoc.pac2.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * This class Interacts with {@param bookDao} to perform operations in the local database.
 *
 * Could be extended also to interact with Firestore, acting as a single entry-point for every
 * book-related operation from all different datasources (Room & Firestore)
 *
 * Created by alex on 03/07/2020.
 */
class BooksInteractor(private val bookDao: BookDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    //val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

    // TODO: Get All Books from DAO
    fun getAllBooks(): LiveData<List<Book>> {
        return bookDao.getAllBooks()
    }

    // TODO: Save Book
    suspend fun saveBook(book: Book) {
        bookDao.saveBook(book)
    }

    // TODO: Save List of Books
    suspend fun saveBooks(books: List<Book>) {
        books.forEach { saveBook(it) }
    }

    // TODO: Get Book by id
    fun getBookById(id: Int): LiveData<Book>? {
        return bookDao.getBookById(id)
    }

    // TODO: Get Book by id
    fun getBookByTitle(titleBook: String): LiveData<Book>? {
        return bookDao.getBookByTitle(titleBook)
    }

}
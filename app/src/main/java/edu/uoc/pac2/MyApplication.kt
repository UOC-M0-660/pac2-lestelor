package edu.uoc.pac2

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import edu.uoc.pac2.data.ApplicationDatabase
import edu.uoc.pac2.data.BookDao
import edu.uoc.pac2.data.BooksInteractor
import kotlinx.coroutines.CoroutineScope


/**
 * Entry point for the Application.
 */
class MyApplication : Application() {

    private lateinit var booksInteractor: BooksInteractor
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var booksDao:BookDao
    override fun onCreate() {
        super.onCreate()

        // TODO: Init Room Database
        // TODO: Init BooksInteractor
        booksDao = ApplicationDatabase.getInstance(applicationContext, AndroidViewModel(applicationContext as Application).viewModelScope).bookDao()
        booksInteractor = BooksInteractor(booksDao)
        viewModelScope = AndroidViewModel(applicationContext as Application).viewModelScope
    }

    fun getBooksInteractor(): BooksInteractor {
        return booksInteractor
    }

    fun getViewModelScope(): CoroutineScope {
        return viewModelScope
    }

    fun hasInternetConnection(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetwork
        return (activeNetwork != null)

    }
}
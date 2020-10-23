package edu.uoc.pac2.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book
import edu.uoc.pac2.data.BooksInteractor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * An activity representing a list of Books.
 */
class BookListActivity : AppCompatActivity() {

    private lateinit var mAdView : AdView
    private lateinit var myApplication: MyApplication
    private val TAG = "BookListActivity"
    private lateinit var adapter: BooksListAdapter
    private var books: List<Book>? = null
    private lateinit var allBooksFlow: Flow<List<Book>>
    private lateinit var allBooks: LiveData<List<Book>>
    private lateinit var booksInteractor: BooksInteractor
    private lateinit var viewModelScope:CoroutineScope


    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)



        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        myApplication = applicationContext as MyApplication

        // Init UI
        initToolbar()
        initRecyclerView()

        // initialize interactor
        booksInteractor = myApplication.getBooksInteractor()

        // define scope for livedata
        viewModelScope = myApplication.getViewModelScope()

        // Get Books
        getBooks()
    }

    // Init Top Toolbar
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_activity)
        setSupportActionBar(toolbar)
        toolbar.title = title
    }

    // Init RecyclerView
    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.book_list)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = BooksListAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    // TODO: Get Books and Update UI
    private fun getBooks() {


        if (!myApplication.hasInternetConnection()) {
            loadBooksFromLocalDb()
        } else        {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("books")
            docRef.addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                books = querySnapshot?.documents?.mapNotNull { it.toObject(Book::class.java) }
                books?.let {
                    adapter.setBooks(it)
                    saveBooksToLocalDatabase(books!!)
                }

                for (doc in querySnapshot?.documentChanges!!) {
                    if (doc?.type == DocumentChange.Type.MODIFIED) {
                        books?.get(doc.document.id.toInt())?.let {saveBookToLocalDatabase(it)}
                    }
                }

            }
        }
    }

    // TODO: Load Books from Room
    private fun loadBooksFromLocalDb() {
        allBooks = booksInteractor.getAllBooks()
        allBooks.observe(this, Observer { books ->
            // Update the cached copy of the books in the adapter.
            books?.let {
                adapter.setBooks(books)
            }
        })


    }

    // TODO: Save Books to Local Storage
    private fun saveBooksToLocalDatabase(books: List<Book>) {
        viewModelScope.launch(Dispatchers.IO) {
            booksInteractor.saveBooks(books)
        }
        /*allBooksFlow = flow<List<Book>> {
            emit(booksInteractor.getAllBooks())
        }
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                allBooksFlow.collect { value:List<Book> -> allBooks.value = value)
            }
        }*/
    }

    // Save Book to Local Storage
    private fun saveBookToLocalDatabase(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            booksInteractor.saveBook(book)
        }
    }

}
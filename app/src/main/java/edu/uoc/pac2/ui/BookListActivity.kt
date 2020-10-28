package edu.uoc.pac2.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


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

        // Enable MyApplication as applicationContext
        myApplication = applicationContext as MyApplication

        // init AdMob adds
        initAdMob()

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

    // init AdMob adds
    private fun initAdMob() {
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
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

        // If no Internet load from local database
        // else get books from Firestore save them to local Room Database and listen changes ->
        // save modified book to Room
        if (!myApplication.hasInternetConnection()) {
            // No internet
            loadBooksFromLocalDb()
        } else        {
            //Internet
            // Get Firebase Database, collection books
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("books")
            docRef.addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                // Map the recovered books to the class Book, must have the same identifiers
                books = querySnapshot?.documents?.mapNotNull { it.toObject(Book::class.java) }
                // Set Recycler View Adapter and save books to Local database
                books?.let {
                    adapter.setBooks(it)
                    saveBooksToLocalDatabase(books!!)
                }

                // If any change in any field, save entire book to local database
                // Using a device API 26 it is possible to inspect the Database View/Tools Window/Database Inspector
                // Refresh -> to see any change
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
        //Observe the live data for changes. When the local database is loaden into allbokks then update the recyclerview adapter
        allBooks.observe(this, Observer { books ->
            // Update the cached copy of the books in the adapter.
            books?.let {
                adapter.setBooks(books)
            }
        })

        // Deprecated way to call an async task. First change LiveData<List<Book>> by just List<Book>
        // Ui can only be accessed on the main Thread, so when in background (e.g. BookDetailFragment) it is necessary to come back to the main in orde to update the ui
        /*AsyncTask.execute {
            // Your background code here
            allBooks = booksInteractor.getAllBooks()
            adapter.setBooks(allBooks)
            Log.d("cfauli", "loadBooksFromLocalDb asynctask after allbooks ")

            runOnUiThread {
                // Main code here
                //Log.d("cfauli", "loadBooksFromLocalDb runonuithread ")
            }
        }*/
    }

// TODO: Save Books to Local Storage
private fun saveBooksToLocalDatabase(books: List<Book>) {
    // Send an order to save books to local database. Observers can recover the data
    viewModelScope.launch(Dispatchers.IO) {
    booksInteractor.saveBooks(books)
}
/* Not successful try on using kotlin coroutines flow
allBooksFlow = flow<List<Book>> {
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
package edu.uoc.pac2.ui

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book
import edu.uoc.pac2.data.BooksInteractor
import kotlinx.android.synthetic.main.activity_book_list.*

/**
 * An activity representing a list of Books.
 */
class BookListActivity : AppCompatActivity() {

    private val TAG = "BookListActivity"

    private var database: FirebaseFirestore = Firebase.firestore
    private lateinit var adapter: BooksListAdapter

    private var booksListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        // Init UI
        initToolbar()
        initRecyclerView()

        // Get Books
        getBooks()

        // Add books data to Firestore [Use for new project with empty Firestore Database]
        // FirestoreBookData.addBooksDataToFirestoreDatabase()

        // Init AdMob
        initAdMob()
    }

    // Init Top Toolbar
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
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

    // Get Books and Update UI
    private fun getBooks() {
        // First load whatever is stored locally
        loadBooksFromLocalDb()
        // Check if Internet is available
        if ((application as MyApplication).hasInternetConnection()) {
            // Internet connection is available, get remote data
            booksListener = database.collection("books")
                    // Subscribe to remote book changes
                    .addSnapshotListener { querySnapshot, exception ->
                        // Success
                        querySnapshot?.let {
                            val books: List<Book> = querySnapshot.documents.mapNotNull {
                                it.toObject(Book::class.java)
                            }
                            Log.i(TAG, "Got #${books.count()} books from Firestore")
                            // Update Local content
                            saveBooksToLocalDatabase(books)
                            // Update UI
                            adapter.setBooks(books)
                        }
                        // Error
                        exception?.let {
                            Log.w(TAG, "Error retrieving books from Firestore: $it")
                        }
                    }
        }
    }

    // Load Books from Room
    private fun loadBooksFromLocalDb() {
        val booksInteractor: BooksInteractor = (application as MyApplication).getBooksInteractor()
        // Run in Background, accessing the local database is a memory-expensive operation
        AsyncTask.execute {
            // Get Books
            val books = booksInteractor.getAllBooks()
            // Update Adapter on the UI Thread
            runOnUiThread {
                adapter.setBooks(books)
            }
        }
    }

    // Save Books to Local Storage
    private fun saveBooksToLocalDatabase(books: List<Book>) {
        val booksInteractor: BooksInteractor = (application as MyApplication).getBooksInteractor()
        // Run in Background; accessing the local database is a memory-expensive operation
        AsyncTask.execute {
            booksInteractor.saveBooks(books)
        }
    }

    private fun initAdMob() {
        MobileAds.initialize(this) {
            Log.i(TAG, "Admob initialize completed with status: $it")
        }
        // Load Ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        // Optional: set some listeners
        adView.adListener = object : AdListener() {
            override fun onAdOpened() {
                Log.i(TAG, "Ad opened! $$$$")
            }
        }
    }

    override fun onDestroy() {
        // IMPORTANT! Remove Firestore Change Listener to prevent memory leaks
        booksListener?.remove()
        super.onDestroy()
    }
}
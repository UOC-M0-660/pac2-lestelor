package edu.uoc.pac2.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.ApplicationDatabase
import edu.uoc.pac2.data.Book
import edu.uoc.pac2.data.BooksInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An activity representing a list of Books.
 */
class BookListActivity : AppCompatActivity() {

    private lateinit var myApplication: MyApplication
    private val TAG = "BookListActivity"

    private lateinit var adapter: BooksListAdapter
    private var books: List<Book>? = null
    private var books2: LiveData<List<Book>>?  = null
    private lateinit var booksInteractor: BooksInteractor
    private lateinit var viewModelScope:CoroutineScope



    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        myApplication = applicationContext as MyApplication

        // Init UI
        initToolbar()
        initRecyclerView()




        booksInteractor = myApplication.getBooksInteractor()
        viewModelScope = AndroidViewModel(application).viewModelScope


        // Get Books
        getBooks(this)




        /*bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        bookViewModel.allBooks.observe(this, Observer { books ->
            // Update the cached copy of the words in the adapter.
            books?.let { adapter.setBooks(books)}
        })*/

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
        adapter = BooksListAdapter(emptyList(), books2)
        recyclerView.adapter = adapter
    }

    // TODO: Get Books and Update UI
    private fun getBooks(lifecycleOwner: LifecycleOwner) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("books")


        if (!myApplication.hasInternetConnection()) {
                val allBooks = booksInteractor.getAllBooks()
                allBooks.observe(lifecycleOwner, Observer { books ->
                    // Update the cached copy of the books in the adapter.
                    books?.let {
                        adapter.setBooks(books)
                    }
                })
        } else        {
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
                        viewModelScope.launch(Dispatchers.IO) {
                            books?.get(doc.document.id.toInt())?.let { booksInteractor.saveBook(it) }
                        }
                    }
                }

            }
        }
    }

    // TODO: Load Books from Room
    private fun loadBooksFromLocalDb() {

    }

    // TODO: Save Books to Local Storage
    private fun saveBooksToLocalDatabase(books: List<Book>) {
        viewModelScope.launch(Dispatchers.IO) {
            booksInteractor.saveBooks(books)
        }
    }


}
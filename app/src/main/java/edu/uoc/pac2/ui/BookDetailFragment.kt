package edu.uoc.pac2.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoProvider
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book
import kotlinx.android.synthetic.main.fragment_book_detail.*
import java.io.File
import kotlin.properties.Delegates

/**
 * A fragment representing a single Book detail screen.
 * This fragment is contained in a [BookDetailActivity].
 */
class BookDetailFragment : Fragment() {
    private lateinit var myApplication: MyApplication
    private lateinit var toolbar: Toolbar
    private lateinit var fab:FloatingActionButton
    private var bookId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        arguments?.let {
            bookId = it.getInt(ARG_ITEM_ID)
        }
        toolbar = activity?.findViewById<Toolbar>(R.id.detail_toolbar)!!
        fab = activity?.findViewById<FloatingActionButton>(R.id.fab)!!

        myApplication = activity!!.application as MyApplication

        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get Book for this detail screen
        loadBook()
    }


    // TODO: Get Book for the given {@param ARG_ITEM_ID} Book id
    private fun loadBook() {
        //throw NotImplementedError()
        var book: Book
        bookId.let {
            val liveBook = myApplication.getBooksInteractor().getBookById(bookId!! + 1)
            liveBook?.observe(this, Observer { bookObserved ->
                // Update the cached copy of the books in the adapter.
                bookObserved?.let {
                    book = bookObserved
                    initUI(book)
                    fab.setOnClickListener {shareContent(book)}
                }
            })
        }
    }

    // TODO: Init UI with book details
    private fun initUI(book: Book) {
        //throw NotImplementedError()
        val actionBarTittle = book.title
        toolbar.title =actionBarTittle

        fragment_book_author.text = book.author
        fragment_book_date.text = book.publicationDate
        fragment_book_description.text = book.description
        Picasso.get().load(book.urlImage).resize(400,700).into(fragment_book_image)
    }

    // TODO: Share Book Title and Image URL
    private fun shareContent(book: Book) {
        //throw NotImplementedError()
        val intent = Intent(Intent.ACTION_SEND)


        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("cfauli@edu.uoc"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "PEC2")
        intent.putExtra(Intent.EXTRA_TEXT, book.title +"\n" + book.urlImage)
        intent.putExtra(Intent.EXTRA_STREAM, book.urlImage)
        intent.type = "message/rfc822"
        startActivity(Intent.createChooser(intent, "Send Email using:"));
    }

    companion object {
        /**
         * The fragment argument representing the item title that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "itemIdKey"

        fun newInstance(itemId: Int): BookDetailFragment {
            val fragment = BookDetailFragment()
            val arguments = Bundle()
            arguments.putInt(ARG_ITEM_ID, itemId)
            fragment.arguments = arguments
            return fragment
        }
    }
}
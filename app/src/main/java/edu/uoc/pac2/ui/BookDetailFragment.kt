package edu.uoc.pac2.ui

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import edu.uoc.pac2.MyApplication
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book

/**
 * A fragment representing a single Book detail screen.
 * This fragment is contained in a [BookDetailActivity].
 */
class BookDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get Book for this detail screen
        loadBook()
    }


    // Get Book for the given {@param ARG_ITEM_ID} Book id
    private fun loadBook() {
        // Get Books Interactor
        val booksInteractor = (requireActivity().application as MyApplication).getBooksInteractor()
        // Here we force cast arguments, which could be null
        // This screen should never be opened without a detail itemId in the arguments.
        // If it's null the app will crash
        val id = arguments!!.getInt(ARG_ITEM_ID)
        // Get book
        AsyncTask.execute {
            val book: Book? = booksInteractor.getBookById(id)
            activity?.runOnUiThread {
                book?.let {
                    initUI(book)
                } ?: run {
                    // Book not found, alert user and finish
                    Toast.makeText(requireContext(), R.string.book_not_found, Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }
            }
        }
    }

    // Init UI with book details
    private fun initUI(book: Book) {
        requireView().findViewById<TextView>(R.id.book_author).text = book.author
        requireView().findViewById<TextView>(R.id.book_date).text = book.publicationDate
        requireView().findViewById<TextView>(R.id.book_detail).text = book.description
        val bookImage = requireView().findViewById<ImageView>(R.id.book_image)
        Picasso.get().load(book.urlImage).into(bookImage)
        // Init AppBar
        val headerImage = requireView().findViewById<ImageView>(R.id.image_header)
        Picasso.get().load(book.urlImage).into(headerImage)
        val appBarLayout: CollapsingToolbarLayout = requireView().findViewById(R.id.toolbar_layout)
        appBarLayout.title = book.title
        val toolbar = requireView().findViewById<Toolbar>(R.id.detail_toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Init Share FAB
        val fab: FloatingActionButton = requireView().findViewById(R.id.fab_share)
        fab.setOnClickListener {
            shareContent(book)
        }
    }

    // Share Book Title and Image URL
    private fun shareContent(book: Book) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, book.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, "${book.title}\n${book.urlImage}")
        shareIntent.type = "text/*"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_info)))
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
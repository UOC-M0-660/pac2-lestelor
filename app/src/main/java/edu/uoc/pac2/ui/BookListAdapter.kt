package edu.uoc.pac2.ui

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac2.R
import edu.uoc.pac2.data.Book

/**
 * Adapter for a list of Books.
 */

class BooksListAdapter(private var books: List<Book>) : RecyclerView.Adapter<BooksListAdapter.ViewHolder>() {

    private val evenViewType = 0
    private val oddViewType = 1

    private fun getBook(position: Int): Book {
        return books[position]
    }
    // To init the recyclerview and maintain updated when there is changes in the entry variable books
    fun setBooks(books: List<Book>) {
        this.books = books
        // Reloads the RecyclerView with new adapter data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            evenViewType
        } else {
            oddViewType
        }
    }

    // Creates View Holder for re-use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = when (viewType) {
            evenViewType -> {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_book_list_content_even, parent, false)
            }
            oddViewType -> {
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_book_list_content_odd, parent, false)
            }
            else -> {
                throw IllegalStateException("Unsupported viewType $viewType")
            }
        }
        return ViewHolder(view)
    }

    // Binds re-usable View for a given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = getBook(position)
        holder.titleView.text = book.title
        holder.authorView.text = book.author

        // tag as a way to inform the onclicklistener on the position, set the onclicklistener
        with(holder.itemView) {
            tag = position
            setOnClickListener(onClickListener)
        }
    }

    private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
        //Log.d("cfauli", "onClickListener item" + v.tag + " " + books2?.get(0)?.title )
        val item = v.tag as Int
        Log.d("cfauli", "onClickListener  " + item)
        val intent = Intent(v.context, BookDetailActivity::class.java).apply {
            putExtra(BookDetailFragment.ARG_ITEM_ID, v.tag as Int)
        }
        v.context.startActivity(intent)
    }


    // Returns total items in Adapter
    override fun getItemCount(): Int {
        return books.size
    }

    // Holds an instance to the view for re-use
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.title)
        val authorView: TextView = view.findViewById(R.id.author)
    }

}

package edu.uoc.pac2.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import edu.uoc.pac2.R
import kotlinx.coroutines.delay

/**
 * An activity representing a single Book detail screen.
 */
class BookDetailActivity : AppCompatActivity() {

    private lateinit var toolbar:Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Not find the way to init the supportactionbar in the fragment. Done here.
        // Put a Toolbar in xml in order to show the back button, the override the actions (below)
        // The height is the minimum toolbar is shown after collapsing
        initToolBar()

        // animate view. Image and text apper from bottom
        animateView()

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val itemID = intent.getIntExtra(BookDetailFragment.ARG_ITEM_ID, -1)
            val fragment = BookDetailFragment.newInstance(itemID)
            supportFragmentManager.beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit()
        }


    }

    // TODO: Override finish animation for actionbar back arrow
    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {

                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. Use NavUtils to allow users
                    // to navigate up one level in the application structure. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    // Animate the view sending image and text to the bottom
                    animationOut()
                    goToMainActivity()
                    NavUtils.navigateUpTo(this, Intent(this, BookListActivity::class.java))

                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    // Same behaviour as back item selected
    // TODO: Override finish animation for phone back button
    override fun onBackPressed() {
        super.onBackPressed()
        animationOut()
        goToMainActivity()
    }

    private fun initToolBar() {
        toolbar = findViewById<Toolbar>(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        // If title enabled, it shows "Book Detail" instead of the name of the book. We want to show the
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // show the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun animateView() {
        val bottomUp: Animation = AnimationUtils.loadAnimation(this,
                R.anim.translate_in_bottom)
        val hiddenPanel = findViewById<ViewGroup>(R.id.book_detail_container)
        hiddenPanel.startAnimation(bottomUp)
        hiddenPanel.visibility = View.VISIBLE
    }

    private fun goToMainActivity() {
        NavUtils.navigateUpTo(this, Intent(this, BookListActivity::class.java))
    }

    private fun animationOut() {
        val upBottom: Animation = AnimationUtils.loadAnimation(this,
                R.anim.translate_out_top)
        val hiddenPanel = findViewById<ViewGroup>(R.id.book_detail_container)
        hiddenPanel.startAnimation(upBottom)
        hiddenPanel.visibility = View.VISIBLE
    }
}
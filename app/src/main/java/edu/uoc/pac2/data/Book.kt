package edu.uoc.pac2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A book Model representing a piece of content.
 */

@Entity(tableName = "Book")
data class Book(
        @PrimaryKey val uid: Int = -1,
        val title: String? = null,
        val author: String? = null,
        val publicationDate: String? = null,
        val description: String? = null,
        val urlImage: String? = null
)
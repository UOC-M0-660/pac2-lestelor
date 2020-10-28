package edu.uoc.pac2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A book Model representing a piece of content.
 */
@Entity
data class Book(
        @PrimaryKey
        val uid:Int? = 0,
        val title: String? = "",
        var author: String? = "",
        val description: String? = "",
        val publicationDate: String? = "",
        val urlImage: String? = ""
)
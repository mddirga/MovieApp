package com.systema.movieapp.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//untuk mengambil film dari database
@Parcelize
data class Film (
    var desc: String ?="",
    var director: String ?="",
    var genre: String ?="",
    var judul: String ?="",
    var poster: String ?="",
    var rating: String ?=""
): Parcelable
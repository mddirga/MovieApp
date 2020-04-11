package com.systema.movieapp.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//untuk mengambil film dari database
@Parcelize
data class Credit (
    var detail: String ?="",
    var id: String ?="",
    var price: String ?="",
    var date: String ?=""
): Parcelable
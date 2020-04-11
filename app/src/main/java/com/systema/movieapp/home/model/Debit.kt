package com.systema.movieapp.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//untuk mengambil film dari database
@Parcelize
data class Debit (
    var detail: String ?="",
    var amount: String ?="",
    var id: String ?="",
    var date: String ?=""
): Parcelable
package com.systema.movieapp.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//untuk menampilkan siapa yang bermain
@Parcelize
data class Plays (
    var nama: String ?="",
    var url: String ?=""
): Parcelable
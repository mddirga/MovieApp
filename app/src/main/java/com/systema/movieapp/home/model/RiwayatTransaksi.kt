package com.systema.movieapp.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//untuk mengambil film dari database
@Parcelize
data class RiwayatTransaksi (
    var user: String ?="",
    var pemasukan: String ?="",
    var pengeluaran: String ?=""
): Parcelable
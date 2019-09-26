package ru.sbrf.sberwifi

import android.net.wifi.ScanResult
import android.os.Parcel
import android.os.Parcelable

public class ResultWiFi constructor(val scan: ScanResult) : Parcelable {

    override fun toString(): String {
        return scan.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(scan, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultWiFi> {
        override fun createFromParcel(parcel: Parcel): ResultWiFi {
            val list = parcel.readParcelable<ScanResult>(ResultWiFi::class.java.classLoader)
            return ResultWiFi(list!!)
        }

        override fun newArray(size: Int): Array<ResultWiFi?> {
            return arrayOfNulls(size)
        }
    }
}
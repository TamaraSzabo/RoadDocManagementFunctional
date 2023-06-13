package com.example.roaddocmanagement.models

import android.os.Parcel
import android.os.Parcelable

data class DocType (
    var title : String="",
    val createdBy: String = "",
    val documents: ArrayList<Document> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Document.CREATOR)!!
    )


    override fun writeToParcel(dest: Parcel, flags: Int)= with(dest) {
        writeString(title)
        writeString(createdBy)
        writeTypedList(documents)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DocType> {
        override fun createFromParcel(parcel: Parcel): DocType {
            return DocType(parcel)
        }

        override fun newArray(size: Int): Array<DocType?> {
            return arrayOfNulls(size)
        }
    }
}
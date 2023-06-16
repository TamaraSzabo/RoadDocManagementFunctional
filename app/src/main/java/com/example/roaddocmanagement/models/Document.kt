package com.example.roaddocmanagement.models

import android.os.Parcel
import android.os.Parcelable

data class Document(
    val name: String = "",
    val createdBy: String = "",
    val image: String = "",
    val expirationDate: Long = 0
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong()
    )

    override fun describeContents() = 0;

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(createdBy)
        writeString(image)
        writeLong(expirationDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Document> = object : Parcelable.Creator<Document> {
            override fun createFromParcel(source: Parcel): Document = Document(source)
            override fun newArray(size: Int): Array<Document?> = arrayOfNulls(size)
        }
    }
}

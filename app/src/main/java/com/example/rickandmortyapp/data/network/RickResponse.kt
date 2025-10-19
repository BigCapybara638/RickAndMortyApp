package com.example.rickandmortyapp.data.network

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class RickResponse(
    @SerializedName("info") val info: Info,
    @SerializedName("results") val result: List<CharacterItem>
)

data class Info(
    @SerializedName("count") val count: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("next") val next: String,
)
@Parcelize
data class CharacterItem(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("species") val species: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("episode") val episodes: List<String>,

    ) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(status)
        dest.writeString(imageUrl)
        dest.writeString(species)
        dest.writeString(gender)
        dest.writeList(episodes)
    }
}

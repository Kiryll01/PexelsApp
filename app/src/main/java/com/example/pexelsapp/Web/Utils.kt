package com.example.pexelsapp.Web

import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.net.toUri
import coil.ImageLoader
import coil.load
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.ImageItemBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.*

//abstract class AbstractListConverter<T> {
//
//    private val gson= Gson()
//    @TypeConverter
//    fun mapListToString(value: List<T>): String {
//        val type = object : TypeToken<List<T>>() {}.type
//        return gson.toJson(value, type)
//    }
//
//    @TypeConverter
//    fun mapStringToList(value: String): List<T> {
//        val type = object : TypeToken<List<T>>() {}.type
//        return gson.fromJson(value, type)
//    }
//}
//class StringListConverter : AbstractListConverter<String>()



fun loadImage (imgUri : String, image : ImageView) {
  val uri = imgUri
        ?.let { it.toUri().buildUpon().scheme("https").build() }
    image.load(uri) {
        placeholder(R.drawable.download_icon)
        error(R.drawable.icon_download_error)
    }
}


@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class NullableString
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class NullableDouble
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class NullableInt
class DoubleNullableAdapter  {
    @FromJson
    @NullableDouble
    fun fromJson(@Nullable data : String?): Double? {
        if(data==null || data == "null")return 0.0
        return data.toDouble()
    }
    @ToJson
    fun toJson(@NullableDouble value: Double?) : Double?{
        return value
    }
}
class IntNullableAdapter  {
    @FromJson
    @NullableInt
    fun fromJson(@Nullable data : String?): Int {
        if(data==null || data == "null")return 0
        return data.toInt()
    }
    @ToJson
    fun toJson(@NullableInt value: Int) : Int{
        return value
    }
}
class StringNullableAdapter  {

    @FromJson
    @NullableString
    fun fromJson(@Nullable data : String?): String {
        return data ?:""
    }
    @ToJson
    fun toJson(@NullableString value: String?) : String?{
        return value
    }
}
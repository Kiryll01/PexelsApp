package com.example.pexelsapp.Web

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.net.toUri
import coil.ImageLoader
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.pexelsapp.PexelsGlideApp
import com.example.pexelsapp.R
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



fun loadImage(imgUri: String, image: ImageView) {
    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.image_placeholder)
    val uri = imgUri.toUri().buildUpon().scheme("https").build()

    Glide.with(image.context)
        .load(uri)
        .apply(requestOptions)
        .into(image)
}

fun loadImageWithCallback(imgUri: String,
                                      image: ImageView,
                                      onResourceReadyCallback : ()->Unit = {},
                                      onResourceFailCallback : ()->Unit = {},
                          ){
    val uri = imgUri.toUri().buildUpon().scheme("https").build()

    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.image_placeholder)

    Glide.with(image.context)
        .load(uri)
        .listener(object : RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceFailCallback.invoke()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onResourceReadyCallback.invoke()
                return false
            }

        })
        .apply(requestOptions)
        .into(image)
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
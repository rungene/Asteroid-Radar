package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidListAdapter
import com.udacity.asteroidradar.main.ImageApiStatus



@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
@BindingAdapter("url", "status")
fun bindImageOfTheDay(imageView: ImageView, url:String?,status: ImageApiStatus?){
    when (status) {
        ImageApiStatus.LOADING -> {
            imageView.setImageResource(R.drawable.loading_animation)
        }
        ImageApiStatus.ERROR -> {
            imageView.setImageResource(R.drawable.connection_error)
        }
        ImageApiStatus.DONE -> {
            url?.let {
                val imgUri = url.toUri().buildUpon().scheme("https").build()
                Picasso.get()
                        .load(imgUri)
                        .placeholder(R.drawable.placeholder_picture_of_day)
                        .into(imageView)


            }

        }
    }
}

/**
 * When there is no Mars property data (data is null), hide the [RecyclerView], otherwise show it.
 */
/*
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidListAdapter
    adapter.submitList(data)
}
*/



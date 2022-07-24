package com.app.payseracurrencyexchange.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.app.payseracurrencyexchange.R
import com.app.payseracurrencyexchange.ui.base.ExchangeRatesStatus

@BindingAdapter("errorHandling")
fun networkStatus(statusImage : ImageView, status: ExchangeRatesStatus?){

    when(status){
        ExchangeRatesStatus.ERROR -> {
            statusImage.setImageResource(R.drawable.ic_connection_error)
            statusImage.visibility = View.VISIBLE
        }
        ExchangeRatesStatus.SUCCESS -> {
            statusImage.visibility = View.GONE
        }
    }


}
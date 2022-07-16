package com.app.payseracurrencyexchange.data.model

data class ConvertCurrencyResponse(
    val success: Boolean,
    val query: Query,
    val info: Info,
    val date: String,
    val result: Double
)

data class Info (
    val timestamp: Long,
    val rate: Double
)

data class Query (
    val from: String,
    val to: String,
    val amount: Long
)
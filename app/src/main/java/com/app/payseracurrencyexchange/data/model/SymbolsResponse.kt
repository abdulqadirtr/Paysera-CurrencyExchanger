package com.app.payseracurrencyexchange.data.model

/**
 * data class for symbols response from service
 */
data class SymbolsResponse (
        val success: Boolean,
        val symbols: Map<String, String>
    )
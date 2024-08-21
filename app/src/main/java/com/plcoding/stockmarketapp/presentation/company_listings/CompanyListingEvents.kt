package com.plcoding.stockmarketapp.presentation.company_listings

import com.plcoding.stockmarketapp.domain.model.CompanyListing

sealed class CompanyListingEvents {
    object refresh : CompanyListingEvents()
    data class onSearchQueryChange (val query : String) : CompanyListingEvents()

}
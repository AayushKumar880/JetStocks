package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListing(
        companyListingEntites: List<CompanyListingEntity>
    )

    @Query("DELETE FROM companylistingentity")
    suspend fun clearCompanyListing()


    @Query(
        """
            SELECT * 
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
    suspend fun searchCompanyListing(query : String) : List<CompanyListingEntity>
}

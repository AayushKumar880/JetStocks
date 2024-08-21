package com.plcoding.stockmarketapp.data.csv


import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.mapper.toIntradayInfo
import com.plcoding.stockmarketapp.data.remote.dto.IntradayInfoDto
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {

    override suspend fun parse(stream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            // Use ZonedDateTime to get the current date in the USA time zone (Eastern Time)
            val lastOpenDay = getLastMarketOpenDay(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDate())
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { line ->
                    val timestamp = line.getOrNull(0) ?: return@mapNotNull null
                    val close = line.getOrNull(4) ?: return@mapNotNull null
                    val dto = IntradayInfoDto(timestamp, close.toDouble())
                    dto.toIntradayInfo()
                }
                .filter {
                    it.date.toLocalDate() == lastOpenDay
                }
                .sortedBy {
                    it.date.hour
                }
                .also {
                    csvReader.close()
                }
        }
    }

    private fun getLastMarketOpenDay(currentDate: LocalDate): LocalDate {
        return when (currentDate.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> currentDate.minusDays(3) // Go back to Friday
            java.time.DayOfWeek.SUNDAY -> currentDate.minusDays(2) // Go back to Friday
            java.time.DayOfWeek.SATURDAY -> currentDate.minusDays(1) // Go back to Friday
            else -> currentDate.minusDays(1) // Any other day, just go back one day
        }
    }
}

package com.example.whalewatch.ui.main

import com.example.whalewatch.data.SecRepository
import com.example.whalewatch.data.model.FilterTab
import com.example.whalewatch.data.model.TradeType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
    @Test
    fun repository_initialDataLoaded() = runTest {
        val repo = SecRepository()
        val filings = repo.filings.value
        val stats = repo.stats.value

        assertTrue(filings.isNotEmpty())
        assertTrue(stats.totalFilings > 0)
        assertTrue(stats.buyCount > 0)
        assertTrue(stats.sellCount > 0)
    }

    @Test
    fun repository_toggleBookmarkWorks() = runTest {
        val repo = SecRepository()
        val firstFiling = repo.filings.value.first()
        val initialStatus = firstFiling.isBookmarked

        repo.toggleBookmark(firstFiling.accessionNumber)
        val updated = repo.filings.value.first { it.accessionNumber == firstFiling.accessionNumber }
        assertEquals(!initialStatus, updated.isBookmarked)
    }
}
package com.example.onlab

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ProductScreenTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickOnProductNavItem_navigateToProductsList() {
        composeRule.onNodeWithContentDescription("Termékek").assertExists()
        composeRule.onNodeWithContentDescription("Termékek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Termékek").performClick()

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()
    }

    @Test
    fun navigateToNewProductScreen(){
        composeRule.onNodeWithContentDescription("Termékek").assertExists()
        composeRule.onNodeWithContentDescription("Termékek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Termékek").performClick()

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()

        composeRule.onNodeWithText("Hozzáadás").assertExists()
        composeRule.onNodeWithText("Hozzáadás").assertIsDisplayed()
        composeRule.onNodeWithText("Hozzáadás").performClick()

        composeRule.onNodeWithText("Termék szerkesztése").assertExists()
        composeRule.onNodeWithText("Termék szerkesztése").assertIsDisplayed()
    }

    @Test
    fun onOpenProductDetails_ThenNavigateBack(){
        composeRule.onNodeWithContentDescription("Termékek").assertExists()
        composeRule.onNodeWithContentDescription("Termékek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Termékek").performClick()

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()

        composeRule.onNodeWithText("Bounty").assertExists()
        composeRule.onNodeWithText("Bounty").assertIsDisplayed()
        composeRule.onNodeWithText("Bounty").performClick()

        composeRule.onNodeWithContentDescription("Back").assertExists()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
    }

    @Test
    fun onSearchForProduct(){
        composeRule.onNodeWithContentDescription("Termékek").assertExists()
        composeRule.onNodeWithContentDescription("Termékek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Termékek").performClick()

        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.mainClock.autoAdvance = true

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()
        composeRule.onNodeWithTag("SearchBar").performClick()
        composeRule.onNodeWithTag("SearchBar").performTextInput("Kinder")

        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.mainClock.autoAdvance = true

        composeRule.onNodeWithText("Bounty").assertDoesNotExist()

        composeRule.onNodeWithText("Kinder mikulás").assertExists()
        composeRule.onNodeWithText("Kinder mikulás").assertIsDisplayed()

    }
}
package com.example.orderapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CustomersScreenTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickOnCustomersNavItem_navigateToCustomersList() {
        composeRule.onNodeWithContentDescription("Ügyfelek").assertExists()
        composeRule.onNodeWithContentDescription("Ügyfelek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Ügyfelek").performClick()

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()
    }

    @Test
    fun navigateToNewCustomerScreen(){
        composeRule.onNodeWithContentDescription("Ügyfelek").assertExists()
        composeRule.onNodeWithContentDescription("Ügyfelek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Ügyfelek").performClick()

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()

        composeRule.onNodeWithText("Hozzáadás").assertExists()
        composeRule.onNodeWithText("Hozzáadás").assertIsDisplayed()
        composeRule.onNodeWithText("Hozzáadás").performClick()

        composeRule.onNodeWithText("Ügyfél szerkesztése").assertExists()
        composeRule.onNodeWithText("Ügyfél szerkesztése").assertIsDisplayed()
    }

    @Test
    fun onOpenNewOrder_ThenNavigateBack(){
        composeRule.onNodeWithContentDescription("Ügyfelek").assertExists()
        composeRule.onNodeWithContentDescription("Ügyfelek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Ügyfelek").performClick()

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()

        composeRule.onNodeWithText("London").assertExists()
        composeRule.onNodeWithText("London").assertIsDisplayed()
        composeRule.onNodeWithText("London").performClick()

        composeRule.onNodeWithContentDescription("Back").assertExists()
        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
    }

    @Test
    fun onSearchForCustomer(){
        composeRule.onNodeWithContentDescription("Ügyfelek").assertExists()
        composeRule.onNodeWithContentDescription("Ügyfelek").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Ügyfelek").performClick()

        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.mainClock.autoAdvance = true

        composeRule.onNodeWithTag("SearchBar").assertExists()
        composeRule.onNodeWithTag("SearchBar").assertIsDisplayed()
        composeRule.onNodeWithTag("SearchBar").performClick()
        composeRule.onNodeWithTag("SearchBar").performTextInput("Emma")

        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(400L)
        composeRule.mainClock.autoAdvance = true

        composeRule.onNodeWithText("London").assertDoesNotExist()

        composeRule.onNodeWithText("Emma").assertExists()
        composeRule.onNodeWithText("Emma").assertIsDisplayed()

    }
}
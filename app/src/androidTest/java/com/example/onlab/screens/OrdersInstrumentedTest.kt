package com.example.onlab.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.onlab.MainActivity
import dagger.hilt.android.testing.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class OrdersScreenTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickOrdersNavItem_navigateToOrders() {
        composeRule.onNodeWithContentDescription("Rendelések").assertExists()
        composeRule.onNodeWithContentDescription("Rendelések").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Rendelések").performClick()

        composeRule.onNodeWithText("Függőben levő rendelések").assertExists()
        composeRule.onNodeWithText("Függőben levő rendelések").assertIsDisplayed()

        composeRule.onNodeWithText("Teljesített rendelések").assertExists()
        composeRule.onNodeWithText("Teljesített rendelések").assertIsDisplayed()
        composeRule.onNodeWithText("Teljesített rendelések").performClick()

        composeRule.onNodeWithText("Emma Smith").assertExists()
        composeRule.onNodeWithText("Emma Smith").assertIsDisplayed()
    }

    @Test
    fun clickOnOrderItem_navigateToOrderDetails(){
        composeRule.onNodeWithContentDescription("Rendelések").assertExists()
        composeRule.onNodeWithContentDescription("Rendelések").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Rendelések").performClick()

        composeRule.onNodeWithText("Károly Nagy").assertExists()
        composeRule.onNodeWithText("Károly Nagy").assertIsDisplayed()
        composeRule.onNodeWithText("Károly Nagy").performClick()

        composeRule.onNodeWithText("Rendelési tételek").assertExists()
    }

    @Test
    fun onSelectOrderAndAddNewOrderItem(){
        composeRule.onNodeWithContentDescription("Rendelések").assertExists()
        composeRule.onNodeWithContentDescription("Rendelések").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Rendelések").performClick()

        composeRule.onNodeWithText("Károly Nagy").assertExists()
        composeRule.onNodeWithText("Károly Nagy").assertIsDisplayed()
        composeRule.onNodeWithText("Károly Nagy").performClick()

        composeRule.onNodeWithContentDescription("Add button").assertExists()
        composeRule.onNodeWithContentDescription("Add button").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add button").performClick()

        composeRule.onNodeWithText("Italok").assertExists()
        composeRule.onNodeWithText("Italok").assertIsDisplayed()
        composeRule.onNodeWithText("Italok").performClick()

        composeRule.onNodeWithText("Topjoy kaktusz").assertExists()
        composeRule.onNodeWithText("Topjoy kaktusz").assertIsDisplayed()
        composeRule.onNodeWithText("Topjoy kaktusz").performClick()

        composeRule.onNodeWithText("Mennyiség").assertExists()
        composeRule.onNodeWithText("Mennyiség").assertIsDisplayed()
        composeRule.onNodeWithText("Mennyiség").performClick()
        composeRule.onNodeWithText("Mennyiség").performTextInput("5")

        composeRule.onNodeWithContentDescription("Save").performClick()

    }
}
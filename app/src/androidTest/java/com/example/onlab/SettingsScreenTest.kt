package com.example.onlab

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//Bejelentkezés után ad helyes eredményeket külön-külön futtatva
@HiltAndroidTest
class SettingsScreenTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createAndroidComposeRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickSettingsNavItem_navigateToSettings() {
        composeRule.onNodeWithContentDescription("Beállítások").assertExists()
        composeRule.onNodeWithContentDescription("Beállítások").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Beállítások").performClick()

        composeRule.onNodeWithText("Profil szerkesztése").assertExists()
        composeRule.onNodeWithText("Profil szerkesztése").assertIsDisplayed()
    }

    @Test
    fun editProfile(){
        composeRule.onNodeWithContentDescription("Beállítások").assertExists()
        composeRule.onNodeWithContentDescription("Beállítások").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Beállítások").performClick()

        composeRule.onNodeWithText("Profil szerkesztése").assertExists()
        composeRule.onNodeWithText("Profil szerkesztése").assertIsDisplayed()
        composeRule.onNodeWithText("Profil szerkesztése").performClick()
    }

    @Test
    fun dismissProfileDeletion(){
        composeRule.onNodeWithContentDescription("Beállítások").assertExists()
        composeRule.onNodeWithContentDescription("Beállítások").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Beállítások").performClick()

        composeRule.onNodeWithText("Profil törlése").assertExists()
        composeRule.onNodeWithText("Profil törlése").assertIsDisplayed()
        composeRule.onNodeWithText("Profil törlése").performClick()

        composeRule.onNodeWithText("Nem").assertExists()
        composeRule.onNodeWithText("Nem").assertIsDisplayed()
        composeRule.onNodeWithText("Nem").performClick()
    }

    @Test
    fun logOut(){

        composeRule.onNodeWithContentDescription("Beállítások").assertExists()
        composeRule.onNodeWithContentDescription("Beállítások").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Beállítások").performClick()

        composeRule.onNodeWithContentDescription("Log out").assertExists()
        composeRule.onNodeWithContentDescription("Log out").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Log out").performClick()

        composeRule.onNodeWithText("Elfelejtett jelszó").assertExists()
        composeRule.onNodeWithText("Elfelejtett jelszó").assertIsDisplayed()
        composeRule.onNodeWithText("Elfelejtett jelszó").performClick()
    }
}


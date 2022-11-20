package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun testInsertRetrieveData() = runBlockingTest {

        val data = ReminderDTO(
            "title test",
            "description test",
            "location test",
            122.00,
            122.00
        )

        database.reminderDao().saveReminder(data)

        val loadedDataList = database.reminderDao().getReminders()

        MatcherAssert.assertThat(loadedDataList.size, `is`(1))

        val loadedData = loadedDataList[0]
        MatcherAssert.assertThat(loadedData.id, `is`(data.id))
        MatcherAssert.assertThat(loadedData.title, `is`(data.title))
        MatcherAssert.assertThat(loadedData.description, `is`(data.description))
        MatcherAssert.assertThat(loadedData.location, `is`(data.location))
        MatcherAssert.assertThat(loadedData.latitude, `is`(data.latitude))
        MatcherAssert.assertThat(loadedData.longitude, `is`(data.longitude))

    }
}
package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {



    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        repository = RemindersLocalRepository(database.reminderDao())
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun testInsertRetrieveData() = runBlocking {

        val data = ReminderDTO(
            "title abc",
            "description abc",
            "location abc",
            122.00,
            122.00)

        repository.saveReminder(data)

        val result = repository.getReminder(data.id)

        result as Result.Success
        MatcherAssert.assertThat(result.data != null, CoreMatchers.`is`(true))

        val loadedData = result.data
        MatcherAssert.assertThat(loadedData.id, CoreMatchers.`is`(data.id))
        MatcherAssert.assertThat(loadedData.title, CoreMatchers.`is`(data.title))
        MatcherAssert.assertThat(loadedData.description, CoreMatchers.`is`(data.description))
        MatcherAssert.assertThat(loadedData.location, CoreMatchers.`is`(data.location))
        MatcherAssert.assertThat(loadedData.latitude, CoreMatchers.`is`(data.latitude))
        MatcherAssert.assertThat(loadedData.longitude, CoreMatchers.`is`(data.longitude))
    }


    @Test
    fun testDataNotFound_returnError() = runBlocking {
        val data = ReminderDTO(
            "title abc",
            "description abc",
            "location abc",
            122.00,
            122.00)
        repository.saveReminder(data)
        repository.deleteAllReminders()


        val result = repository.getReminder(data.id)
        MatcherAssert.assertThat(result is Result.Error, CoreMatchers.`is`(true))
        result as Result.Error
        MatcherAssert.assertThat(result.message, CoreMatchers.`is`("Reminder not found!"))
    }
}
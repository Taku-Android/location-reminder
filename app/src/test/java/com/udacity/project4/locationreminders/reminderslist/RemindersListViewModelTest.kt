package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.rule.MainCoroutineRule

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = intArrayOf(Build.VERSION_CODES.P))
class RemindersListViewModelTest: AutoCloseKoinTest(){

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderViewModel: RemindersListViewModel

    private lateinit var reminderRepo: FakeDataSource


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupReminderViewModel(){
        reminderRepo = FakeDataSource()

        reminderViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),reminderRepo)
    }

    @Test
    fun saveReminderdata() = runBlockingTest{
        return@runBlockingTest reminderRepo.saveReminder(
            ReminderDTO(
                "title test ",
                "description test ",
                "location test",
                77.0,
                77.0
            )
        )
    }

    @Test
    fun test_shouldReturnError () = runBlockingTest  {
        reminderRepo.setReturnError(true)
        saveReminderdata()
        reminderViewModel.loadReminders()

        assertThat(
            reminderViewModel.showSnackBar.value, `is`("reminders not found")
        )
    }

    @Test
    fun loadReminder_displayContent() = runBlockingTest{
        mainCoroutineRule.pauseDispatcher()
        saveReminderdata()
        reminderViewModel.loadReminders()

        assertThat(reminderViewModel.showLoading.value, `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(reminderViewModel.showLoading.value, `is`(false))
    }
}

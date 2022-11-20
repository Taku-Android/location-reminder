package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.rule.MainCoroutineRule

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.koin.test.AutoCloseKoinTest




@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest: AutoCloseKoinTest()  {


    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
    }

    @Test
    fun shouldReturnError () = runBlockingTest  {
        val result = saveReminderViewModel.validateEnteredData(createIncompleteReminderDataItem())
        MatcherAssert.assertThat(result, CoreMatchers.`is`(false))
    }

    private fun createIncompleteReminderDataItem(): ReminderDataItem {
        return ReminderDataItem(
            "",
            "description abc",
            "location abc",
            77.00,
            77.00)
    }

    @Test
    fun check_loading() = runBlockingTest {

        mainRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(createFakeReminderData())

        MatcherAssert.assertThat(saveReminderViewModel.showLoading.value, CoreMatchers.`is`(true))

        mainRule.resumeDispatcher()
        MatcherAssert.assertThat(saveReminderViewModel.showLoading.value, CoreMatchers.`is`(false))
    }

    private fun createFakeReminderData(): ReminderDataItem {
        return ReminderDataItem(
            "title abc",
            "description abc",
            "location abc",
            77.00,
            77.00)
    }
}
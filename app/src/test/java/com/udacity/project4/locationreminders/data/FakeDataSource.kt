package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.withContext

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminderlist: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {



    private var returnError = false

    fun setReturnError(value: Boolean) {
        returnError = value
    }



    override suspend fun getReminders(): Result<List<ReminderDTO>>{
        return try {
            if(returnError) {
                throw Exception("reminders not found")
            }
            Result.Success(ArrayList(reminderlist))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderlist.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO>  {
        return try {
            val reminder = reminderlist.find { it.id == id }

            if (returnError || reminder == null) {
                throw Exception("Not found $id")
            } else {
                Result.Success(reminder)
            }
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        reminderlist.clear()
    }

}
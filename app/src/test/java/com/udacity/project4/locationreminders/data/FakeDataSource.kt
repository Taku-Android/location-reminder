package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.withContext

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {
    var reminderDTOList = mutableListOf<ReminderDTO>()

    private var shouldReturnError = false

    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        if (shouldReturnError) {
            return Result.Error("Error")
        } else {

           return Result.Success(ArrayList(reminderDTOList))
        }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDTOList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {



        if (shouldReturnError ) {
            return Result.Error("Error")
        }else {
            val reminder = reminderDTOList.find { it.id == id }

            if (reminder != null) {
              return  Result.Success(reminder)
            } else {
               return Result.Error("Reminder not found", 404)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminderDTOList.clear()
    }
}
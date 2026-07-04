package com.example.data.repository

import com.example.data.database.ActivityDao
import com.example.data.database.ActivityItem
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    val allActivities: Flow<List<ActivityItem>> = activityDao.getAllActivities()

    suspend fun insertActivity(activity: ActivityItem) {
        activityDao.insertActivity(activity)
    }

    suspend fun deleteActivityById(id: Int) {
        activityDao.deleteActivityById(id)
    }

    suspend fun clearAll() {
        activityDao.clearAllActivities()
    }
}

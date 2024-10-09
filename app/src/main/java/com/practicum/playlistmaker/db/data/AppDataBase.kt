package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.db.data.dao.TrackDao
import com.practicum.playlistmaker.db.data.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDataBase(): RoomDatabase(){
    abstract fun trackDao(): TrackDao
}
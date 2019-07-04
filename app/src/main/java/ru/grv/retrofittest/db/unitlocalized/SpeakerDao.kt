package ru.grv.retrofittest.db.unitlocalized

import android.arch.persistence.room.*
import ru.grv.retrofittest.Speaker

@Dao
interface SpeakerDao {
    @Query("SELECT * FROM speakers")
    fun getAllSpeaker(): List<Speaker>?

    @Query("SELECT * FROM speakers WHERE id = :idSpeaker")
    fun getSpeaker(idSpeaker: String?): Speaker

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSpeaker(speaker: Speaker)

    /*@Delete
    fun deleteSpeaker(speaker: Speaker)

    @Query("DELETE FROM speakers")
    fun deleteAllSpeaker()*/
}


package ru.grv.retrofittest.db.unitlocalized

import android.arch.persistence.room.*
import ru.grv.retrofittest.Speaker
import ru.grv.retrofittest.Talk

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity")
    fun getAllActivity(): List<Talk>?

    @Query("SELECT * FROM activity WHERE speaker = :idSpeaker")
    fun getActivity(idSpeaker: String?): Talk

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActivity(activity: Talk)

    /*@Delete
    fun deleteActivity(activity: Talk)*/

    /*@Query("DELETE FROM activity")
    fun deleteAllActivity()*/
}
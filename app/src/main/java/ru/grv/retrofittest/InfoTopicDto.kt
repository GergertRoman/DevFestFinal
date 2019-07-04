package ru.grv.retrofittest

import android.arch.persistence.room.*

data class InfoTopicDto(val speakers:List<Speaker>, val schedule: Schedule)

@Entity(tableName = "speakers")
data class Speaker(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") var id: String = "",
    @ColumnInfo(name = "first_name") var firstName: String = "",
    @ColumnInfo(name = "last_name") var lastName: String = "",
    @ColumnInfo(name = "location") var location: String = "",
    @ColumnInfo(name = "job_title") var jobTitle: String = "",
    @ColumnInfo(name = "job") var job: String? = null,
    @ColumnInfo(name = "company") var company: String? = null ,
    @ColumnInfo(name = "about") var about: String = "",
    @ColumnInfo(name = "photo") var photo: String = "",
    @ColumnInfo(name = "flag_image") var flagImage: String = "",
    @Ignore @Embedded(prefix = "links_") var links: Link = Link(),
    @ColumnInfo (name = "twitter") var twitter: String? = null,
    @ColumnInfo (name = "github") var github: String? = null,
    @ColumnInfo (name = "telegram") var telegram: String? = null
)

data class Link(
    val twitter: String? = null,
    val github: String? = null,
    val telegram: String? = null
)

data class Schedule(var talks: List<Talk>, var activities: List<Activity>)

interface Timeable {
    val rawTime: String
}

@Entity(tableName = "activity")
data class Talk(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "room") var room: String = "",
    @ColumnInfo(name = "track") var track: String = "",
    @ColumnInfo(name = "speaker") var speaker: String = "",
    @ColumnInfo(name = "time") var time: String = ""
) : Timeable {
    override val rawTime: String get() = time
}

data class Activity(var title: String  = "",
                    var time: String = ""
) : Timeable {
    override val rawTime: String get() = time
}



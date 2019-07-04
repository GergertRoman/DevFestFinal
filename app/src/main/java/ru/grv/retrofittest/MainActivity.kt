package ru.grv.retrofittest

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Response
import retrofit2.Call
import ru.grv.retrofittest.db.DevFestDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.facebook.stetho.Stetho
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.widget.SwipeRefreshLayout

open class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var mDb: DevFestDatabase? = null
    private var mAdapter: ScheduleAdapter? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private val mUiHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeContainer?.setOnRefreshListener(this)
        swipeContainer?.setColorSchemeResources(R.color.colorPrimary)

        initializerStetho()

        mAdapter = ScheduleAdapter(emptyList(), emptyList())

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()

        toolbar?.setTitle(R.string.app_name)
        initRecyclerView()

        mDb = DevFestDatabase.invoke(this)

        getData()
    }

    fun getData() {
        if (isNetworkAvailable(rvSchedule.context)) {
            swipeContainer?.isRefreshing = true
            response()
        } else {
            swipeContainer?.isRefreshing = false
            fetchDataFromDb()
        }
    }

    override fun onRefresh() {
        getData()
    }

    private fun fetchDataFromDb() {
        val task = Runnable {
            val speakerData =
                mDb?.speakerDao()?.getAllSpeaker()
            val activityData =
                mDb?.activityDao()?.getAllActivity()
            mUiHandler.post {
                mAdapter?.update(activityData, speakerData)
            }
        }
        mDbWorkerThread.postTask(task)
    }

    private  fun response() {
        val foursquareService: FoursquareService = retrofit.create(FoursquareService::class.java)
        val call = foursquareService.getVenues()

        call.enqueue(object : retrofit2.Callback<InfoTopicDto> {

            override fun onResponse(call: Call<InfoTopicDto>, response: Response<InfoTopicDto>) {
                if (response.isSuccessful) {
                    mDbWorkerThread.postTask(Runnable {
                        // request successful (status code 200, 201)
                        val result: InfoTopicDto? = response.body()
                        val talks = result?.schedule?.talks?.orEmpty()
                        val activities = result?.schedule?.activities?.orEmpty()
                        val speakers = result?.speakers
                        val talksActivities = getResultList(talks, activities)
                        var speaker = Speaker()
                        var activity = Talk()
                        if (speakers != null) {
                            for (i in 0 until speakers.size) {
                                val speak = speakers[i]
                                speaker.id = speak.id
                                speaker.about = speak.about
                                speaker.company = speak.company
                                speaker.firstName = speak.firstName
                                speaker.lastName = speak.lastName
                                speaker.flagImage = speak.flagImage
                                speaker.job = speak.job
                                speaker.jobTitle = speak.jobTitle
                                speaker.location = speak.location
                                speaker.photo = speak.photo
                                speaker.github = speak.links.github
                                speaker.telegram = speak.links.telegram
                                speaker.twitter = speak.links.twitter

                                mDb?.speakerDao()?.insertSpeaker(speaker)
                            }
                        }

                        if (talksActivities != null) {
                            for (i in 0 until talksActivities.size) {
                                val activ = talksActivities[i]
                                if (activ is Talk) {
                                    activity.description = activ.description
                                    activity.room = activ.room
                                    activity.speaker = activ.speaker
                                    activity.time = activ.time
                                    activity.title = activ.title
                                    activity.track = activ.track
                                }

                                if (activ is Activity) {
                                    activity.description = ""
                                    activity.room = ""
                                    activity.speaker = ""
                                    activity.time = activ.time
                                    activity.title = activ.title
                                    activity.track = ""
                                }

                                mDb?.activityDao()?.insertActivity(activity)
                            }
                            val speakerData =
                                mDb?.speakerDao()?.getAllSpeaker()
                            val activityData =
                                mDb?.activityDao()?.getAllActivity()
                            runOnUiThread {
                                mAdapter?.update(activityData, speakerData)
                                swipeContainer?.isRefreshing = false
                            }
                        }
                    })

                } else {
                    swipeContainer?.isRefreshing = false
                    //request not successful (like 400,401,403 etc)
                    //Handle errors
                }
            }

            override fun onFailure(call: Call<InfoTopicDto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun getResultList(talks: List<Talk>?, activities: List<Activity>?): List<Timeable> {
        val resultTalks = talks?.map { it as Timeable }
        val resultActivities = activities?.map { it as Timeable }?.toMutableList()
        resultActivities?.addAll(resultTalks!!)
        val dateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val adapterTalkActivitiy = resultActivities?.sortedBy { dateFormatter.parse(it.rawTime).time }

        return (adapterTalkActivitiy!!).toList()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(rvSchedule?.context, layoutManager.orientation)
        rvSchedule?.addItemDecoration(dividerItemDecoration)
        rvSchedule?.layoutManager = layoutManager
        rvSchedule?.itemAnimator = DefaultItemAnimator()
        rvSchedule?.adapter = mAdapter
    }

    fun initializerStetho() {
        // Create an InitializerBuilder
        val initializerBuilder = Stetho.newInitializerBuilder(this)

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
            Stetho.defaultInspectorModulesProvider(this)
        )

        // Enable command line interface
        initializerBuilder.enableDumpapp(
            Stetho.defaultDumperPluginsProvider(this)
        )

        // Use the InitializerBuilder to generate an Initializer
        val initializer = initializerBuilder.build()

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    override fun onDestroy() {
        mDb?.destroyInstance()
        mDbWorkerThread.quit()
        super.onDestroy()
    }
}

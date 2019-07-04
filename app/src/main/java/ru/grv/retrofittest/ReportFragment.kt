package ru.grv.retrofittest

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_report.*
import ru.grv.retrofittest.db.DevFestDatabase

class ReportFragment: Fragment() {
    var speakerSelect: String? = null
    private var mDb: DevFestDatabase? = null
    private lateinit var mDbWorkerThread: DbWorkerThread
    private val mUiHandler = Handler()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        speakerSelect = arguments?.getString(ITEM_KEY)

        mDbWorkerThread = DbWorkerThread("dbWorkerThread")
        mDbWorkerThread.start()

        mDb = DevFestDatabase.invoke(context!!.applicationContext)

        byContentFromDb(speakerSelect)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    private fun byContentFromDb(speakerId: String?) {
        val task = Runnable {
            val speaker =
                mDb?.speakerDao()?.getSpeaker(speakerId)
            val activity =
                mDb?.activityDao()?.getActivity(speakerId)
            mUiHandler.post {
                tvTime?.text = activity?.time

                tvTheme?.text = activity?.title

                tvRoom?.text = "Room " + activity?.room

                when(activity?.track) {
                    "android" -> ivContent?.setImageResource(R.drawable.android_head)
                    "frontend" -> ivContent?.setImageResource(R.drawable.ic_cellphone_ui)
                    "common" -> ivContent?.setImageResource(R.drawable.ic_developer_board)
                }

                when(speaker?.flagImage) {
                    "ru" -> ivLanguage?.setImageResource(R.drawable.ic_rus)
                    "de" -> ivLanguage?.setImageResource(R.drawable.ic_germany)
                    "us" -> ivLanguage?.setImageResource(R.drawable.ic_usa)
                    "gb" -> ivLanguage?.setImageResource(R.drawable.ic_uk)
                    "ua" -> ivLanguage?.setImageResource(R.drawable.ic_ua)
                }

                tvSpeaker?.text = speaker?.firstName + " " + speaker?.lastName

                speaker?.jobTitle?.let {
                    tvPosition?.text = it + " " + speaker?.company + " " + speaker?.location
                }

                speaker?.job?.let {
                    tvPosition?.text = it + " " + speaker?.company + " " + speaker?.location
                }

                tvDescription?.text = activity?.description
            }
        }
        mDbWorkerThread.postTask(task)
    }
}
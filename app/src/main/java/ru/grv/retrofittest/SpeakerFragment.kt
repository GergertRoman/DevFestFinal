package ru.grv.retrofittest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_report.ivContent
import kotlinx.android.synthetic.main.fragment_report.ivLanguage
import kotlinx.android.synthetic.main.fragment_report.tvPosition
import kotlinx.android.synthetic.main.fragment_report.tvRoom
import kotlinx.android.synthetic.main.fragment_report.tvSpeaker
import kotlinx.android.synthetic.main.fragment_report.tvTheme
import kotlinx.android.synthetic.main.fragment_report.tvTime
import kotlinx.android.synthetic.main.fragment_speaker.*
import ru.grv.retrofittest.db.DevFestDatabase

class SpeakerFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_speaker, container, false)
    }

    private fun byContentFromDb(speakerId: String?) {
        val task = Runnable {
            val speaker =
                mDb?.speakerDao()?.getSpeaker(speakerId)
            val activity =
                mDb?.activityDao()?.getActivity(speakerId)
            mUiHandler.post {
                Picasso.get().load(speaker?.photo).into(civAvatar)

                when (speaker?.flagImage) {
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

                tvInfoSpeaker?.text = speaker?.about

                tvTheme?.text = activity?.title

                tvRoom?.text = "Room " + activity?.room

                when (activity?.track) {
                    "android" -> ivContent?.setImageResource(R.drawable.android_head)
                    "frontend" -> ivContent?.setImageResource(R.drawable.ic_cellphone_ui)
                    "common" -> ivContent?.setImageResource(R.drawable.ic_developer_board)
                }

                tvTime?.text = activity?.time

                if (speaker?.telegram != "" && speaker?.telegram != null) {
                    ivTelegram?.apply {
                        setImageResource(R.drawable.ic_telegram)
                        setOnClickListener {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(speaker.telegram)))
                        }
                    }
                    if (speaker.twitter != "" && speaker.twitter != null) {
                        ivTwitter?.apply {
                            setImageResource(R.drawable.ic_twitter)
                            setOnClickListener {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(speaker.twitter)))
                            }
                        }

                    } else
                        if (speaker.github != "" && speaker.github != null) {
                            ivTwitter?.apply {
                                setImageResource(R.drawable.ic_link_variant)
                                setOnClickListener {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(speaker.github)))
                                }
                            }
                        }
                } else
                    if (speaker?.twitter != "" && speaker?.twitter != null) {
                        ivTelegram?.apply {
                            setImageResource(R.drawable.ic_twitter)
                            setOnClickListener {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(speaker.twitter)))
                            }
                        }
                        if (speaker.github != "" && speaker.github != null) {
                            ivTwitter?.apply {
                                setImageResource(R.drawable.ic_link_variant)
                                setOnClickListener {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(speaker.github)))
                                }
                            }
                        }
                    } else
                        if (speaker?.github != "" && speaker?.github != null) {
                            ivTelegram?.apply {
                                setImageResource(R.drawable.ic_link_variant)
                                setOnClickListener {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(speaker?.github)))
                                }
                            }
                        }
            }
        }
        mDbWorkerThread.postTask(task)
    }
}
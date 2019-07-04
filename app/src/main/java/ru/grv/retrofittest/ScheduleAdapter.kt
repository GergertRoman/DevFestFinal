package ru.grv.retrofittest

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide.init
import kotlinx.android.synthetic.main.topic_item.view.*
import ru.grv.retrofittest.db.DevFestDatabase

class ScheduleAdapter(private var talks: List<Talk>,
                      private var spekers: List<Speaker>
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.topic_item, parent, false)


        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return talks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val talks = talks[position]
        var speaker = Speaker()

        if (talks.speaker == "") {
            holder.track?.visibility = View.INVISIBLE
            holder.flagImage?.visibility = View.INVISIBLE
            holder.room?.visibility = View.INVISIBLE
            holder.speaker?.visibility = View.INVISIBLE
            holder.position?.visibility = View.INVISIBLE
            holder.itemView?.isClickable = false
            talks.time?.let { holder.time?.setText(it) }
            talks.title?.let { holder.title?.setText(it) }
        } else {
            holder.track?.visibility = View.VISIBLE
            holder.flagImage?.visibility = View.VISIBLE
            holder.room?.visibility = View.VISIBLE
            holder.speaker?.visibility = View.VISIBLE
            holder.position?.visibility = View.VISIBLE
            holder.itemView?.isClickable = true
            talks.time?.let { holder.time?.setText(it) }
            talks.title?.let { holder.title?.setText(it) }
            talks.room?.let { holder.room?.setText("Room $it") }

            when (talks.track) {
                "android" -> holder.track?.setImageResource(R.drawable.android_head)
                "frontend" -> holder.track?.setImageResource(R.drawable.ic_cellphone_ui)
                "common" -> holder.track?.setImageResource(R.drawable.ic_developer_board)
            }

            for (i in 0 until spekers.size) {
                val speakers = spekers[i]
                if (talks.speaker == speakers.id) {
                    speaker.id = speakers.id
                    speakers.firstName?.let { holder.speaker?.setText(it + " " + speakers.lastName) }
                    speakers.company?.let { holder.position?.setText(it) }
                    when (speakers.flagImage) {
                        //Picasso.get().load(speakers.photo).into(holder.flagImage)
                        "ru" -> holder.flagImage?.setImageResource(R.drawable.ic_rus)
                        "de" -> holder.flagImage?.setImageResource(R.drawable.ic_germany)
                        "us" -> holder.flagImage?.setImageResource(R.drawable.ic_usa)
                        "gb" -> holder.flagImage?.setImageResource(R.drawable.ic_uk)
                        "ua" -> holder.flagImage?.setImageResource(R.drawable.ic_ua)
                    }
                }
            }

            holder.itemView?.setOnClickListener {
                val ctx = holder.itemView.context
                val itemIntent = Intent(ctx, TabActivity::class.java)
                itemIntent.putExtra(ITEM_KEY, talks.speaker)
                ctx.startActivity(itemIntent)
            }
        }
    }

    fun update(talks: List<Talk>?, speakers: List<Speaker>?){
        talks?.let { this.talks = talks }
        speakers?.let { this.spekers = speakers }
        notifyDataSetChanged()
    }

    class ViewHolder(row: View): RecyclerView.ViewHolder(row) {
        var time: TextView? = null
        var title: TextView? = null
        var room: TextView? = null
        var track: ImageView? = null
        var speaker: TextView? = null
        var position: TextView? = null
        var flagImage: ImageView? = null

        init {
            this.time = row?.tvTime
            this.title = row?.tvTheme
            this.room = row?.tvRoom
            this.track = row?.ivContent
            this.speaker = row?.tvSpeaker
            this.position = row?.tvPosition
            this.flagImage = row?.ivLanguage
        }

    }

}
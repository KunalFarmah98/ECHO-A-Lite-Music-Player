package com.apps.kunalfarmah.echo.adapter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps.kunalfarmah.echo.R
import com.apps.kunalfarmah.echo.activity.MainActivity
import com.apps.kunalfarmah.echo.activity.SongPlayingActivity
import com.apps.kunalfarmah.echo.adapter.MainScreenAdapter.Statified.stopPlayingCalled
import com.apps.kunalfarmah.echo.databinding.RowCustomMainscreenAdapterBinding
import com.apps.kunalfarmah.echo.model.Songs
import com.apps.kunalfarmah.echo.util.AppUtil
import com.apps.kunalfarmah.echo.util.Constants
import com.apps.kunalfarmah.echo.util.MediaUtils
import com.apps.kunalfarmah.echo.util.MediaUtils.mediaPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlin.math.max


/*This adapter class also serves the same function to act as a bridge between the single row view and its data. The implementation is quite similar to the one we did
* for the navigation drawer adapter*/
class AlbumTracksAdapter(_songDetails: ArrayList<Songs>, _context: Context, val albumId: Long?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_FOOTER = 1
    }

    /*Local variables used for storing the data sent from the fragment to be used in the adapter
    * These variables are initially null*/
    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null
    lateinit var sharedPreferences: SharedPreferences

    object Statified{
        var stopPlayingCalled = false
    }

    /*In the init block we assign the data received from the params to our local variables*/
    init {
        this.songDetails = _songDetails
        this.mContext = _context
        sharedPreferences = AppUtil.getAppPreferences(mContext)
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val songObject = songDetails?.get(position)
            if (MediaUtils.isAlbumPlaying && position == MediaUtils.currInd && albumId == MediaUtils.currAlbum) {
                try {
                    holder.binding?.cardContent?.setBackgroundColor(mContext?.resources?.getColor(R.color.white_overlay_15_percent)!!)
                } catch (_: Exception) {
                    holder.binding?.contentRow?.strokeWidth = 2
                    holder.binding?.contentRow?.strokeColor =
                        mContext?.resources?.getColor(R.color.colorAccent)!!
                }
            } else {
                try {
                    holder.binding?.cardContent?.setBackgroundColor(mContext?.resources?.getColor(R.color.colorPrimary)!!)
                } catch (_: Exception) {
                    holder.binding?.contentRow?.strokeWidth = 0
                    holder.binding?.contentRow?.strokeColor =
                        mContext?.resources?.getColor(R.color.colorPrimary)!!
                }
            }

            /*The holder object of our MyViewHolder class has two properties i.e
        * trackTitle for holding the name of the song and
        * trackArtist for holding the name of the artist*/

            holder.binding?.trackTitle?.text = songObject?.songTitle
            holder.binding?.trackArtist?.text = songObject?.artist
            holder.binding?.trackAlbum?.text = songObject?.album

            if (holder.binding?.trackTitle?.text?.equals("<unknown>") == true)
                holder.binding?.trackTitle?.text = "unknown"

            if (holder.binding?.trackArtist?.text?.equals("<unknown>") == true)
                holder.binding?.trackArtist?.visibility = View.GONE

            if (holder.binding?.trackAlbum?.text?.equals("<unknown>") == true)
                holder.binding?.trackAlbum?.text = "Unknown Album"

            var albumId = songObject?.songAlbum as Long
            //var art: Bitmap? =null

            if (albumId <= 0L) holder.binding!!.album!!.setImageDrawable(mContext!!.resources.getDrawable(R.drawable.now_playing_bar_eq_image))
            val sArtworkUri: Uri = Uri
                .parse("content://media/external/audio/albumart")
            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, albumId)
            mContext?.let {
                holder.binding?.album?.let { it1 ->
                    Glide.with(it).load(uri).placeholder(R.drawable.now_playing_bar_eq_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(it1)
                }
            }




            /*Handling the click event i.e. the action which happens when we click on any song*/
            holder.binding?.contentRow?.setOnClickListener {
                val intent = Intent(mContext, SongPlayingActivity::class.java)
                notifyItemChanged(max(MediaUtils.getSongIndex(), 0))
                MediaUtils.currSong = songObject
                MediaUtils.isAlbumPlaying = true
                MediaUtils.currAlbum = albumId
                MediaUtils.isAllSongsPLaying = false
                MediaUtils.isFavouritesPlaying = false

                intent.putExtra("songArtist", songObject.artist)
                intent.putExtra("songTitle", songObject.songTitle)
                intent.putExtra("path", songObject.songData)
                intent.putExtra("SongID", songObject.songID)
                intent.putExtra("songAlbum", songObject.songAlbum ?: -1)
                intent.putExtra("album", songObject.album)
                intent.putExtra("songPosition", position)
                intent.`package` = mContext?.packageName
                MediaUtils.songsList = songDetails ?: ArrayList()
                MediaUtils.setMediaItems()

                stopPlaying(intent)

                try {
                    holder.binding?.cardContent?.setBackgroundColor(mContext?.resources?.getColor(R.color.white_overlay_15_percent)!!)
                } catch (_: Exception) {
                    holder.binding?.contentRow?.strokeWidth = 2
                    holder.binding?.contentRow?.strokeColor =
                        mContext?.resources?.getColor(R.color.colorAccent)!!
                }


                (mContext as MainActivity).startActivity(intent)
            }
        } else if (holder is FooterViewHolder) {
            val totalDurationMs = songDetails?.sumOf { MediaUtils.songDurations[it.songID] ?: 0L } ?: 0L
            val totalDuration = MediaUtils.getDuration(totalDurationMs)
            holder.totalSongs.text = mContext?.getString(R.string.total_songs_info, songDetails?.size ?: 0, totalDuration)
        }
    }

    /*This has the same implementation which we did for the navigation drawer adapter*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_FOOTER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer, parent, false)
            return FooterViewHolder(view)
        }
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == songDetails?.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {

        /*If the array list for the songs is null i.e. there are no songs in your device
        * then we return 0 and no songs are displayed*/
        if (songDetails == null || songDetails?.size == 0) {
            return 0
        }

        /*Else we return the total size of the song details which will be the total number of song details*/
        else {
            return (songDetails as ArrayList<Songs>).size + 1
        }
    }

    /*Every view holder class we create will serve the same purpose as it did when we created it for the navigation drawer*/
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        /*Constructor initialisation for the variables*/
        var binding:RowCustomMainscreenAdapterBinding?=null
        init {
            binding = RowCustomMainscreenAdapterBinding.bind(view)
        }
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val totalSongs: android.widget.TextView = view.findViewById(R.id.totalSongs)
    }


    private fun stopPlaying(intent: Intent) {
        try {
            if (mediaPlayer != null && MediaUtils.isMediaPlayerPlaying()) {
                mediaPlayer.stop()
                intent.putExtra(Constants.WAS_MEDIA_PLAYING,true)
            }
            stopPlayingCalled = true
        }catch (e:Exception){}
    }

}
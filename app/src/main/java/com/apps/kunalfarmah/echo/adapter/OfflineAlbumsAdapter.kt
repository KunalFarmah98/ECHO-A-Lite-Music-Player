package com.apps.kunalfarmah.echo.adapter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apps.kunalfarmah.echo.R
import com.apps.kunalfarmah.echo.activity.MainActivity
import com.apps.kunalfarmah.echo.databinding.GridItemBinding
import com.apps.kunalfarmah.echo.fragment.AlbumTracksFragment
import com.apps.kunalfarmah.echo.fragment.OfflineAlbumsFragment
import com.apps.kunalfarmah.echo.model.SongAlbum
import com.apps.kunalfarmah.echo.util.MediaUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class OfflineAlbumsAdapter(context: Context, list: List<SongAlbum>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mContext = context
    var albums = list

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_FOOTER = 1

    class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: GridItemBinding? = null

        init {
            binding = GridItemBinding.bind(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(album: SongAlbum) {
            var name = album._name

            if(name.equals("<unknown>",true)){
                name = "Unknown Album"
            }
            binding?.name?.text = name
            if (album._id <= 0L) binding!!.image!!.setImageDrawable(itemView.context!!.resources.getDrawable(R.drawable.now_playing_bar_eq_image))
            val sArtworkUri: Uri = Uri
                    .parse("content://media/external/audio/albumart")
            val uri: Uri = ContentUris.withAppendedId(sArtworkUri, album._id)
            itemView.context?.let { binding?.image?.let { it1 -> Glide.with(it).load(uri).placeholder(R.drawable.now_playing_bar_eq_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(it1) } }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_FOOTER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_footer, parent, false)
            return FooterViewHolder(view)
        }
        return AlbumsViewHolder(GridItemBinding.inflate(LayoutInflater.from(mContext), parent, false).root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlbumsViewHolder) {
            val album = albums[position]
            holder.bind(album)
            holder.binding?.root?.setOnClickListener {
                OfflineAlbumsFragment.postion = position
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.details_fragment,
                        AlbumTracksFragment(album._id, album._name),
                        AlbumTracksFragment.TAG
                    )
                    .addToBackStack(AlbumTracksFragment.TAG)
                    .commit()
            }
        } else if (holder is FooterViewHolder) {
            val totalSongs = MediaUtils.allSongsList.size
            val totalDurationMs = MediaUtils.allSongsList.sumOf { MediaUtils.songDurations[it.songID] ?: 0L }
            val totalDuration = MediaUtils.getDuration(totalDurationMs)
            holder.totalAlbums.text = mContext.getString(R.string.total_albums_info, albums.size, totalSongs, totalDuration)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == albums.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return albums.size + 1
    }

    class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val totalAlbums: android.widget.TextView = view.findViewById(R.id.totalSongs)
    }
}
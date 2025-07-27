package com.apps.kunalfarmah.echo.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.apps.kunalfarmah.echo.R
import com.apps.kunalfarmah.echo.databinding.ActivitySongPlayingBinding
import com.apps.kunalfarmah.echo.fragment.SongPlayingFragment
import com.apps.kunalfarmah.echo.util.BottomBarUtils

class SongPlayingActivity : AppCompatActivity() {
    lateinit var binding : ActivitySongPlayingBinding
    companion object{
        var instance: SongPlayingActivity?=null
        var songPlayingFragment = SongPlayingFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this@SongPlayingActivity
        binding = ActivitySongPlayingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_queue_music_white_24dp)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            val statusBarHeight = systemBars.top // Height of the status bar
            // Set the top margin of the container
            // The container needs to be below both the status bar and the ActionBar
            binding.container.post {
                val currentActionBarHeight = supportActionBar?.height ?: 0
                binding.container.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = statusBarHeight + currentActionBarHeight
                }
            }
            WindowInsetsCompat.CONSUMED
        }

        val args = intent.extras
        songPlayingFragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container, songPlayingFragment)
            .commit()
    }

    override fun onBackPressed() {
        instance = null
        BottomBarUtils.bottomBarBinding?.root?.visibility = View.VISIBLE
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
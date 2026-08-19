package com.apps.kunalfarmah.echo.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_queue_music_white_24dp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
                val systemBars =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                binding.appbarlayout.setPadding(0, systemBars.top, 0, 0)
                view.setPadding(
                    systemBars.left,
                    0,
                    systemBars.right,
                    systemBars.bottom
                )
                windowInsets
            }
            // preserving statusbar theme
            val windowInsetsController =
                ViewCompat.getWindowInsetsController(window.decorView)
            windowInsetsController?.isAppearanceLightStatusBars = false
            windowInsetsController?.isAppearanceLightNavigationBars = false
        }

        val args = intent.extras
        songPlayingFragment.arguments = args
        supportFragmentManager.beginTransaction().replace(R.id.container, songPlayingFragment)
            .commit()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                instance = null
                BottomBarUtils.bottomBarBinding?.root?.visibility = View.VISIBLE
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
package com.dede.android_eggs.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dede.android_eggs.settings.EdgePref

class SnapshotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EdgePref.applyEdge(this, window)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SnapshotFragment())
            .commit()
    }
}
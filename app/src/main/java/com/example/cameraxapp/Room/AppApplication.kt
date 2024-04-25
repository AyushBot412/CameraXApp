package com.example.cameraxapp.Room

import android.app.Application

class AppApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this)}
}
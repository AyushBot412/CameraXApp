package com.example.cameraxapp.Room

import android.app.Application

//The following class helps us to get an instance of the database to interact with it
class AppApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this)}
}
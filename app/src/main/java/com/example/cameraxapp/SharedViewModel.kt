package com.example.cameraxapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _expDate = MutableLiveData<String>()
    val expDate: LiveData<String> get() = _expDate

    fun setExpDate(date: String) {
        _expDate.value = date
    }
}
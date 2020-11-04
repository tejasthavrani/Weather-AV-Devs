package com.example.admin.kotlinvideorecyclerview.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.admin.kotlinvideorecyclerview.model.DateTemperatureData

class DateTemperatureViewModel : ViewModel {
    var current_time = ""
    var current_temperature = ""
    constructor() : super()
    constructor(category: DateTemperatureData) : super() {
        this.current_time = category.current_time
        this.current_temperature = category.current_temperature
    }


    var arraylistmutablelivedata = MutableLiveData<ArrayList<DateTemperatureViewModel>>()

    var arraylist = ArrayList<DateTemperatureViewModel>()

    /** For get data from list*/
    fun getArrayList(): MutableLiveData<ArrayList<DateTemperatureViewModel>> {

        return arraylistmutablelivedata
    }

    /** For add data to list*/
    fun addToArrayList(str_current_time: String, str_current_temperature: String) {

        val category1 = DateTemperatureData(str_current_time, str_current_temperature)

        val categoryviewmodel1: DateTemperatureViewModel = DateTemperatureViewModel(category1)


        arraylist!!.add(categoryviewmodel1)

        arraylistmutablelivedata.value = arraylist

    }

    /** For remove all data to list*/
    fun removeArrayList() {
        arraylist.clear()
        arraylistmutablelivedata.value = arraylist

    }
}










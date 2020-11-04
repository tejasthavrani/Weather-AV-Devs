package com.example.admin.kotlinvideorecyclerview.model

class DateTemperatureData {
    var current_time = ""
    var current_temperature = ""

    constructor(current_time: String, current_temperature: String) {
        this.current_time = current_time
        this.current_temperature = current_temperature
    }
}
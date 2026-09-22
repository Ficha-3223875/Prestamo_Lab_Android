package com.example.prestamolab

import android.app.Application
import com.example.prestamolab.di.AppContainer

class PrestamoLabApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
package org.ifit.sucks

import android.app.Application
import org.slf4j.LoggerFactory

class MainApplication : Application() {

    private val logger = LoggerFactory.getLogger(MainApplication::class.java)

    override fun onCreate() {
        super.onCreate()

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            logger.error(e.message, e)
        }
    }
}
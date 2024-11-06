package com.sensor.app

import android.app.Application
import com.sensor.app.data.schema.History
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApplication: Application() {
    companion object {
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    History::class
                )
            )
        )
    }

}
package com.sensor.app.data.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class History: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var title: String = ""
    var gyroscopePath: String = ""
    var accelerometerPath: String = ""
    var magneticPath: String = ""
    var lightPath: String = ""
    var stepCounterPath: String = ""
    var locationPath: String = ""
    var timestamp: Long = 0
}
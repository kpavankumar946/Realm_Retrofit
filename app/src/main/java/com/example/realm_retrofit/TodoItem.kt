package com.example.realm_retrofit

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.UUID

open class TodoItem(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    var isCompleted: Boolean = false
) : RealmObject()
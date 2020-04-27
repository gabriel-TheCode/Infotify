package com.thecode.infotify.entities

import io.realm.RealmObject

open class Source (
    var cnbc: String? = null,
    var name: String? = null,
    var description: String? = null,
    var url: String? = null,
    var category: String? = null,
    var language: String? = null,
    var country: String? = null

    ): RealmObject()
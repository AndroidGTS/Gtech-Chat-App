package com.gtechapps.chatmessager.Model

class User {
    var id: String? = null
    var username: String? = null
    var imageURL: String? = null
    var status: String? = null
    var search: String? = null
    var bio: String? = null

    constructor(
        id: String?,
        username: String?,
        imageURL: String?,
        status: String?,
        search: String?,
        bio: String?
    ) {
        this.id = id
        this.username = username
        this.imageURL = imageURL
        this.status = status
        this.search = search
        this.bio = bio
    }

    constructor()
}
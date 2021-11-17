package com.gtechapps.chatmessager.Model

class Chat {
    var sender: String? = null
    var receiver: String? = null
    var message: String? = null
    var isIsseen = false
        private set
    var time: String? = null

    constructor(
        sender: String?,
        receiver: String?,
        message: String?,
        isseen: Boolean,
        time: String?
    ) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        isIsseen = isseen
        this.time = time
    }

    constructor()

    fun setIsseen(isseen: Boolean) {
        isIsseen = isseen
    }
}
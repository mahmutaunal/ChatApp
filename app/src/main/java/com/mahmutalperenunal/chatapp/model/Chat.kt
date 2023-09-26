package com.mahmutalperenunal.chatapp.model

data class Chat(
    var sender: String? = null,
    var message: String? = null,
    var receiver: String? = null,
    var isSeen: Boolean = false,
    var url: String? = null,
    var messageId: String? = null
)
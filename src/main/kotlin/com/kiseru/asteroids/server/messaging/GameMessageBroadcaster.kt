package com.kiseru.asteroids.server.messaging

class GameMessageBroadcaster {

    private val sendMessageFns = mutableListOf<(String) -> Unit>()

    fun addRecipient(fn: (String) -> Unit) {
        sendMessageFns.add(fn)
    }

    fun broadcast(message: String) {
        for (fn in sendMessageFns) {
            fn(message)
        }
    }
}
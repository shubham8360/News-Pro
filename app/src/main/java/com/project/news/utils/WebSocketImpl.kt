package com.project.news.utils

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

private const val TAG = "WebSocketImpl"
private const val NORMAL_CLOSURE_STATUS = 1000

class WebSocketImpl:WebSocketListener() {

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "onClosed: code $code reason $reason ")
       /* webSocket.send("Knock, knock!");
        webSocket.send("Hello!");
//        webSocket.send(ByteString.decodeHex("deadbeef"));
        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");*/
        super.onClosed(webSocket, code, reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d(TAG, "onClosing: code $code reason $reason ")
        super.onClosing(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d(TAG, "onFailure: response $response")
        response?.close()
        super.onFailure(webSocket, t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d(TAG, "onMessage: $text")
        super.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG, "onMessage: $bytes ")
        super.onMessage(webSocket, bytes)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d(TAG, "onOpen:  $response")
        webSocket.send("Knock, knock!");
        webSocket.send("Hello!");
//        webSocket.send(ByteString.decodeHex("deadbeef"));
//        webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
        super.onOpen(webSocket, response)
    }
}
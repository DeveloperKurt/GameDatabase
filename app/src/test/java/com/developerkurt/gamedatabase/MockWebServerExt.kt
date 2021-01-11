package com.developerkurt.gamedatabase.util


import com.developerkurt.gamedatabase.readStringFromResources
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

private val HEADER_CONTENT_TYPE_VALUE = "application/json; charset=utf-8"
private val HEADER_CONTENT_TYPE = "Content-Type"

/**
 * Enqueues GTA V's details as a success response
 */
fun MockWebServer.enqueueSuccessGameDetails()
{
    this.enqueue(
            MockResponse().setResponseCode(200)
                .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                .setBody(readStringFromResources("success_game_details_response.json")))
}

/**
 * Enqueues a list that contains 20 GameData as a success response
 */
fun MockWebServer.enqueueSuccessGameList()
{
    this.enqueue(
            MockResponse().setResponseCode(200)
                .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                .setBody(readStringFromResources("success_game_list_updated_response.json")))
}

/**
 * Enqueues a generic 404 not found response
 */
fun MockWebServer.enqueueNotFound()
{
    this.enqueue(
            MockResponse().setResponseCode(404)
                .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                .setBody(readStringFromResources("not_found_response.json")))
}
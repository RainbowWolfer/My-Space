package com.rainbowwolfer.myspacedemo1.models.exceptions

import com.rainbowwolfer.myspacedemo1.models.api.GoResponse

class ResponseException(
	val response: GoResponse
) : Exception()
package com.brawlpulse.api.common.exceptions

sealed class BhApiException : Exception()

class PlayerNotFoundException : BhApiException()

class InvalidApiKeyException : BhApiException()

class RateLimitExceededAfterRetryException : BhApiException()

class ServerErrorException : BhApiException()

class NetworkErrorException : BhApiException()
// Serializable
class DeserializationException : BhApiException()
package com.brawlpulse.api.common.exceptions

sealed class BhApiException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

class IllegalStateException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class InvalidApiKeyException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class NotFoundException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class BadRequestException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class RateLimitExceededException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class ServerErrorException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class UncoveredException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)

class DeserializationException(
    message: String? = null,
    cause: Throwable? = null
) : BhApiException(message, cause)
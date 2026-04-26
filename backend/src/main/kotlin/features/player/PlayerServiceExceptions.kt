package com.brawlpulse.api.features.player

sealed class PlayerServiceException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

class SteamIdNotLinkedException(
    message: String? = null,
    cause: Throwable? = null
) : PlayerServiceException(message, cause)

class BrawlhallaApiUnavailableException(
    message: String? = null,
    cause: Throwable? = null
) : PlayerServiceException(message, cause)
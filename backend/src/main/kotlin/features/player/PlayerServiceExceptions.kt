package com.brawlpulse.api.features.player

// Exceptions

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

// Results

sealed class AddPlayerResult {
    data class Created(val player: Player) : AddPlayerResult()
    data class AlreadyTracked(val player: Player) : AddPlayerResult()
}
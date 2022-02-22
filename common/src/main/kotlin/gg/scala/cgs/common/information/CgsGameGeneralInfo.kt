package gg.scala.cgs.common.information

import gg.scala.cgs.common.information.mode.CgsGameMode

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
open class CgsGameGeneralInfo(
    val fancyNameRender: String,
    val gameVersion: Float,
    val minimumPlayers: Int,
    val startingCountdownSec: Int,
    val awards: CgsGameAwardInfo,
    val disqualifyOnLogout: Boolean,
    val spectateOnDeath: Boolean,
    val showTabHearts: Boolean,
    val showNameHearts: Boolean,
    val usesCustomArenaWorld: Boolean,
    val gameModes: List<CgsGameMode>
)

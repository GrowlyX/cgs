package gg.scala.cgs.common.instance.game

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.states.CgsGameState
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
class CgsGameServerInfo(
    val uniqueId: UUID,
    var arenaId: String,
    var gameMode: String
)
{
    var state = CgsGameState.WAITING

    fun refresh()
    {
        participants = Bukkit.getOnlinePlayers()
            .filter { !it.hasMetadata("spectator") }
            .map { it.uniqueId }.toMutableList()

        spectators = Bukkit.getOnlinePlayers()
            .filter { it.hasMetadata("spectator") }
            .map { it.uniqueId }.toMutableList()

        state = CgsGameEngine.INSTANCE.gameState
    }

    var participants = mutableListOf<UUID>()
    var spectators = mutableListOf<UUID>()
}

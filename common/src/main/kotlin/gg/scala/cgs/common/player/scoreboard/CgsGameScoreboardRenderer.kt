package gg.scala.cgs.common.player.scoreboard

import gg.scala.cgs.common.states.CgsGameState
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
interface CgsGameScoreboardRenderer
{
    fun getTitle(): String

    fun render(lines: LinkedList<String>, player: Player, state: CgsGameState)
}

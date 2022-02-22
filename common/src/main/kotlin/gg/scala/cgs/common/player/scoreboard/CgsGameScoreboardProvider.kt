package gg.scala.cgs.common.player.scoreboard

import gg.scala.cgs.common.CgsGameEngine
import net.evilblock.cubed.scoreboard.ScoreboardAdapter
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard
import java.util.*

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
class CgsGameScoreboardProvider(
    private val engine: CgsGameEngine<*>
) : ScoreboardAdapter()
{
    override fun getTitle(player: Player) = engine.getScoreboardRenderer().getTitle()

    override fun getLines(board: LinkedList<String>, player: Player)
    {
        val renderer = engine.getScoreboardRenderer()
        renderer.render(board, player, engine.gameState)
    }

    override fun onScoreboardCreate(player: Player, scoreboard: Scoreboard) {
        if (engine.gameInfo.spectateOnDeath) {
            scoreboard.registerNewTeam("ghost").also {
                it.setCanSeeFriendlyInvisibles(true)
            }
        }

        if (engine.gameInfo.showTabHearts) {
            scoreboard.registerNewObjective("tabHealth", "health").also {
                it.displaySlot = DisplaySlot.PLAYER_LIST
            }
        }

        if (engine.gameInfo.showNameHearts) {
            scoreboard.registerNewObjective("nameHealth", "health").also {
                it.displaySlot = DisplaySlot.BELOW_NAME
                it.displayName = "${CC.D_RED}\u2764"
            }
        }
    }
}

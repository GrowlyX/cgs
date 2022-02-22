package gg.scala.cgs.common.player.handler

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.giveCoins
import gg.scala.cgs.common.teams.CgsGameTeamService
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
object CgsGameDisqualificationHandler
{
    fun disqualifyPlayer(
        player: Player,
        broadcastNotification: Boolean = false,
        setSpectator: Boolean = true
    )
    {
        val cgsGameTeam = CgsGameTeamService.getTeamOf(player) ?: return
        cgsGameTeam.eliminated.add(player.uniqueId)

        val cgsGamePlayer = CgsPlayerHandler.find(player)!!

        val statistics = CgsGameEngine.INSTANCE.getStatistics(cgsGamePlayer)
        statistics.losses.increment()

        if (CgsGameEngine.INSTANCE.gameInfo.spectateOnDeath && setSpectator)
        {
            CgsSpectatorHandler.setSpectator(player, true)

            player giveCoins (CgsGameEngine.INSTANCE.gameInfo.awards.participationCoinRange.random() to "Playing a game")
        } else if (broadcastNotification)
        {
            CgsGameEngine.INSTANCE.sendMessage(
                "${player.displayName}${CC.SEC} has been disqualified."
            )

            player giveCoins (CgsGameEngine.INSTANCE.gameInfo.awards.participationCoinRange.random() to "Playing a game")
        }
    }
}

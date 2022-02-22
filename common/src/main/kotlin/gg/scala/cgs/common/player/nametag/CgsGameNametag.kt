package gg.scala.cgs.common.player.nametag

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.teams.CgsGameTeamService
import net.evilblock.cubed.nametag.NametagInfo
import net.evilblock.cubed.nametag.NametagProvider
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
object CgsGameNametag : NametagProvider(
    "cgs", 10
)
{
    @JvmStatic
    val SPECTATOR = createNametag(CC.GRAY, "")

    @JvmStatic
    val GREEN = createNametag(CC.GREEN, "")

    @JvmStatic
    val RED = createNametag(CC.RED, "")

    override fun fetchNametag(toRefresh: Player, refreshFor: Player): NametagInfo
    {
        val viewer = CgsPlayerHandler.find(toRefresh)!!
        val target = CgsPlayerHandler.find(refreshFor)!!

        if (
            toRefresh.hasMetadata("spectator") &&
            refreshFor.hasMetadata("spectator")
        )
        {
            return SPECTATOR
        }

        val computed = CgsGameEngine.INSTANCE.getNametagAdapter()
            .computeNametag(viewer, target)

        val teamOfViewer = CgsGameTeamService.getTeamOf(toRefresh)
        val teamOfTarget = CgsGameTeamService.getTeamOf(refreshFor)

        return computed ?:
            if (teamOfTarget == teamOfViewer) GREEN else RED
    }
}

package gg.scala.cgs.common.player.visibility

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import net.evilblock.cubed.visibility.VisibilityAction
import net.evilblock.cubed.visibility.VisibilityAdapter
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
object CgsGameVisibility : VisibilityAdapter
{
    override fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction
    {
        if (toRefresh.hasMetadata("spectator"))
        {
            if (!refreshFor.hasMetadata("spectator"))
            {
                return VisibilityAction.HIDE
            }
        }

        if (!toRefresh.hasMetadata("spectator") && !refreshFor.hasMetadata("spectator"))
        {
            return VisibilityAction.NEUTRAL
        }

        val viewer = CgsPlayerHandler.find(toRefresh)!!
        val target = CgsPlayerHandler.find(refreshFor)!!

        return CgsGameEngine.INSTANCE.getVisibilityAdapter()
            .computeVisibility(viewer, target)
    }
}

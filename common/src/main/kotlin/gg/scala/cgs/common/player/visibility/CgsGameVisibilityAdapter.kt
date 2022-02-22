package gg.scala.cgs.common.player.visibility

import gg.scala.cgs.common.player.CgsGamePlayer
import net.evilblock.cubed.visibility.VisibilityAction

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
interface CgsGameVisibilityAdapter
{
    fun computeVisibility(viewer: CgsGamePlayer, target: CgsGamePlayer): VisibilityAction
}

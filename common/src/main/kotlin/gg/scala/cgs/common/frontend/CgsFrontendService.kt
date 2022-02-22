package gg.scala.cgs.common.frontend

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.channel.CgsOverridingSpectatorChannel
import gg.scala.cgs.common.player.nametag.CgsGameNametag
import gg.scala.cgs.common.player.scoreboard.CgsGameScoreboardProvider
import gg.scala.cgs.common.player.visibility.CgsGameVisibility
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.lemon.handler.ChatHandler
import net.evilblock.cubed.nametag.NametagHandler
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.visibility.VisibilityHandler

/**
 * @author GrowlyX
 * @since 1/17/2022
 */
@Service
@IgnoreAutoScan
object CgsFrontendService
{
    @Inject
    lateinit var engine: CgsGameEngine<*>

    @Configure
    fun configure()
    {
        ScoreboardHandler.configure(
            CgsGameScoreboardProvider(engine)
        )

        ChatHandler.registerChannelOverride(
            CgsOverridingSpectatorChannel
        )

        VisibilityHandler.registerAdapter(
            "cgs", CgsGameVisibility
        )

        NametagHandler.registerProvider(
            CgsGameNametag
        )
    }
}

package gg.scala.cgs.game

import gg.scala.cgs.common.information.arena.CgsGameArenaHandler
import gg.scala.cgs.game.command.CgsCommandService
import gg.scala.cgs.game.engine.CgsEngineConfigurationService
import gg.scala.cgs.game.locator.CgsInstanceLocator
import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
@Plugin(
    name = "CGS-Engine",
    depends = [
        PluginDependency("Cubed"),
        PluginDependency("helper"),
        PluginDependency("Lemon"),
        PluginDependency("Parties"),
        PluginDependency("cloudsync")
    ]
)
class CgsEnginePlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        var LOADING_STRING = ""
    }

    private val flavor = Flavor.create<CgsEnginePlugin>(
        FlavorOptions(logger)
    )

    override fun enable()
    {
        logger.info("*** Attempting to find CGS Game implementation! ***")

        server.scheduler.runTaskTimerAsynchronously(this,
            {
                LOADING_STRING = if (LOADING_STRING == "") "." else if (LOADING_STRING == ".") ".." else if (LOADING_STRING == "..") "..." else ""
            }, 0L, 10L
        )

        flavor.bind<CgsEnginePlugin>() to this
        flavor.inject(CgsInstanceLocator)

        CgsInstanceLocator.configure {
            flavor.inject(CgsCommandService)
            flavor.inject(CgsEngineConfigurationService)
        }

        CloudSyncDiscoveryService
            .discoverable.assets
            .apply {
                add("gg.scala.cgs:game:cgs-game")
                add("gg.scala.cgs:parties:cgs-parties")
            }
    }

    override fun disable()
    {
        flavor.close()

        CgsGameArenaHandler.close()
    }
}

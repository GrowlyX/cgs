package gg.scala.cgs.lobby

import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import gg.scala.cgs.lobby.locator.CgsInstanceLocator
import gg.scala.commons.ExtendedScalaPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
@Plugin(
    name = "CGS-Lobby",
    depends = [
        PluginDependency("Cubed"),
        PluginDependency("helper"),
        PluginDependency("Lemon"),
        PluginDependency("Tangerine"),
        PluginDependency("cloudsync")
    ]
)
class CgsLobbyPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<CgsLobbyPlugin>()
    }

    override fun enable()
    {
        INSTANCE = this

        CgsInstanceLocator.configure {
            invokeTrackedTask("lobby loading") {
                CgsGameLobby.INSTANCE.initialResourceLoad()
            }
        }
    }
}

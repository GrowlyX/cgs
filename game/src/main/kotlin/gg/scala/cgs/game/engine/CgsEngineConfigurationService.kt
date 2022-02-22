package gg.scala.cgs.game.engine

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.instance.CgsServerType
import gg.scala.cgs.common.instance.handler.CgsInstanceService
import gg.scala.cgs.game.CgsEnginePlugin
import gg.scala.cgs.game.listener.CgsGameEventListener
import gg.scala.cgs.game.listener.CgsGameGeneralListener
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.RedisHandler.buildMessage
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
@Service
object CgsEngineConfigurationService
{
    @Inject
    lateinit var plugin: CgsEnginePlugin

    @Configure
    fun configure()
    {
        CgsInstanceService.configure(CgsServerType.GAME_SERVER)

        Bukkit.getPluginManager().registerEvents(
            CgsGameEventListener, plugin
        )

        Bukkit.getPluginManager().registerEvents(
            CgsGameGeneralListener, plugin
        )

        CgsGameEngine.INSTANCE.initialResourceLoad()

        buildMessage(
            "add-server",
            "id" to Lemon.instance
                .settings.id,
            "address" to "127.0.0.1",
            "port" to Bukkit.getPort()
                .toString()
        ).dispatch(
            "cocoa",
            Lemon.instance.banana
        )

        Tasks.asyncTimer({
            Lemon.instance.localInstance
                .metaData["game-state"] = CgsGameEngine.INSTANCE
                .gameState.name.replace("STARTED", "IN_GAME")

            Lemon.instance.localInstance
                .metaData["remaining"] = Bukkit.getOnlinePlayers()
                .count { !it.hasMetadata("spectator") }.toString()
        }, 0L, 20L)
    }

    @Close
    fun close()
    {
        buildMessage(
            "remove-server",
            "id" to Lemon.instance
                .settings.id
        ).dispatch(
            "cocoa",
            Lemon.instance.banana
        )

        Lemon.instance.banana.useResource {
            it.hdel("cgs:servers", Lemon.instance.settings.id)
        }
    }
}

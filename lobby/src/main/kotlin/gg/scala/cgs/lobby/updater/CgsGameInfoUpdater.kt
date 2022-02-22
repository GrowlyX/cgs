package gg.scala.cgs.lobby.updater

import gg.scala.cgs.common.instance.CgsServerInstance
import gg.scala.cgs.common.instance.CgsServerType
import gg.scala.cgs.common.instance.handler.CgsInstanceService
import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import gg.scala.store.storage.type.DataStoreStorageType

/**
 * @author GrowlyX
 * @since 12/4/2021
 */
object CgsGameInfoUpdater : Thread("CGS - Instance Info Updater")
{
    val lobbies = mutableSetOf<CgsServerInstance>()

    val gameServers = mutableSetOf<CgsServerInstance>()
    val gameModeCounts = mutableMapOf<String, Int>()

    var lobbyTotalCount = 0
    var playingTotalCount = 0

    override fun run()
    {
        try
        {
            val engine = CgsGameLobby.INSTANCE

            val instances = CgsInstanceService.service
                .loadAll(DataStoreStorageType.REDIS)
                .join()

            lobbies.clear()
            gameServers.clear()

            for (instance in instances)
            {
                if (instance.value.type == CgsServerType.LOBBY)
                {
                    lobbies.add(instance.value)
                } else
                {
                    gameServers.add(instance.value)
                }
            }

            for (gameMode in engine.getGameInfo().gameModes)
            {
                gameModeCounts[gameMode.getId()] = instances.values
                    .filter { it.type == CgsServerType.GAME_SERVER }
                    .filter { it.gameServerInfo!!.gameMode == gameMode.getId() }
                    .sumOf { it.gameServerInfo!!.participants.size }
            }

            playingTotalCount = instances.values
                .filter { it.type == CgsServerType.GAME_SERVER }
                .sumOf { it.gameServerInfo!!.participants.size }

            playingTotalCount = instances.values
                .filter { it.type == CgsServerType.LOBBY }
                .sumOf { it.online }
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

        try
        {
            sleep(1000L)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}

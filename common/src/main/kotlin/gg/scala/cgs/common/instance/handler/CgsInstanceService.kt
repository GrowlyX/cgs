package gg.scala.cgs.common.instance.handler

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.instance.CgsServerInstance
import gg.scala.cgs.common.instance.CgsServerType
import gg.scala.cgs.common.instance.game.CgsGameServerInfo
import gg.scala.lemon.Lemon
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Bukkit
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
object CgsInstanceService
{
    var current by Delegates.notNull<CgsServerInstance>()
    var service by Delegates.notNull<DataStoreObjectController<CgsServerInstance>>()

    fun configure(type: CgsServerType)
    {
        current = CgsServerInstance(
            Lemon.instance.settings.id, type
        )

        if (type == CgsServerType.GAME_SERVER)
        {
            current.gameServerInfo = CgsGameServerInfo(
                CgsGameEngine.INSTANCE.uniqueId,
                CgsGameEngine.INSTANCE.gameArena.getId(),
                CgsGameEngine.INSTANCE.gameMode.getId()
            )
        }

        service = DataStoreObjectControllerCache.create()
        service.provideCustomSerializer(Serializers.gson)

        Tasks.asyncTimer(0L, 55L) {
            if (current.gameServerInfo != null)
            {
                current.gameServerInfo!!.refresh()
            }

            current.online = Bukkit.getOnlinePlayers().size

            service.save(current, DataStoreStorageType.REDIS)
        }
    }
}

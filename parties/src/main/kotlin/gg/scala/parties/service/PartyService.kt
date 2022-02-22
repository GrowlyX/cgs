package gg.scala.parties.service

import gg.scala.parties.model.Party
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.serializers.Serializers
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
object PartyService
{
    var service by Delegates.notNull<DataStoreObjectController<Party>>()

    private val loadedParties = mutableMapOf<UUID, Party>()

    fun configure()
    {
        service = DataStoreObjectControllerCache.create()
        service.provideCustomSerializer(Serializers.gson)
    }

    fun findPartyByLeader(uniqueId: UUID): Party?
    {
        return loadedParties.values.firstOrNull {
            it.leader.uniqueId == uniqueId
        }
    }

    fun findPartyByUniqueId(uniqueId: UUID): Party?
    {
        return loadedParties.values.firstOrNull {
            it.members.containsKey(uniqueId)
        }
    }

    fun findPartyByUniqueId(player: Player): Party?
    {
        return loadedParties.values.firstOrNull {
            it.members.containsKey(player.uniqueId)
        }
    }

    fun reloadPartyByUniqueId(uniqueId: UUID)
    {
        if (loadedParties[uniqueId] == null)
            return

        service
            .loadAll(DataStoreStorageType.REDIS)
            .thenAccept {
                val found = it.values.firstOrNull { playerParty ->
                    playerParty.uniqueId == uniqueId
                }

                if (found != null)
                {
                    kotlin.run {
                        loadedParties[found.uniqueId] = found
                    }
                }
            }
    }

    fun loadPartyOfPlayerIfAbsent(player: Player): CompletableFuture<Party?>
    {
        val loadedParty = findPartyByUniqueId(player)

        if (loadedParty != null)
        {
            val completable = CompletableFuture<Party?>()
            completable.complete(loadedParty)

            return completable
        } else
        {
            return service
                .loadAll(DataStoreStorageType.REDIS)
                .thenApply {
                    val found = it.values.firstOrNull { playerParty ->
                        playerParty.members.containsKey(player.uniqueId)
                    }

                    if (found != null)
                    {
                        kotlin.run {
                            loadedParties[found.uniqueId] = found
                        }
                    }

                    return@thenApply found
                }
        }
    }
}

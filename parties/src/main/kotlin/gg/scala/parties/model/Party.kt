package gg.scala.parties.model

import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.RedisHandler
import gg.scala.parties.service.PartyService
import gg.scala.parties.stream.PartyMessageStream
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import net.evilblock.cubed.util.bukkit.FancyMessage
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
data class Party(
    val uniqueId: UUID = UUID.randomUUID(),
    var leader: PartyMember
) : IDataStoreObject
{
    override val identifier: UUID
        get() = uniqueId

    val members = mutableMapOf<UUID, PartyMember>()
    val settings = mutableMapOf<PartySetting, Boolean>()

    fun sendMessage(message: FancyMessage)
    {
        PartyMessageStream.pushToStream(this, message)
    }

    fun saveAndUpdateParty(): CompletableFuture<Void>
    {
        return PartyService.service.save(
            this, DataStoreStorageType.REDIS
        ).thenRun {
            RedisHandler.buildMessage(
                "party-update",
                "uniqueId" to uniqueId.toString()
            ).dispatch(Lemon.instance.banana)
        }
    }
}

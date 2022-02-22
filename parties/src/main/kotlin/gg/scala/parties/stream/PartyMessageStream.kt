package gg.scala.parties.stream

import gg.scala.banana.BananaBuilder
import gg.scala.banana.annotate.Subscribe
import gg.scala.banana.message.Message
import gg.scala.banana.options.BananaOptions
import gg.scala.banana.subscribe.marker.BananaHandler
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.RedisHandler
import gg.scala.parties.model.Party
import gg.scala.parties.service.PartyService
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.FancyMessage
import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 12/31/2021
 */
object PartyMessageStream : BananaHandler
{
    private val banana = BananaBuilder()
        .options(
            BananaOptions(
                channel = "party:message_stream",
                async = true
            )
        )
        .credentials(
            Lemon.instance.credentials
        )
        .build()

    @Subscribe("pm_stream")
    fun onPmStream(message: Message)
    {
        val partyUniqueId = UUID.fromString(
            message["partyId"]!!
        )

        val rawContent = message["content"]!!

        val deserialized = Serializers.gson.fromJson(
            rawContent, FancyMessage::class.java
        )

        PartyService
            .findPartyByUniqueId(partyUniqueId)
            ?.let { party ->
                party.members.forEach {
                    val bukkitPlayer = Bukkit
                        .getPlayer(it.value.uniqueId)
                        ?: return@forEach

                    deserialized.sendToPlayer(bukkitPlayer)
                }
            }
    }

    fun pushToStream(
        party: Party, message: FancyMessage
    )
    {
        RedisHandler
            .buildMessage("pm_stream")
            .apply {
                this["partyId"] = party.identifier.toString()
                this["content"] = Serializers.gson.toJson(message)
            }
            .dispatch(banana)
    }
}

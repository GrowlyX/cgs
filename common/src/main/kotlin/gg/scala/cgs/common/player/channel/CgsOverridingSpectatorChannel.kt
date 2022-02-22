package gg.scala.cgs.common.player.channel

import gg.scala.lemon.player.channel.ChannelOverride
import gg.scala.lemon.player.rank.Rank
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
object CgsOverridingSpectatorChannel : ChannelOverride
{
    override fun getId() = "spectator"
    override fun isGlobal() = false

    override fun shouldCheckForPrefix() = false
    override fun shouldOverride(player: Player) = player.hasMetadata("spectator")

    override fun getPrefix() = ""
    override fun getWeight() = 10

    override fun getFormatted(
        message: String, sender: String,
        rank: Rank, receiver: Player
    ): String
    {
        return "${CC.GRAY}[Spectator] $sender: $message"
    }

    override fun hasPermission(t: Player) = t.hasMetadata("spectator")

    @Deprecated(
        message = "Please use ChannelOverride#shouldOverride.",
        replaceWith = ReplaceWith(
            "hasPermission(player)"
        )
    )
    override fun getPermission(): String? = null

}

package gg.scala.parties.model

import org.bukkit.Bukkit
import java.util.*

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
class PartyMember(
    val uniqueId: UUID,
    val role: PartyRole
)
{
    fun sendMessage(message: String)
    {
        Bukkit.getPlayer(uniqueId)?.sendMessage(message)
    }

    fun isOnline(): Boolean = Bukkit.getPlayer(uniqueId) != null
}

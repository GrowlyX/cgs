package gg.scala.cgs.common.teams

import java.util.*

/**
 * @author GrowlyX
 * @since 12/1/2021
 */
class CgsGameTeam(
    val id: Int
)
{
    val participants = mutableSetOf<UUID>()
    val eliminated = mutableSetOf<UUID>()

    val alive: List<UUID>
        get() = participants.filter { !eliminated.contains(it) }
}

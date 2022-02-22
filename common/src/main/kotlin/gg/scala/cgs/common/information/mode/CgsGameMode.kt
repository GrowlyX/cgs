package gg.scala.cgs.common.information.mode

import gg.scala.cgs.common.information.arena.CgsGameArena
import org.bukkit.Material

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
interface CgsGameMode
{
    fun getId(): String

    fun getName(): String
    fun getMaterial(): Pair<Material, Int>
    fun getDescription(): String

    fun getArenas(): List<CgsGameArena>

    fun getTeamSize(): Int
    fun getMaxTeams(): Int

    fun isSoloGame(): Boolean = getTeamSize() == 1
}

package gg.scala.cgs.lobby.leaderboard

import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.updating.FormatUpdatingHologramEntity
import net.evilblock.cubed.util.CC
import org.bukkit.Location
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 12/5/2021
 */
class CgsLobbyRankingHologram(
    location: Location,
    private val entry: CgsLobbyRankingEntry<*>
) : FormatUpdatingHologramEntity(
    entry.getId(), location
)
{
    fun initialLoad()
    {
        initializeData()

        EntityHandler.trackEntity(this)
        EntityHandler.saveData()
    }

    override fun getNewLines() = mutableListOf<String>().also {
        it.add("")
        it.add("${CC.B_PRI}${CgsGameLobby.INSTANCE.getGameInfo().fancyNameRender}")
        it.add("${CC.GRAY}${entry.getDisplay()}")
        it.add("")
        it.addAll(CgsLobbyRankingEngine.ID_TO_FORMAT[entry.getId()] ?: listOf())
        it.add("")
    }

    override fun getTickInterval() = TimeUnit.SECONDS.toMillis(90)
}

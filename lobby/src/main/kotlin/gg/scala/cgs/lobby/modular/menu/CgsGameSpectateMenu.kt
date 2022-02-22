package gg.scala.cgs.lobby.modular.menu

import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import gg.scala.cgs.lobby.updater.CgsGameInfoUpdater
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/4/2021
 */
class CgsGameSpectateMenu : PaginatedMenu()
{
    companion object
    {
        @JvmStatic
        val SLOTS = listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
        )
    }

    init
    {
        placeholdBorders = true
        autoUpdate = true
    }

    override fun size(buttons: Map<Int, Button>): Int = 36
    override fun getAllPagesButtonSlots(): List<Int> = SLOTS

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also {
            CgsGameInfoUpdater.gameServers
                .filter { it.gameServerInfo!!.state.isAfter(CgsGameState.STARTED) }
                .forEach { server ->
                    it[it.size] = CgsGameLobby.INSTANCE
                        .getFormattedButton(server)
                }
        }
    }

    override fun getPrePaginatedTitle(player: Player): String = "Spectate a Game"
}

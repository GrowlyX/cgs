package gg.scala.cgs.lobby.modular.menu

import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 12/5/2021
 */
class CgsGameJoinMenu : Menu(
    "Play ${CgsGameLobby.INSTANCE.getGameInfo().fancyNameRender}"
)
{
    init
    {
        autoUpdate = true
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return CgsGameLobby.INSTANCE.getGameModeButtons()
    }
}

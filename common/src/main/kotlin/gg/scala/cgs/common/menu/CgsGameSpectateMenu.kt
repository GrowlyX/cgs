package gg.scala.cgs.common.menu

import gg.scala.cgs.common.teams.CgsGameTeamService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
class CgsGameSpectateMenu : PaginatedMenu()
{
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also { buttons ->
            CgsGameTeamService.teams.values.forEach { team ->
                team.alive.mapNotNull {
                    Bukkit.getPlayer(it)
                }.forEach {
                    buttons[buttons.size] = SpectateButton(it)
                }
            }
        }
    }

    override fun getPrePaginatedTitle(player: Player) = "Spectate Menu"

    inner class SpectateButton(
        private val player: Player
    ) : Button()
    {
        override fun getButtonItem(player: Player): ItemStack
        {
            return ItemBuilder(Material.SKULL_ITEM)
                .owner(this.player.name)
                .name(this.player.displayName)
                .addToLore(
                    "${CC.YELLOW}Click to teleport."
                )
                .data(3).build()
        }

        override fun clicked(
            player: Player, slot: Int,
            clickType: ClickType, view: InventoryView
        )
        {
            player.teleport(this.player)
            player.sendMessage("${CC.GREEN}You've been teleported to ${CC.ID_GREEN}${this.player.displayName}${CC.GREEN}.")
        }
    }
}

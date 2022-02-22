package gg.scala.cgs.lobby.modular

import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import gg.scala.cgs.lobby.modular.menu.CgsGameJoinMenu
import gg.scala.cgs.lobby.modular.menu.CgsGameSpectateMenu
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.items.ConfigurableItemHandler
import gg.scala.tangerine.module.impl.HubModuleItemAdapter
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 12/4/2021
 */
@Service
object CgsLobbyModuleItems : HubModuleItemAdapter
{
    private val spectateItem = ItemBuilder(Material.WATCH)
        .name("${CC.B_PRI}Spectate Game")
        .addToLore(
            "${CC.GRAY}Spectate an ongoing game."
        )
        .glow().build()

    @Inject
    lateinit var engine: CgsGameLobby<*>

    // We need to wait for CgsGameLobby.INSTANCE to initialize
    private val joinGameItem by lazy {
        ItemBuilder(Material.NETHER_STAR)
            .name("${CC.B_PRI}Play ${
                engine.getGameInfo().fancyNameRender
            }")
            .addToLore(
                "${CC.GRAY}Join a new game."
            )
            .glow().build()
    }

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java).handler {
            ConfigurableItemHandler.items.forEach { (index, itemStack) ->
                it.player.inventory.setItem(index, itemStack)
            }

            it.player.inventory.setItem(4, joinGameItem)
            it.player.inventory.setItem(6, spectateItem)

            it.player.updateInventory()
        }

        Events.subscribe(PlayerInteractEvent::class.java)
            .filter { it.item != null && it.action.name.contains("RIGHT") }
            .handler {
                if (it.item.isSimilar(spectateItem))
                {
                    CgsGameSpectateMenu().openMenu(it.player)
                } else if (it.item.isSimilar(joinGameItem))
                {
                    CgsGameJoinMenu().openMenu(it.player)
                }
            }
    }

    // this is unused so just ignore it
    override val additionalItemsAndClickEvents = mutableMapOf<ItemStack, Map.Entry<Int, (Player) -> Unit>>()
}

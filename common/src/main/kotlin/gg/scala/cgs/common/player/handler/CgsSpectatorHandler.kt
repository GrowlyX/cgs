package gg.scala.cgs.common.player.handler

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.adventure
import gg.scala.cgs.common.refresh
import gg.scala.lemon.util.QuickAccess
import net.evilblock.cubed.nametag.NametagHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.visibility.VisibilityHandler
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


/**
 * @author GrowlyX
 * @since 11/30/2021
 */
object CgsSpectatorHandler
{
    private val engine = CgsGameEngine.INSTANCE

    private val invisibility = PotionEffect(
        PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 0, false
    )

    private val spectateMenu = ItemBuilder(Material.ITEM_FRAME)
        .name(CC.B_PRI + "Spectate Menu")
        .addToLore(
            CC.GRAY + "See a list of players",
            CC.GRAY + "that you're able to",
            CC.GRAY + "teleport to and spectate."
        ).build()

    private val returnToLobby = ItemBuilder(Material.BED)
        .name(CC.B_RED + "Return to Lobby")
        .addToLore(
            CC.RED + "Return to the ${engine.gameInfo.fancyNameRender} lobby."
        ).build()

    private val spectateTitle = Title.title(
        Component.text("YOU DIED")
            .decorate(TextDecoration.BOLD)
            .color(TextColor.fromHexString("#FF4F4B")),
        Component.text("Better luck next time!")
            .color(TextColor.fromHexString("#FFFFFF"))
    )

    fun removeSpectator(
        player: Player
    )
    {
        if (player.hasMetadata("spectator"))
        {
            player.removeMetadata("spectator", engine.plugin)
        }

        player refresh (false to GameMode.SURVIVAL)
        player.playerListName = QuickAccess.coloredName(player)

        player.inventory.clear()
        player.updateInventory()

        VisibilityHandler.update(player)
        NametagHandler.reloadPlayer(player)
    }

    @JvmOverloads
    fun setSpectator(
        player: Player, sendTitle: Boolean = true
    )
    {
        player.setMetadata("spectator", FixedMetadataValue(engine.plugin, true))

        Tasks.delayed(1L)
        {
            player refresh (true to GameMode.CREATIVE)
            player.addPotionEffect(invisibility, true)

            player.sendMessage("${CC.D_RED}✘ ${CC.RED}You've been made a spectator.")

            if (sendTitle)
            {
                player adventure {
                    it.showTitle(spectateTitle)
                }
            }

            Tasks.delayed(1L)
            {
                player.teleport(
                    engine.gameArena.getSpectatorLocation()
                )
            }

            player.playerListName = "${CC.GRAY}${player.name}"

            player.inventory.setItem(0, spectateMenu)
            player.inventory.setItem(8, returnToLobby)

            player.updateInventory()

            VisibilityHandler.update(player)
            NametagHandler.reloadPlayer(player)

            val cgsSpectatorAdd = CgsGameEngine
                .CgsGameSpectatorAddEvent(player)

            cgsSpectatorAdd.callNow()
        }
    }
}

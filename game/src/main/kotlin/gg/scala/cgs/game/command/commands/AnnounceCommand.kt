package gg.scala.cgs.game.command.commands

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.lemon.Lemon
import gg.scala.lemon.util.QuickAccess
import gg.scala.lemon.util.QuickAccess.sendGlobalFancyBroadcast
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.CommandPermission
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.apache.commons.lang3.time.DurationFormatUtils
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object AnnounceCommand : BaseCommand()
{
    @JvmStatic
    val COOL_DOWN = TimeUnit.SECONDS.toMillis(30L)

    private val engine = CgsGameEngine.INSTANCE

    private var lastAnnouncement = -1L
    private var lastAnnouncementCreator = ""

    @CommandAlias("announce")
    @CommandPermission("cgs.command.announce")
    fun onAnnounce(player: Player)
    {
        if (engine.gameState.isAfter(CgsGameState.STARTED))
        {
            throw ConditionFailedException("You cannot announce the game at this time.")
        }

        if (lastAnnouncement + COOL_DOWN > System.currentTimeMillis() && !player.isOp)
        {
            throw ConditionFailedException("You must wait ${
                DurationFormatUtils.formatDurationWords(
                    lastAnnouncement + COOL_DOWN - System.currentTimeMillis(), true, true
                )
            } before announcing this game again as $lastAnnouncementCreator${CC.RED} already announced the game.")
        }

        lastAnnouncement = System.currentTimeMillis()
        lastAnnouncementCreator = QuickAccess.coloredName(player) ?: "${CC.D_RED}Console"

        val fancyMessage = FancyMessage()
        fancyMessage.withMessage(
            "${CC.B_GOLD}Alert ${CC.B_GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${CC.SEC}Come play ${CC.WHITE}${
                engine.gameInfo.fancyNameRender
            }${CC.SEC} with $lastAnnouncementCreator${CC.SEC} on ${CC.GREEN}${
                Lemon.instance.settings.id
            }${CC.SEC}! ${CC.B_GREEN}[Connect]"
        )
        fancyMessage.andHoverOf(
            "${CC.GREEN}Click to switch to ${CC.D_GREEN}${
                Lemon.instance.settings.id
            }${CC.GREEN}!",
            "",
            "${CC.B_RED}Warning:${CC.RED} This will switch your server!"
        )
        fancyMessage.andCommandOf(
            ClickEvent.Action.RUN_COMMAND,
            "/join ${Lemon.instance.settings.id}"
        )

        sendGlobalFancyBroadcast(
            fancyMessage = fancyMessage,
            permission = null
        )
    }
}

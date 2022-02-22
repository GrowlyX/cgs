package gg.scala.cgs.game.command.commands

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.lemon.player.LemonPlayer
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.CommandPermission
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 12/20/2021
 */
object ReviveCommand : BaseCommand()
{
    @CommandAlias("revive")
    @CommandPermission("cgs.command.revive")
    fun onRevive(sender: CommandSender, target: LemonPlayer)
    {
        if (!target.bukkitPlayer!!.hasMetadata("spectator"))
            throw ConditionFailedException(
                "${target.getColoredName()}${CC.RED} is not spectating."
            )

        val playerReinstateEvent = CgsGameEngine
            .CgsGameParticipantReinstateEvent(
                target.bukkitPlayer!!, false
            )

        playerReinstateEvent.callNow()

        sender.sendMessage("${CC.SEC}You've put ${target.getColoredName()}${CC.SEC} back into the game.")
    }
}

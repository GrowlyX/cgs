package gg.scala.cgs.game.command.commands

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.runnable.state.StartingStateRunnable
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.CommandPermission
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object ForceStartCommand : BaseCommand()
{
    private val engine = CgsGameEngine.INSTANCE

    @CommandAlias("force-start")
    @CommandPermission("op")
    fun onForceStart(sender: CommandSender)
    {
        if (Bukkit.getOnlinePlayers().size <= 1)
        {
            throw ConditionFailedException("You cannot force-start the game when you are alone.")
        }

        if (engine.gameState.isAfter(CgsGameState.STARTING))
        {
            throw ConditionFailedException("You cannot force-start the game at this time.")
        }

        StartingStateRunnable.hasBeenForceStarted = true

        engine.onAsyncPreStartResourceInitialization()
            .thenAccept {
                engine.gameState = CgsGameState.STARTING

                val cgsGameForceStart = CgsGameEngine
                    .CgsGameForceStartEvent(sender)

                cgsGameForceStart.callNow()

                StartingStateRunnable.PRE_START_TIME = 11
            }
    }
}

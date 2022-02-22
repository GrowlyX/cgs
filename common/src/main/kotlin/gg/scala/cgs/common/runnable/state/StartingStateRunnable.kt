package gg.scala.cgs.common.runnable.state

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.runnable.StateRunnable
import gg.scala.cgs.common.startMessage
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object StartingStateRunnable : StateRunnable(
    CgsGameState.STARTING
)
{
    private val engine = CgsGameEngine.INSTANCE

    var hasBeenForceStarted = false

    @JvmField
    var PRE_START_TIME = engine.gameInfo
        .startingCountdownSec + 1

    private val alertTicks = listOf(
        18000, 14400, 10800, 7200, 3600, 2700,
        1800, 900, 600, 300, 240, 180, 120,
        60, 50, 40, 30, 15, 10, 5, 4, 3, 2, 1
    )

    private val rangeToColor = mutableMapOf(
        0..5 to CC.RED,
        5..10 to CC.GOLD
    )

    override fun onTick()
    {
        PRE_START_TIME--

        if (Bukkit.getOnlinePlayers().size < engine.gameInfo.minimumPlayers && !hasBeenForceStarted)
        {
            engine.gameState = CgsGameState.WAITING
            return
        }

        if (alertTicks.contains(PRE_START_TIME))
        {
            val currentTitle = Title.title(
                Component.text(PRE_START_TIME)
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.fromHexString("#2acc29")),
                Component.text("The game is starting!")
            )

            engine.sendTitle(currentTitle)
            engine.playSound(Sound.NOTE_STICKS)
            engine.sendMessage("${CC.SEC}The game starts in ${getCurrentColor()}${
                TimeUtil.formatIntoDetailedString((PRE_START_TIME))
            }${CC.SEC}.")
        }

        if (PRE_START_TIME <= 0)
        {
            val currentTitle = Title.title(
                Component.text("BEGIN")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.fromHexString("#2acc29")),
                Component.text("The game has started!")
            )

            engine.gameState = CgsGameState.STARTED

            engine.sendTitle(currentTitle)
            engine.playSound(Sound.LEVEL_UP)
            engine.sendMessage("${CC.GREEN}The game has commenced!")

            if (engine.gameInfo.gameVersion < 1.0)
            {
                engine.sendMessage(startMessage)
            }
        }
    }

    private fun getCurrentColor(): String
    {
        return rangeToColor.entries
            .firstOrNull { it.key.contains(PRE_START_TIME) }?.value
            ?: CC.GREEN
    }
}

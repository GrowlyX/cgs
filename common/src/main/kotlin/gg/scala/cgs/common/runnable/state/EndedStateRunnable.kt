package gg.scala.cgs.common.runnable.state

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.adventure
import gg.scala.cgs.common.giveCoins
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.runnable.StateRunnable
import gg.scala.lemon.util.CubedCacheUtil
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.evilblock.cubed.util.bukkit.Tasks
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.apache.commons.lang3.time.DurationFormatUtils
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object EndedStateRunnable : StateRunnable(
    CgsGameState.ENDED
)
{
    @JvmField
    var ALLOWED_TO_JOIN = true

    private val engine = CgsGameEngine.INSTANCE

    private val alertTicks = listOf(
        20, 15, 10, 5, 4, 3, 2, 1
    )

    override fun onTick()
    {
        if (currentTick == 0)
        {
            val description = mutableListOf<String>()
            description.add(" ")
            description.add(" ${CC.B_PRI}${engine.gameInfo.fancyNameRender} Game Overview:")
            description.add(" ${CC.GRAY}Duration: ${CC.WHITE}${
                DurationFormatUtils.formatDurationWords(
                    System.currentTimeMillis() - engine.gameStart,
                    true, true
                )
            }")
            description.add(" ${CC.GRAY}Winner${
                if (engine.gameMode.getTeamSize() == 1) "" else "s"
            }: ${CC.WHITE}${
                engine.winningTeam.alive.joinToString(
                    separator = ", "
                ) { 
                    Bukkit.getPlayer(it)?.name ?: "???"
                }
            }")

            description.add("")
            description.addAll(engine.getExtraWinInformation())
            description.add("")

            description.add(" ${CC.GREEN}Thanks for playing ${engine.gameInfo.fancyNameRender}!")
            description.add("")

            engine.sendMessage(
                FancyMessage().withMessage(*description.toTypedArray())
            )

            val currentTitle = Title.title(
                Component.text("YOU WON")
                    .decorate(TextDecoration.BOLD)
                    .color(TextColor.fromHexString("#2acc29")),
                Component.text("Congratulations!")
            )

            engine.winningTeam.alive.forEach {
                val bukkitPlayer = Bukkit.getPlayer(it)
                    ?: return@forEach
                val cgsGamePlayer = CgsPlayerHandler.find(bukkitPlayer)!!

                val statistics = engine.getStatistics(cgsGamePlayer)
                statistics.wins.increment()

                bukkitPlayer giveCoins (engine.gameInfo.awards.winningCoinRange.random() to "Winning a ${engine.gameInfo.fancyNameRender} game")
                bukkitPlayer adventure { audi ->
                    audi.showTitle(currentTitle)
                }
            }
        }

        if (alertTicks.contains(10 - currentTick))
        {
            engine.sendMessage("${CC.B_RED}The server will automatically reboot in ${10 - currentTick} seconds.")
        }

        if (currentTick == 10)
        {
            val kickMessage = CC.YELLOW + engine.winningTeam.alive.joinToString(
                separator = "${CC.GREEN}, ${CC.YELLOW}"
            ) {
                CubedCacheUtil.fetchName(it) ?: "???"
            } + CC.GREEN + " won the game. Thanks for playing!"

            Tasks.sync {
                for (onlinePlayer in Bukkit.getOnlinePlayers())
                {
                    onlinePlayer.kickPlayer(kickMessage)
                }
            }

            ALLOWED_TO_JOIN = false

            Schedulers.sync().runLater(
                {
                    Bukkit.shutdown()
                }, 40L
            )
        }
    }
}

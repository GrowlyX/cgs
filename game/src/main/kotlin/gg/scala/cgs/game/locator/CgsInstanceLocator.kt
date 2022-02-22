package gg.scala.cgs.game.locator

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.game.CgsEnginePlugin
import gg.scala.flavor.inject.Inject
import me.lucko.helper.Schedulers
import me.lucko.helper.scheduler.Task
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
object CgsInstanceLocator : Runnable
{
    @Inject
    lateinit var plugin: CgsEnginePlugin

    private var found = false

    private var attempts = -1
    private var lambda = {}

    private lateinit var task: Task

    fun configure(lambda: () -> Unit)
    {
        task = Schedulers.sync().runRepeating(
            this, 0L, 20L
        )

        this.lambda = lambda
    }

    override fun run()
    {
        if (found)
        {
            task.closeAndReportException()
            return
        }

        attempts++

        try
        {
            // The not null delegate will throw an exception
            val engine = CgsGameEngine.INSTANCE

            println(
                """
                    *** IMPLEMENTATION WAS FOUND! INFORMATION BELOW ***
                    *** Mini-game: ${engine.gameInfo.fancyNameRender} v${engine.gameInfo.gameVersion} ***
                    *** Game Mode: ${engine.gameMode.getName()} ***
                    *** Map: ${engine.gameArena.getId()} ***
                """.trimIndent()
            )

            plugin.logger.info(
                "CGS found an implementation, now booting into the WAITING state..."
            )

            task.closeAndReportException()

            found = true
            lambda.invoke()
        } catch (e: Exception)
        {
            e.printStackTrace()

            if (attempts >= 5)
            {
                plugin.logger.severe(
                    "*** IMPLEMENTATION WAS NOT FOUND, SHUTTING DOWN ***"
                )

                Bukkit.shutdown()
                return
            }

            plugin.logger.warning(
                "Waiting for implementation to be located..."
            )
        }
    }
}

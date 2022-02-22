package gg.scala.cgs.lobby.locator

import gg.scala.cgs.lobby.CgsLobbyPlugin
import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import me.lucko.helper.Schedulers
import me.lucko.helper.scheduler.Task
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
object CgsInstanceLocator : Runnable
{
    var found = false

    private var attempts = -1
    private var lambda = {}

    private lateinit var task: Task

    fun configure(lambda: () -> Unit)
    {
        task = Schedulers.sync().runRepeating(
            this, 0L, 20L
        )

        CgsInstanceLocator.lambda = lambda
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
            CgsGameLobby.INSTANCE

            println(
                """
                    *** IMPLEMENTATION WAS FOUND! INFORMATION BELOW ***
                """.trimIndent()
            )

            CgsLobbyPlugin.INSTANCE.logger.info(
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
                CgsLobbyPlugin.INSTANCE.logger.severe(
                    "*** IMPLEMENTATION WAS NOT FOUND, SHUTTING DOWN ***"
                )

                Bukkit.shutdown()
                return
            }

            CgsLobbyPlugin.INSTANCE.logger.warning(
                "Waiting for implementation to be located..."
            )
        }
    }
}

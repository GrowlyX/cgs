package gg.scala.cgs.common.runnable

import gg.scala.cgs.common.runnable.state.EndedStateRunnable
import gg.scala.cgs.common.runnable.state.StartedStateRunnable
import gg.scala.cgs.common.runnable.state.StartingStateRunnable
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.util.bukkit.Tasks

/**
 * @author GrowlyX
 * @since 12/22/2021
 */
@Service
@IgnoreAutoScan
object StateRunnableService
{
    private val registered = mutableMapOf<CgsGameState, StateRunnable>()

    @Configure
    fun configure()
    {
        registerOrOverride(
            CgsGameState.STARTING, StartingStateRunnable
        )

        registerOrOverride(
            CgsGameState.STARTED, StartedStateRunnable
        )

        registerOrOverride(
            CgsGameState.ENDED, EndedStateRunnable
        )
    }

    fun registerOrOverride(
        state: CgsGameState, runnable: StateRunnable
    )
    {
        registered[state] = runnable
    }

    fun startRunningAsync(state: CgsGameState)
    {
        registered[state]?.let {
            Tasks.asyncTimer(it, 0L, 20L)
        }
    }
}

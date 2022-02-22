package gg.scala.cgs.common.statistics

import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.player.statistic.GameSpecificStatistics
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import me.lucko.helper.Events
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
class CgsStatisticService<S : GameSpecificStatistics>
{
    @Inject
    lateinit var provider: CgsStatisticProvider<S>

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerJoinEvent::class.java).handler {
            CgsPlayerHandler.find(it.player)?.let { player ->
                try
                {
                    provider.getStatistics(player)
                } catch (ignored: Exception)
                {
                    player.gameSpecificStatistics[provider.statisticType.java.simpleName] =
                        provider.statisticType.java.newInstance() as S
                }
            }
        }
    }
}

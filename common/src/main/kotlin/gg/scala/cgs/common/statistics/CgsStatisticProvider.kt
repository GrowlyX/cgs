package gg.scala.cgs.common.statistics

import gg.scala.cgs.common.player.CgsGamePlayer
import gg.scala.cgs.common.player.statistic.GameSpecificStatistics
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
interface CgsStatisticProvider<S : GameSpecificStatistics>
{
    val statisticType: KClass<S>
    fun getStatistics(cgsGamePlayer: CgsGamePlayer): S
}

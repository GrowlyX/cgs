package gg.scala.cgs.common.player.statistic

import gg.scala.cgs.common.player.statistic.value.CgsGameStatistic
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializable

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
abstract class GameSpecificStatistics : AbstractTypeSerializable
{
    abstract var gameKills: CgsGameStatistic

    abstract var kills: CgsGameStatistic
    abstract var deaths: CgsGameStatistic

    abstract var played: CgsGameStatistic
    abstract var wins: CgsGameStatistic
    abstract var losses: CgsGameStatistic
}

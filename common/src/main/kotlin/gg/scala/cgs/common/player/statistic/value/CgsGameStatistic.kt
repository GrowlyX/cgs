package gg.scala.cgs.common.player.statistic.value

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
class CgsGameStatistic
{
    var value = 0

    fun increment()
    {
        value++
    }

    fun decrement()
    {
        value--
    }

    fun update(new: Int)
    {
        value = new
    }

    fun reset()
    {
        value = 0
    }
}

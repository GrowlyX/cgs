package gg.scala.cgs.common.states.machine

/**
 * @author GrowlyX
 * @since 12/19/2021
 */
interface CgsGameStateMachine
{
    fun started(): Boolean
    fun startTimestamp(): Long

    fun id(): String
    fun onEnd()
    fun onStart()

    fun onUpdate()
    fun getTimeout(): Long
}

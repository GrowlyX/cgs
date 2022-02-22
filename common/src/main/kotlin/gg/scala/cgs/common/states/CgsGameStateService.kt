package gg.scala.cgs.common.states

import gg.scala.cgs.common.states.machine.CgsGameStateMachine
import java.util.*

/**
 * @author GrowlyX
 * @since 12/19/2021
 */
object CgsGameStateService
{
    val stateMachines = LinkedList<CgsGameStateMachine>()

    fun register(stateMachine: CgsGameStateMachine)
    {
        stateMachines.add(stateMachine)
    }

    fun current() = stateMachines.peek()
}

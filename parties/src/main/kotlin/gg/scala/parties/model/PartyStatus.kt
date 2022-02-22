package gg.scala.parties.model

import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
enum class PartyStatus(
    val formatted: String
)
{
    PUBLIC("${CC.GREEN}Public"),
    PRIVATE("${CC.RED}Private")
}

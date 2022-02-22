package gg.scala.parties.model

import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 12/2/2021
 */
enum class PartyRole(
    val formatted: String
)
{
    MEMBER("${CC.D_GRAY}Member"),
    MODERATOR("${CC.D_GREEN}Mod"),
    LEADER("${CC.GOLD}Leader");

    infix fun PartyRole.over(role: PartyRole): Boolean
    {
        return role.ordinal >= this.ordinal
    }
}

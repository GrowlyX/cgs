package gg.scala.cgs.common.player.handler

import net.evilblock.cubed.util.CC
import org.apache.commons.lang.WordUtils
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.ceil


/**
 * @author GrowlyX
 * @since 12/2/2021
 */
object CgsDeathHandler
{
    fun formDeathMessage(entity: Player, killer: Entity?): String
    {
        var output = getEntityName(entity, true) + CC.SEC
        val cause = entity.lastDamageCause

        if (cause != null)
        {
            val killerName = getEntityName(killer)

            when (cause.cause!!)
            {
                Cause.BLOCK_EXPLOSION, Cause.ENTITY_EXPLOSION -> output += " was blown to smithereens"
                Cause.CONTACT -> output += " was pricked to death"
                Cause.DROWNING -> output += if (killer != null)
                {
                    " drowned while fighting $killerName"
                } else
                {
                    " drowned"
                }
                Cause.ENTITY_ATTACK -> if (killer != null)
                {
                    output += " was slain by $killerName"

                    if (killer is Player)
                    {
                        val hand = killer.itemInHand
                        val handString =
                            if (hand == null) "their fists" else if (hand.hasItemMeta() && hand.itemMeta
                                    .hasDisplayName()
                            ) hand.itemMeta.displayName else WordUtils.capitalizeFully(
                                hand.type.name.replace("_", " ")
                            )

                        output += CC.SEC + " using " + CC.RED + handString
                    }
                }
                Cause.FALL -> output += if (killer != null)
                {
                    " hit the ground too hard thanks to $killerName"
                } else
                {
                    " hit the ground too hard"
                }
                Cause.FALLING_BLOCK ->
                {
                }
                Cause.FIRE_TICK, Cause.FIRE -> output += if (killer != null)
                {
                    " burned to death thanks to $killerName"
                } else
                {
                    " burned to death"
                }
                Cause.LAVA -> output += if (killer != null)
                {
                    " tried to swim in lava while fighting $killerName"
                } else
                {
                    " tried to swim in lava"
                }
                Cause.MAGIC -> output += " died"
                Cause.MELTING -> output += " died of melting"
                Cause.POISON -> output += " was poisoned"
                Cause.LIGHTNING -> output += " was struck by lightning"
                Cause.PROJECTILE -> if (killer != null)
                {
                    output += " was shot to death by $killerName"
                }
                Cause.STARVATION -> output += " starved to death"
                Cause.SUFFOCATION -> output += " suffocated in a wall"
                Cause.SUICIDE -> output += " committed suicide"
                Cause.THORNS -> output += " died whilst trying to kill $killerName"
                Cause.VOID -> output += if (killer != null)
                {
                    " fell into the void thanks to $killerName"
                } else
                {
                    " fell into the void"
                }
                Cause.WITHER -> output += " withered away"
                Cause.CUSTOM -> output += " died"
            }
        } else
        {
            output += " died for unknown reasons"
        }

        return "$output${CC.SEC}."
    }

    private fun getEntityName(entity: Entity?, doNotAddHealth: Boolean = false): String
    {
        entity ?: return ""

        val output: String = if (entity is Player)
        {
            val health = ceil(entity.health) / 2.0

            "${entity.displayName}${
                if (!doNotAddHealth)
                    " ${CC.D_RED}[${health}❤]"
                else 
                    ""
            }"
        } else
        {
            val entityName: String = if (entity.customName != null)
                entity.customName else entity.type.name

            CC.SEC + "a " + CC.RED + WordUtils.capitalizeFully(entityName.replace("_", ""))
        }

        return output
    }

    fun getKiller(player: Player): CraftEntity?
    {
        val lastAttacker = (player as CraftPlayer).handle.lastDamager
        return lastAttacker?.bukkitEntity
    }
}

typealias Cause = EntityDamageEvent.DamageCause

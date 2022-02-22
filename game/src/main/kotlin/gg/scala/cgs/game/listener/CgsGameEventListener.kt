package gg.scala.cgs.game.listener

import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsDeathHandler
import gg.scala.cgs.common.player.handler.CgsGameDisqualificationHandler
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.player.handler.CgsSpectatorHandler
import gg.scala.cgs.common.refresh
import gg.scala.cgs.common.respawnPlayer
import gg.scala.cgs.common.runnable.StateRunnableService
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.teams.CgsGameTeamService
import gg.scala.lemon.disguise.update.event.PreDisguiseEvent
import gg.scala.lemon.util.QuickAccess.coloredName
import net.evilblock.cubed.nametag.NametagHandler
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants.HEART_SYMBOL
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import kotlin.math.ceil

/**
 * @author GrowlyX, puugz
 * @since 12/1/2021
 */
object CgsGameEventListener : Listener
{
    private val engine = CgsGameEngine.INSTANCE

    @EventHandler(
        priority = EventPriority.HIGH
    )
    fun onCgsParticipantConnect(
        event: CgsGameEngine.CgsGameParticipantConnectEvent
    )
    {
        val cgsGamePlayer = CgsPlayerHandler
            .find(event.participant)!!

        if (engine.gameState == CgsGameState.WAITING || engine.gameState == CgsGameState.STARTING)
        {
            val participantSize = Bukkit.getOnlinePlayers().size

            if (!CgsGameTeamService.allocatePlayersToAvailableTeam(cgsGamePlayer))
            {
                if (
                    !CgsGameTeamService.allocatePlayersToAvailableTeam(
                        cgsGamePlayer, forceRandom = true
                    )
                )
                {
                    event.participant.kickPlayer("${CC.RED}Sorry, we were unable to allocate you to a team.")
                }
            }

            engine.sendMessage(
                "${coloredName(event.participant)}${CC.SEC} has joined ${CC.AQUA}(${
                    "${participantSize}/${Bukkit.getMaxPlayers()}"
                })${CC.YELLOW}!"
            )

            event.participant.removeMetadata("spectator", engine.plugin)

            VisibilityHandler.update(event.participant)

            event.participant refresh (false to GameMode.SURVIVAL)

            if (engine.gameState == CgsGameState.WAITING)
            {
                event.participant.teleport(
                    engine.gameArena.getPreLobbyLocation()
                )
            }

            if (participantSize >= engine.gameInfo.minimumPlayers)
            {
                engine.onAsyncPreStartResourceInitialization()
                    .thenAccept {
                        engine.gameState = CgsGameState.STARTING
                    }
            } else {
                engine.sendMessage("${CC.SEC}The game requires ${CC.PRI + (engine.gameInfo.minimumPlayers - participantSize) + CC.SEC} more players to start.")
            }
        } else if (!event.reconnectCalled)
        {
            CgsSpectatorHandler.setSpectator(event.participant)

            // An extension to the spectator message which is
            // sent within the setSpectator method being called above this.
            Tasks.delayed(1L)
            {
                event.participant.sendMessage("${CC.D_RED}✘ ${CC.RED}This is due to your late entrance into the game.")
            }
        }
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onCgsParticipantReconnect(
        event: CgsGameEngine.CgsGameParticipantReconnectEvent
    )
    {
        if (event.connectedWithinTimeframe)
        {
            // The CGS game team should never be null.
            val cgsGameTeam = CgsGameTeamService.getTeamOf(event.participant)!!
            cgsGameTeam.eliminated.remove(event.participant.uniqueId)

            val cgsParticipantReinstate = CgsGameEngine
                .CgsGameParticipantReinstateEvent(event.participant, true)

            cgsParticipantReinstate.callNow()
        } else
        {
            CgsSpectatorHandler.setSpectator(
                event.participant, false
            )

            // We are delaying this message by one tick to send
            // the player the correct order of messages.

            // The original spectator notification is sent
            // during the delayed task.
            Tasks.delayed(1L)
            {
                event.participant.sendMessage("${CC.D_RED}✘ ${CC.RED}This is due to your late reconnection to the server.")
            }
        }
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onCgsParticipantDisconnect(
        event: CgsGameEngine.CgsGameParticipantDisconnectEvent
    )
    {
        if (engine.gameState == CgsGameState.WAITING || engine.gameState == CgsGameState.STARTING)
        {
            CgsGameTeamService.removePlayerFromTeam(event.participant)

            engine.sendMessage(
                "${event.participant.name}${CC.SEC} has left ${CC.AQUA}(${
                    "${Bukkit.getOnlinePlayers().size - 1}/${Bukkit.getMaxPlayers()}"
                })${CC.YELLOW}!"
            )
        } else if (engine.gameState.isAfter(CgsGameState.STARTED))
        {
            val cgsGamePlayer = CgsPlayerHandler
                .find(event.participant) ?: return

            // disqualification on death would also
            // mean disqualification on log-out.
            if (engine.gameInfo.disqualifyOnLogout)
            {
                CgsGameDisqualificationHandler.disqualifyPlayer(
                    player = event.participant,
                    broadcastNotification = true,
                    setSpectator = false
                )
            } else
            {
                // We are not considering spectators as
                // active players in the game.
                if (event.participant.hasMetadata("spectator"))
                {
                    cgsGamePlayer.lastPlayedGameId = null
                    return
                }

                // We're only adding reconnection data if the
                // player will not be disqualified on logout
                cgsGamePlayer.lastPlayedGameId = engine.uniqueId
                cgsGamePlayer.lastPlayedGameDisconnectionTimestamp = System.currentTimeMillis()
            }
        }
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onPreDisguise(event: PreDisguiseEvent)
    {
        if (engine.gameState.isAfter(CgsGameState.STARTED))
        {
            event.isCancelled = true
            event.player.sendMessage("${CC.RED}You are not allowed to disguise at this time.")
        }
    }

    @EventHandler(
        priority = EventPriority.HIGH
    )
    fun onPlayerDeath(event: PlayerDeathEvent)
    {
        val player = event.entity
        val location = event.entity.location
        val killer = event.entity.killer

        val cgsGamePlayer = CgsPlayerHandler.find(player)!!

        val statistics = engine.getStatistics(cgsGamePlayer)
        statistics.deaths.increment()

        respawnPlayer(event)

        if (killer != null)
        {
            val cgsGameKiller = CgsPlayerHandler.find(killer)!!
            val killerStatistics = engine.getStatistics(cgsGameKiller)

            killerStatistics.kills.increment()
            killerStatistics.gameKills.increment()
        }

        event.deathMessage = CgsDeathHandler
            .formDeathMessage(player, killer)

        val cgsDeathEvent = CgsGameEngine
            .CgsGameParticipantDeathEvent(player, location)

        if (engine.gameInfo.spectateOnDeath)
        {
            CgsGameDisqualificationHandler.disqualifyPlayer(
                player = player, broadcastNotification = false, setSpectator = true
            )
        }

        cgsDeathEvent.callNow()
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onCgsGameStart(
        event: CgsGameEngine.CgsGameStartEvent
    )
    {
        val participants = Bukkit.getOnlinePlayers()
            .filter { !it.hasMetadata("spectator") }

        engine.gameStart = System.currentTimeMillis()
        engine.originalRemaining = participants.size

        participants.forEach {
            NametagHandler.reloadPlayer(it)
        }

        StateRunnableService
            .startRunningAsync(CgsGameState.STARTED)
    }

    @EventHandler
    fun onCgsGameParticipantReinstate(
        event: CgsGameEngine.CgsGameParticipantReinstateEvent
    )
    {
        if (!event.connected)
        {
            CgsSpectatorHandler.removeSpectator(event.participant)
        }

        event.participant.sendMessage(
            "${CC.D_GREEN}✓ ${CC.GREEN}You've been added back into the game."
        )
    }

    @EventHandler
    fun onCgsGamePreStart(
        event: CgsGameEngine.CgsGamePreStartEvent
    )
    {
        StateRunnableService
            .startRunningAsync(CgsGameState.STARTING)
    }

    @EventHandler
    fun onCgsGameEnd(
        event: CgsGameEngine.CgsGameEndEvent
    )
    {
        StateRunnableService
            .startRunningAsync(CgsGameState.ENDED)
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onCgsForceStart(
        event: CgsGameEngine.CgsGameForceStartEvent
    )
    {
        engine.sendMessage("${CC.GREEN}The game has been force-started. ${CC.GRAY}(by ${
            if (event.starter is Player) event.starter.name else "Console"
        })")
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onEntityDamage(event: EntityDamageByEntityEvent)
    {
        val entity = event.entity
        val damagedBy = event.damager

        if (entity is Player && damagedBy is Player)
        {
            val cgsGameTeam = CgsGameTeamService
                .getTeamOf(damagedBy)!!

            if (cgsGameTeam.participants.contains(entity.uniqueId))
            {
                event.isCancelled = true
                event.damager.sendMessage(
                    "${CC.RED}You're unable to hurt ${CC.ITALIC}${entity.name}${CC.RED}."
                )
            }
        }
    }

    @EventHandler(
        priority = EventPriority.HIGHEST
    )
    fun onArrowShoot(event: EntityDamageByEntityEvent)
    {
        if (engine.gameState != CgsGameState.STARTED)
            return

        val player = event.entity
        val arrow = event.damager

        if (arrow is Arrow && player is Player)
        {
            val shooter = arrow.shooter

            if (shooter is Player)
            {
                if (arrow.getName() != shooter.name)
                {
                    val health = ceil(player.health - event.finalDamage) / 2.0

                    if (health > 0.0)
                    {
                        shooter.sendMessage("${coloredName(player)}${CC.SEC} is now at ${CC.RED}$health${HEART_SYMBOL}${CC.SEC}.")
                    }
                }
            }
        }
    }
}

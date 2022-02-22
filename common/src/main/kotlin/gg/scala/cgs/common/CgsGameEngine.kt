package gg.scala.cgs.common

import gg.scala.cgs.common.enviornment.EditableFieldService
import gg.scala.cgs.common.enviornment.editor.EnvironmentEditorService
import gg.scala.cgs.common.frontend.CgsFrontendService
import gg.scala.cgs.common.information.CgsGameGeneralInfo
import gg.scala.cgs.common.information.arena.CgsGameArena
import gg.scala.cgs.common.information.arena.CgsGameArenaHandler
import gg.scala.cgs.common.information.mode.CgsGameMode
import gg.scala.cgs.common.player.CgsGamePlayer
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.player.nametag.CgsGameNametagAdapter
import gg.scala.cgs.common.player.scoreboard.CgsGameScoreboardRenderer
import gg.scala.cgs.common.player.statistic.GameSpecificStatistics
import gg.scala.cgs.common.player.visibility.CgsGameVisibilityAdapter
import gg.scala.cgs.common.runnable.StateRunnableService
import gg.scala.cgs.common.runnable.state.EndedStateRunnable
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.statistics.CgsStatisticProvider
import gg.scala.cgs.common.statistics.CgsStatisticService
import gg.scala.cgs.common.teams.CgsGameTeam
import gg.scala.cgs.common.teams.CgsGameTeamService
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import gg.scala.lemon.Lemon
import me.lucko.helper.Events
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.evilblock.cubed.util.bukkit.Tasks
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author GrowlyX
 * @since 11/30/2021
 */
abstract class CgsGameEngine<S : GameSpecificStatistics>(
    val plugin: ExtendedScalaPlugin,
    val gameInfo: CgsGameGeneralInfo,
    val gameMode: CgsGameMode,
    override val statisticType: KClass<S>
) : CgsStatisticProvider<S>
{
    companion object
    {
        @JvmStatic
        var INSTANCE by Delegates.notNull<CgsGameEngine<*>>()
    }

    lateinit var gameArena: CgsGameArena

    val uniqueId: UUID = UUID.randomUUID()
    var gameState by SmartCgsState()

    var gameStart = 0L
    var originalRemaining = 0

    val audience = BukkitAudiences.create(plugin)

    val flavor = Flavor.create<CgsGameEngine<*>>(
        FlavorOptions(Logger.getLogger("CgsGameEngine"))
    )

    fun initialLoad()
    {
        INSTANCE = this
        gameArena = CgsGameArenaHandler.arena
    }

    fun initialResourceLoad()
    {
        flavor.bind<CgsGameEngine<S>>() to this

        plugin.invokeTrackedTask("initial loading CGS resources") {
            Serializers.useGsonBuilderThenRebuild {
                it.registerTypeAdapter(
                    GameSpecificStatistics::class.java,
                    AbstractTypeSerializer<GameSpecificStatistics>()
                )
            }

            flavor.inject(StateRunnableService)

            flavor.inject(CgsPlayerHandler)
            flavor.inject(CgsGameTeamService)

            flavor.inject(CgsFrontendService)
            flavor.inject(EnvironmentEditorService)

            flavor.inject(EditableFieldService)

            Events.subscribe(AsyncPlayerPreLoginEvent::class.java).handler {
                if (!EndedStateRunnable.ALLOWED_TO_JOIN)
                {
                    it.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        "${CC.RED}This server is currently whitelisted."
                    )
                }
            }

            flavor.bind<CgsStatisticProvider<S>>() to this
            flavor.injected<CgsStatisticService<S>>().configure()

            Lemon.instance.localInstance
                .metaData["game-server"] = "true"

            Bukkit.getServer().maxPlayers =
                gameMode.getMaxTeams() * gameMode.getTeamSize()
        }
    }

    fun sendMessage(message: String)
    {
        for (team in CgsGameTeamService.teams.values)
        {
            team.participants.forEach { uuid ->
                val bukkitPlayer = Bukkit.getPlayer(uuid)
                    ?: return@forEach

                if (!bukkitPlayer.hasMetadata("spectator"))
                    bukkitPlayer.sendMessage(message)
            }
        }

        for (spectator in Bukkit.getOnlinePlayers()
            .filter { it.hasMetadata("spectator") }
        )
        {
            spectator.sendMessage(message)
        }
    }

    fun sendTitle(title: Title)
    {
        for (team in CgsGameTeamService.teams.values)
        {
            team.participants.forEach { uuid ->
                val bukkitPlayer = Bukkit.getPlayer(uuid)
                    ?: return@forEach

                if (!bukkitPlayer.hasMetadata("spectator"))
                    bukkitPlayer adventure {
                        it.showTitle(title)
                    }
            }
        }

        for (spectator in Bukkit.getOnlinePlayers()
            .filter { it.hasMetadata("spectator") }
        )
        {
            spectator adventure {
                it.showTitle(title)
            }
        }
    }

    fun playSound(sound: Sound)
    {
        for (team in CgsGameTeamService.teams.values)
        {
            team.participants.forEach { uuid ->
                val bukkitPlayer = Bukkit.getPlayer(uuid)
                    ?: return@forEach

                if (!bukkitPlayer.hasMetadata("spectator"))
                    bukkitPlayer.playSound(
                        bukkitPlayer.location, sound, 1F, 1F
                    )
            }
        }

        for (spectator in Bukkit.getOnlinePlayers()
            .filter { it.hasMetadata("spectator") }
        )
        {
            spectator.playSound(
                spectator.location, sound, 1F, 1F
            )
        }
    }

    fun sendMessage(fancyMessage: FancyMessage)
    {
        for (team in CgsGameTeamService.teams.values)
        {
            team.participants.forEach { uuid ->
                val bukkitPlayer = Bukkit.getPlayer(uuid)
                    ?: return@forEach

                if (!bukkitPlayer.hasMetadata("spectator"))
                    fancyMessage.sendToPlayer(bukkitPlayer)
            }
        }

        for (spectator in Bukkit.getOnlinePlayers()
            .filter { it.hasMetadata("spectator") }
        )
        {
            fancyMessage.sendToPlayer(spectator)
        }
    }

    override fun getStatistics(cgsGamePlayer: CgsGamePlayer): S
    {
        return cgsGamePlayer.gameSpecificStatistics[statisticType.java.simpleName]!! as S
    }

    lateinit var winningTeam: CgsGameTeam

    protected fun onStateChange(oldState: CgsGameState)
    {
        var event: CgsGameEvent? = null

        if (compare(oldState, CgsGameState.WAITING, CgsGameState.STARTING))
        {
            event = CgsGamePreStartEvent()
        } else if (compare(oldState, CgsGameState.STARTING, CgsGameState.WAITING))
        {
            event = CgsGamePreStartCancelEvent()
        } else if (compare(oldState, CgsGameState.STARTING, CgsGameState.STARTED))
        {
            event = CgsGameStartEvent()
        } else if (compare(oldState, CgsGameState.STARTED, CgsGameState.ENDED))
        {
            event = CgsGameEndEvent()
        }

        Tasks.sync {
            event?.callNow()
        }
    }

    private fun compare(oldState: CgsGameState, expected: CgsGameState, newState: CgsGameState): Boolean
    {
        return oldState == expected && newState == gameState
    }

    open fun onAsyncPreStartResourceInitialization(): CompletableFuture<Boolean>
    {
        return CompletableFuture.supplyAsync { true }
    }

    abstract fun getScoreboardRenderer(): CgsGameScoreboardRenderer

    abstract fun getVisibilityAdapter(): CgsGameVisibilityAdapter
    abstract fun getNametagAdapter(): CgsGameNametagAdapter

    abstract fun getExtraWinInformation(): List<String>

    class CgsGameEndEvent : CgsGameEvent()

    class CgsGameStartEvent : CgsGameEvent()
    class CgsGamePreStartEvent : CgsGameEvent()

    class CgsGameForceStartEvent(
        val starter: CommandSender
    ) : CgsGameEvent()

    class CgsGamePreStartCancelEvent : CgsGameEvent()

    class CgsGameParticipantConnectEvent(
        val participant: Player, val reconnectCalled: Boolean
    ) : CgsGameEvent()

    class CgsGameParticipantReconnectEvent(
        val participant: Player, val connectedWithinTimeframe: Boolean
    ) : CgsGameEvent()

    class CgsGameParticipantReinstateEvent(
        val participant: Player,
        val connected: Boolean
    ) : CgsGameEvent()

    class CgsGameParticipantDisconnectEvent(
        val participant: Player
    ) : CgsGameEvent()

    class CgsGameParticipantDeathEvent(
        val participant: Player,
        val deathLocation: Location
    ) : CgsGameEvent()

    class CgsGameSpectatorAddEvent(
        val spectator: Player
    ) : CgsGameEvent()

    abstract class CgsGameEvent : Event(), Cancellable
    {
        companion object
        {
            @JvmStatic
            val handlerList = HandlerList()
        }

        private var internalCancelled = false

        override fun getHandlers() = handlerList
        override fun isCancelled() = internalCancelled

        override fun setCancelled(new: Boolean)
        {
            internalCancelled = new
        }

        fun callNow(): Boolean
        {
            if (!Bukkit.isPrimaryThread())
            {
                Tasks.sync {
                    Bukkit.getPluginManager().callEvent(this)
                }
                return true
            }

            Bukkit.getPluginManager().callEvent(this)

            return internalCancelled
        }
    }

    private inner class SmartCgsState : ReadWriteProperty<Any, CgsGameState>
    {
        private var value = CgsGameState.WAITING

        override fun getValue(thisRef: Any, property: KProperty<*>): CgsGameState
        {
            return value
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: CgsGameState)
        {
            val oldValue = this.value
            this.value = value

            onStateChange(oldValue)
        }
    }
}

package gg.scala.cgs.lobby.leaderboard

import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.lobby.gamemode.CgsGameLobby
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.util.CubedCacheUtil
import gg.scala.store.storage.type.DataStoreStorageType
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 12/4/2021
 */
@Service
object CgsLobbyRankingEngine
{
    @JvmStatic
    val ID_TO_FORMAT = mutableMapOf<String, List<String>>()

    @Inject
    lateinit var engine: CgsGameLobby<*>

    var entries by Delegates.notNull<List<CgsLobbyRankingEntry<*>>>()

    fun findEntry(id: String): CgsLobbyRankingEntry<*>?
    {
        return entries.firstOrNull {
            it.getId().equals(id, true)
        }
    }

    @Configure
    fun configure()
    {
        entries = engine.getRankingEntries().toList()

        Schedulers.async().runRepeating(Runnable {
            CgsPlayerHandler.handle
                .loadAll(DataStoreStorageType.MONGO)
                .thenAccept {
                    for (entry in entries)
                    {
                        val topTen = it.entries
                            .sortedByDescending { mapping -> entry.getValue(mapping.value) }
                            .subList(0, 9)

                        val formatted = mutableListOf<String>()

                        topTen.forEachIndexed { index, data ->
                            formatted.add(
                                "${CC.PRI}${index + 1}. ${CC.RESET}${
                                    CubedCacheUtil.fetchName(data.key)
                                } ${CC.GRAY}- ${CC.GREEN}${data.value}"
                            )
                        }

                        ID_TO_FORMAT[entry.getId()] = formatted
                    }
                }
        }, 0L, 20 * 60 * 5)
    }
}

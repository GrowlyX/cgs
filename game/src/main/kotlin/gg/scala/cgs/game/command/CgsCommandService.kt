package gg.scala.cgs.game.command

import gg.scala.cgs.common.enviornment.editor.EnvironmentEditorService
import gg.scala.cgs.game.CgsEnginePlugin
import gg.scala.cgs.game.command.commands.AnnounceCommand
import gg.scala.cgs.game.command.commands.EditCommand
import gg.scala.cgs.game.command.commands.ForceStartCommand
import gg.scala.cgs.game.command.commands.ReviveCommand
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import net.evilblock.cubed.command.manager.CubedCommandManager

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
@Service
object CgsCommandService
{
    @Inject
    lateinit var plugin: CgsEnginePlugin

    @Configure
    fun configure()
    {
        val manager = CubedCommandManager(plugin)

        Lemon.instance.registerCompletionsAndContexts(manager)

        manager.commandCompletions
            .registerAsyncCompletion("fields") {
                return@registerAsyncCompletion EnvironmentEditorService
                    .editable.map { it.field.name }
            }

        manager.registerCommand(AnnounceCommand)
        manager.registerCommand(ForceStartCommand)
        manager.registerCommand(EditCommand)
        manager.registerCommand(ReviveCommand)
    }
}

package gg.scala.cgs.game.command.commands

import gg.scala.cgs.common.enviornment.EditableFieldService
import gg.scala.cgs.common.enviornment.editor.EnvironmentEditorService
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.CommandCompletion
import net.evilblock.cubed.acf.annotation.CommandPermission
import net.evilblock.cubed.acf.annotation.Single
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 12/29/2021
 */
object EditCommand : BaseCommand()
{
    @CommandAlias("edit")
    @CommandCompletion("@fields")
    @CommandPermission("cgs.command.edit")
    fun onEdit(
        sender: CommandSender,
        @Single field: String,
        @Single value: String
    )
    {
        val entry = EnvironmentEditorService.editable
            .firstOrNull { it.field.name == field }
            ?: throw ConditionFailedException(
                "There is no field with the name $field."
            )

        val current = entry.field
            .get(entry.instance)

        entry.field.set(
            entry.instance,
            EditableFieldService
                .castFancy(entry, value)
        )

        sender.sendMessage("${CC.SEC}You set ${CC.PRI}${
            entry.field.name
        }${CC.SEC} to ${CC.WHITE}${
            entry.field.get(
                entry.instance
            )
        }${CC.SEC}. ${CC.GRAY}(previously $current)")
    }
}

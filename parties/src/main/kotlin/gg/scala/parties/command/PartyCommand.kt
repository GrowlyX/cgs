package gg.scala.parties.command

import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.CommandHelp
import net.evilblock.cubed.acf.annotation.Default
import net.evilblock.cubed.acf.annotation.HelpCommand

/**
 * @author GrowlyX
 * @since 12/17/2021
 */
object PartyCommand : BaseCommand()
{
    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }
}

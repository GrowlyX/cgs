package gg.scala.cgs.common.enviornment.editor

import gg.scala.cgs.common.enviornment.EditableFieldEntry
import gg.scala.cgs.common.runnable.state.EndedStateRunnable
import gg.scala.cgs.common.runnable.state.StartingStateRunnable
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan

/**
 * @author GrowlyX
 * @since 12/22/2021
 */
@Service
@IgnoreAutoScan
object EnvironmentEditorService
{
    val editable = mutableListOf<EditableFieldEntry>()

    @Configure
    fun configure()
    {
        registerAllEditables(StartingStateRunnable)
        registerAllEditables(EndedStateRunnable)
    }

    fun registerAllEditables(
        `object`: Any
    )
    {
        val fields = `object`.javaClass
            .declaredFields

        // Only @JvmFields will be registered,
        // so no checks are needed.
        for (field in fields)
        {
            // lmao
            if (!field.name.contains("_"))
                continue

            editable.add(
                EditableFieldEntry(
                    field, `object`
                )
            )
        }
    }
}

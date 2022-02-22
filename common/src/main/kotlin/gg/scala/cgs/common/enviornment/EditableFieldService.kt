package gg.scala.cgs.common.enviornment

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan

/**
 * @author GrowlyX
 * @since 12/29/2021
 */
@Service
@IgnoreAutoScan
object EditableFieldService
{
    private val casters = mutableMapOf<Class<*>, (String) -> Any>()

    @Configure
    fun configure()
    {
        casters[Int::class.java] = { it.toInt() }
        casters[Boolean::class.java] = { it.toBoolean() }
        casters[Double::class.java] = { it.toDouble() }
    }

    fun castFancy(
        entry: EditableFieldEntry,
        input: String
    ): Any
    {
        val caster = casters[entry.field.type]
            ?: return input

        return caster.invoke(input)
    }
}

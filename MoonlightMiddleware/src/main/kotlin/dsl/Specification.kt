package dsl

import eu.quanticol.moonlight.core.base.Box
import eu.quanticol.moonlight.core.base.Tuple
import eu.quanticol.moonlight.core.formula.Formula
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure
import eu.quanticol.moonlight.core.space.DistanceStructure
import eu.quanticol.moonlight.core.space.SpatialModel
import eu.quanticol.moonlight.domain.DoubleDomain
import main.Settings
import java.util.function.Function

/**
 * TODO: it currently supports a single specification. An identifier should 
 * be used otherwise in the input file that says "which" spec to use.
 */
object Specification {
    @JvmField
    val spatialModel: SpatialModel<Double> = Settings.buildSpatialModel(4);

    @JvmField
    val atoms = mutableMapOf<String, Function<Tuple, Box<Boolean>>>()

    @JvmField
    var formula: Formula? = null //throw Error("The formula has not been setup by the user")

    internal const val defaultDistanceFunction = "infinite"

    @JvmField
    val distanceFunctions: MutableMap<String, Function<SpatialModel<Double>, 
            DistanceStructure<Double, *>>> = mutableMapOf(
        defaultDistanceFunction to Function { infiniteDistance(it) }
    )

    private fun infiniteDistance(model: SpatialModel<Double>) = 
        DefaultDistanceStructure(
            { x: Double -> x },
            DoubleDomain(), 
            0.0, Double.MAX_VALUE, 
            model
        )

}

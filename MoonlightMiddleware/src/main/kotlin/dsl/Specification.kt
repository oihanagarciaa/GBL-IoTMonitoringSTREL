package dsl

import eu.quanticol.moonlight.core.base.Box
import eu.quanticol.moonlight.core.base.Tuple
import eu.quanticol.moonlight.core.formula.Formula
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure
import eu.quanticol.moonlight.core.space.DistanceStructure
import eu.quanticol.moonlight.core.space.SpatialModel
import eu.quanticol.moonlight.domain.DoubleDomain
import java.util.function.Function

object Specification {
    const val defaultDistanceFunction = "infinite"
    
    val spatialModel: SpatialModel<Double> = TODO()
    
    val atoms: MutableMap<String, Function<Tuple, Box<Boolean>>> =
        mutableMapOf()
    
    val distanceFunctions: MutableMap<String, Function<SpatialModel<Double>, 
            DistanceStructure<Double, Double>>> = mutableMapOf(
        defaultDistanceFunction to Function { infiniteDistance(it) })
    
    private fun infiniteDistance(model: SpatialModel<Double>): 
            DistanceStructure<Double, Double> {
        return DefaultDistanceStructure(
            { x: Double -> x },
            DoubleDomain(), 0.0, Double.MAX_VALUE, model
        )
    }
    
    var formula: Formula? = null
}

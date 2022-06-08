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
    
    val spatialModel: SpatialModel<Double> = TODO("Yet to be developed")
    
    val atoms = mutableMapOf<String, Function<Tuple, Box<Boolean>>>()
    
    var formula: Formula = throw Error("The formula has not been setup by the user")
    
    val distanceFunctions = mutableMapOf<String, 
            Function<SpatialModel<Double>, DistanceStructure<Double, Double>>>(
        defaultDistanceFunction to Function { infiniteDistance(it) }
    )
    
    internal const val defaultDistanceFunction = "infinite"
    
    private fun infiniteDistance(model: SpatialModel<Double>) = 
        DefaultDistanceStructure(
            { x: Double -> x },
            DoubleDomain(), 
            0.0, Double.MAX_VALUE, 
            model
        )
    
}

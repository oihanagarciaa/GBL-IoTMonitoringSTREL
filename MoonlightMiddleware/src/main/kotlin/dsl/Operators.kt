package dsl // DSL: Domain-specific languages

import dsl.Specification.defaultDistanceFunction
import dsl.Specification.distanceFunctions
import eu.quanticol.moonlight.core.formula.Formula
import eu.quanticol.moonlight.core.formula.Interval
import eu.quanticol.moonlight.core.space.DefaultDistanceStructure
import eu.quanticol.moonlight.core.space.DistanceStructure
import eu.quanticol.moonlight.core.space.SpatialModel
import eu.quanticol.moonlight.domain.DoubleDomain
import eu.quanticol.moonlight.formula.classic.AndFormula
import eu.quanticol.moonlight.formula.classic.NegationFormula
import eu.quanticol.moonlight.formula.classic.OrFormula
import eu.quanticol.moonlight.formula.spatial.EverywhereFormula
import eu.quanticol.moonlight.formula.spatial.ReachFormula
import eu.quanticol.moonlight.formula.spatial.SomewhereFormula
import eu.quanticol.moonlight.formula.temporal.EventuallyFormula
import eu.quanticol.moonlight.formula.temporal.GloballyFormula
import eu.quanticol.moonlight.formula.temporal.UntilFormula

// Interval shorthand
typealias interval = eu.quanticol.moonlight.core.formula.Interval

// Classical logic operators
infix fun Formula.implies(right: Formula) = OrFormula(NegationFormula(this), right)
infix fun Formula.or(right: Formula): Formula = OrFormula(this, right)
infix fun Formula.and(right: Formula): Formula = AndFormula(this, right)
fun not(argument: Formula): Formula = NegationFormula(argument)

// Temporal operators
fun eventually(argument: Formula): EventuallyFormula = EventuallyFormula(argument)
fun globally(argument: Formula): GloballyFormula = GloballyFormula(argument)
infix fun Formula.until(right: Formula): UntilFormula = UntilFormula(this, right)

// Temporal operators intervals
infix fun EventuallyFormula.within(interval: Interval) = EventuallyFormula(this.argument, interval)
infix fun GloballyFormula.within(interval: Interval) = GloballyFormula(this.argument, interval)
infix fun UntilFormula.within(interval: Interval) = UntilFormula(this.firstArgument, this.secondArgument, interval)

// Spatial operators
fun everywhere(argument: Formula): EverywhereFormula = EverywhereFormula(defaultDistanceFunction, argument)
fun somewhere(argument: Formula): SomewhereFormula = SomewhereFormula(defaultDistanceFunction, argument)
infix fun Formula.reach(right: Formula): ReachFormula = ReachFormula(this, defaultDistanceFunction, right)

// Spatial operators distances
infix fun SomewhereFormula.within(distanceInterval: Interval) =
    SomewhereFormula(addDistanceFunction(distanceInterval), this.argument)
infix fun EverywhereFormula.within(distanceInterval: Interval) =
    SomewhereFormula(addDistanceFunction(distanceInterval), this.argument)
infix fun ReachFormula.within(distanceInterval: Interval) = 
    ReachFormula(this.firstArgument, addDistanceFunction(distanceInterval), this.secondArgument)

private fun addDistanceFunction(interval: interval): String {
    val id = interval.toString()
    distanceFunctions.put(id) { intervalToDistance(interval, it) }
    return id
}

private fun intervalToDistance(interval: Interval, model: SpatialModel<Double>):
        DistanceStructure<Double, Double> { 
    return DefaultDistanceStructure({ x: Double -> x }, 
        DoubleDomain(), interval.start, interval.end, model)
}

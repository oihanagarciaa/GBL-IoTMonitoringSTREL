package dsl

import dsl.Specification.defaultDistanceFunction
import eu.quanticol.moonlight.core.formula.Formula
import eu.quanticol.moonlight.core.formula.Interval
import eu.quanticol.moonlight.formula.classic.AndFormula
import eu.quanticol.moonlight.formula.classic.NegationFormula
import eu.quanticol.moonlight.formula.classic.OrFormula
import eu.quanticol.moonlight.formula.spatial.EverywhereFormula
import eu.quanticol.moonlight.formula.spatial.ReachFormula
import eu.quanticol.moonlight.formula.spatial.SomewhereFormula
import eu.quanticol.moonlight.formula.temporal.EventuallyFormula
import eu.quanticol.moonlight.formula.temporal.GloballyFormula
import eu.quanticol.moonlight.formula.temporal.UntilFormula

typealias interval = eu.quanticol.moonlight.core.formula.Interval

// DSL: Domain-specific languages
fun impliesFormula(left: Formula, right: Formula) =
    OrFormula(NegationFormula(left), right)

infix fun Formula.implies(right: Formula) =
    OrFormula(NegationFormula(this), right)
infix fun Formula.or(right: Formula): Formula = OrFormula(this, right)
infix fun Formula.and(right: Formula): Formula = AndFormula(this, right)
fun not(argument: Formula): Formula = NegationFormula(argument)
fun eventually(argument: Formula): EventuallyFormula =
    EventuallyFormula(argument)

infix fun EventuallyFormula.within(interval: Interval): Formula =
    EventuallyFormula(this.argument, interval)

fun GloballyFormula.within(interval: Interval): Formula =
    GloballyFormula(this.argument, interval)

fun globally(argument: Formula, interval: Interval? = null): GloballyFormula =
    GloballyFormula(argument, interval)

fun everywhere(argument: Formula): EverywhereFormula =
    EverywhereFormula(defaultDistanceFunction, argument)

fun somewhere(argument: Formula): Formula =
    SomewhereFormula(defaultDistanceFunction, argument)

infix fun Formula.reach(right: Formula): ReachFormula =
    ReachFormula(this, defaultDistanceFunction, right)

infix fun ReachFormula.within(distance: String): ReachFormula =
    ReachFormula(this.firstArgument, distance, this.secondArgument)

infix fun Formula.reach2(right: Formula): (String) -> Formula {
    return { distance -> ReachFormula(this, distance, right) }
}

infix fun ((String) -> Formula).distance(distanceId: String): Formula {
    return this(distanceId)
}

infix fun Formula.until(right: Formula): Formula =
    UntilFormula(this, right)

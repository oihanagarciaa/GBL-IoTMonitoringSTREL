package main

import eu.quanticol.moonlight.core.base.Box
import eu.quanticol.moonlight.core.base.Tuple
import eu.quanticol.moonlight.core.formula.Formula
import eu.quanticol.moonlight.core.formula.Interval
import eu.quanticol.moonlight.formula.classic.AndFormula
import eu.quanticol.moonlight.formula.classic.NegationFormula
import eu.quanticol.moonlight.formula.classic.OrFormula
import eu.quanticol.moonlight.formula.spatial.EverywhereFormula
import eu.quanticol.moonlight.formula.spatial.ReachFormula
import eu.quanticol.moonlight.formula.spatial.SomewhereFormula
import eu.quanticol.moonlight.formula.temporal.EventuallyFormula
import eu.quanticol.moonlight.formula.temporal.UntilFormula
import java.util.function.Function

typealias NotFormula = NegationFormula

class Language {
    
    //val test = "Temperature" compare ((t) -> Box(true, true))
    
    val atoms: MutableMap<String, Function<Tuple, Box<Boolean>>> = 
        mutableMapOf()
    
    fun addAtom(id: String, operation: Function<Tuple, Box<Boolean>>) {
        atoms.set(id, operation)
    }
    
    private fun addIfAllowed(id: String, operation: Function<Tuple, Box<Boolean>>) {
        when(id) {
            "temperature" -> addAtom(id, operation)
            else -> throw UnsupportedOperationException("unsupported identifier")
        }
    }
    
    infix fun String.compare(operation: Function<Tuple, Box<Boolean>>) {
        addIfAllowed(this, operation)
    }
    
    private val distanceFunctionId = "base"
    
    fun impliesFormula(left: Formula, right: Formula) =
        OrFormula(NotFormula(left), right)

    infix fun Formula.implies(right: Formula) =
        OrFormula(NotFormula(this), right)
    infix fun Formula.or(right: Formula): Formula = OrFormula(this, right)
    infix fun Formula.and(right: Formula): Formula = AndFormula(this, right)
    fun not(argument: Formula): Formula = NotFormula(argument)
    fun eventually(argument: Formula, interval: Interval? = null): Formula =
        EventuallyFormula(argument, interval)

    fun globally(argument: Formula, interval: Interval? = null): Formula =
        EventuallyFormula(argument, interval)

    fun everywhere(argument: Formula): Formula =
        EverywhereFormula(distanceFunctionId, argument)

    fun somewhere(argument: Formula): Formula =
        SomewhereFormula(distanceFunctionId, argument)

    infix fun Formula.reach(right: Formula): Formula =
        ReachFormula(this, distanceFunctionId, right)

    infix fun Formula.reach2(right: Formula): (String) -> Formula {
        return { distance -> ReachFormula(this, distance, right) }
    }

    infix fun ((String) -> Formula).distance(distanceId: String): Formula {
        return this(distanceId)
    }

    infix fun Formula.until(right: Formula): Formula =
        UntilFormula(this, right)
}

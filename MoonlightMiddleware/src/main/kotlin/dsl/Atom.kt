package dsl

import eu.quanticol.moonlight.core.base.Box
import eu.quanticol.moonlight.core.base.Tuple
import eu.quanticol.moonlight.formula.AtomicFormula
import java.util.function.Function

/* ***********************************************************************
 * Public atom methods
 * ***********************************************************************/

infix fun String.equalTo(comparison: Number): AtomicFormula {
    val dimension = getDimensionFromName(this)
    addIfAllowed(this, checkEq(dimension, comparison))
    return AtomicFormula(this)
}

infix fun String.greaterThan(comparison: Number): AtomicFormula {
    val dimension = getDimensionFromName(this)
    addIfAllowed(this, checkGt(dimension, comparison.toDouble()))
    return AtomicFormula(this)
}

infix fun String.greaterThanEquals(comparison: Number): AtomicFormula {
    val dimension = getDimensionFromName(this)
    addIfAllowed(this, checkGte(dimension, comparison.toDouble()))
    return AtomicFormula(this)
}

infix fun String.lessThan(comparison: Number): AtomicFormula {
    val dimension = getDimensionFromName(this)
    addIfAllowed(this, checkLt(dimension, comparison.toDouble()))
    return AtomicFormula(this)
}

infix fun String.lessThanEquals(comparison: Number): AtomicFormula {
    val dimension = getDimensionFromName(this)
    addIfAllowed(this, checkLte(dimension, comparison.toDouble()))
    return AtomicFormula(this)
}

/* ***********************************************************************
 * Internal atom methods operation
 * ***********************************************************************/

private fun checkEq(dim: Int, other: Number): (Tuple) -> Box<Boolean> {
    return {
        val value: Number = it.getIthValue(dim)
        condToBox(value == other)
    }
}

private fun checkGt(dim: Int, other: Double): (Tuple) -> Box<Boolean> {
    return {
        val value: Double = it.getIthValue(dim)
        condToBox(value > other)
    }
}

private fun checkGte(dim: Int, other: Double): (Tuple) -> Box<Boolean> {
    return {
        val value: Double = it.getIthValue(dim)
        condToBox(value >= other)
    }
}

private fun checkLt(dim: Int, other: Double): (Tuple) -> Box<Boolean> {
    return {
        val value: Double = it.getIthValue(dim)
        condToBox(value < other)
    }
}

private fun checkLte(dim: Int, other: Double): (Tuple) -> Box<Boolean> {
    return {
        val value: Double = it.getIthValue(dim)
        condToBox(value <= other)
    }
}


private fun addAtom(id: String, operation: Function<Tuple, Box<Boolean>>) {
    Specification.atoms[id] = operation
}

private fun addIfAllowed(id: String, op: Function<Tuple, Box<Boolean>>) {
    when(id) {
        "temperature" -> addAtom(id, op)
        else -> throw Error("unsupported identifier")
    }
}

private fun condToBox(cond: Boolean): Box<Boolean> {
    return if (cond) Box(true, true) else Box(false, false)
}

private fun getDimensionFromName(id: String): Int {
    return when(sanitizeName(id)) {
        "temperature" -> 0
        "humidity" -> 1
        "co2" -> 2
        "tvoc" -> 3
        else -> throw Error("Unrecognized parameter")
    }
}

/**
 * We remove extra spaces and normalize to lowercase
 */
private fun sanitizeName(id: String) = id.trim().lowercase()

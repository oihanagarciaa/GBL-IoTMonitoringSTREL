import dsl.*

val tempGreat = "temperature" greaterThan 20
val tempLow = "temperature" lessThan 25

val f1 = tempGreat implies (everywhere(tempLow) within interval(0, 15))

Specification.formula = f1
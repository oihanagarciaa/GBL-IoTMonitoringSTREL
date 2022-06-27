import dsl.*

val co2Great = "co2" greaterThan 800
val co2Low = "co2" lessThan 800

val f1 = co2Great implies (eventually(co2Low) within interval(0, 1500))

Specification.formula = f1

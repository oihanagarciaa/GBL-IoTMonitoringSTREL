import dsl.*

val temp = "temperature" greaterThan 10
val humidity = "humidity" lessThan 10

val f1 = temp and (eventually(humidity) within interval(0, 1))

Specification.formula = f1

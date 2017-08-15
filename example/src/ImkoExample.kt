import io.sellmair.imko.Immutable
import java.awt.Color

/*
 * Copyright 2017 Sebastian Sellmair
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class TuningKit(boost: Int) : Immutable<TuningKit>() {
    constructor() : this(0)

    /**
     * Defines how many ps per cylinder are boosted
     */
    val boostPerCylinder = ivar(boost)
}

class Engine(cylinders: Int) : Immutable<Engine>() {
    constructor() : this(0)

    /**
     * Defines how many ps each cylinder has. This
     * cannot be changed
     */
    val baselinePower = ival(25)

    /**
     * The number of cylinders cannot be changed
     */
    val cylinders = ival(cylinders)

    /**
     * But the tuning
     */
    val tuningKit = ivar<TuningKit?>(null)
}

class Car(modelNumber: Long) : Immutable<Car>() {
    constructor() : this(-1)

    /**
     * The cars model number (cannot be changed)
     */
    val modelNumber = ival(modelNumber)

    /**
     * The color can be changed by bringing the car to the paint shop
     */
    val color = ivar(Color.BLUE)

    /**
     * We can tune the car!
     */
    val engine = ivar(Engine(cylinders = 6))


    /**
     * A function telling us how much power the car has
     */
    fun power(): Int {
        val cylinders = engine().cylinders()
        val baselinePower = engine().baselinePower()
        val tuningBoost = engine().tuningKit()?.boostPerCylinder?.get() ?: 0
        return cylinders * (baselinePower + tuningBoost)
    }
}


fun aliG() {
    // Lets buy the first car, which should have
    val firstCar = Car(modelNumber = 1)
    print(firstCar.power())     //Our first car has 150 ps!   //150
    print(firstCar.color())     // And it is blue!            //java.awt.Color[r=0,g=0,b=255]

    //lets get a yellow version of the car
    val yellowCar = firstCar.color { Color.YELLOW }

    // Now we have two nearly identical cars: One blue, One yellow.
    // Color of the first car: java.awt.Color[r=0,g=0,b=255] ; second car: java.awt.Color[r=255,g=255,b=0]
    print("Color of the first car: ${firstCar.color()} ; second car: ${yellowCar.color()}")


    //Lets tune the yellow car!
    val tunedYellowCar = yellowCar {
        engine {
            tuningKit {
                TuningKit(15)
            }
        }
    }

    // We now have a tuned version of our yellow car!
    // Power: yellow car: 150 ;  tuned version: 240
    print("Power: yellow car: ${yellowCar.power()} ;  tuned version: ${tunedYellowCar.power()}")


    //But now we want to get professional with our yellow car. We want to further tune it, but instead
    //Of simply defining another boost we want to multiply the current boost with the count of cylinders

    val monsterCar = tunedYellowCar{
        engine {
            tuningKit{
                this?.boostPerCylinder?.mutate {
                    // You have access to the number of cylinders here.
                    // This is extremely cool, because it is part of the engine
                    // but technically not part of the tuning kit
                    cylinders() * this
                }
            }
        }
    }

    // It worked! We have a monster version of our car!..: AliG would be proud of his yellow monster
    //Power: tuned car: 240 ;  monser version: 690
    print("Power: tuned car: ${tunedYellowCar.power()} ;  monser version: ${monsterCar.power()}")

}

fun print(a: Any) {
    System.out.println(a)
}

fun main(args: Array<String>) {
    aliG()
}
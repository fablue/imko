# imko
## Immutable Kotlin Objects
### What is imko?
Imko lets you define truly immutable objects in kotlin in a very efficient way and intuitive syntax. It also solves the problem of "nested immutability". Meaning the problem of your immutable object containing multiple other non trivial objects.

```
Object 1
| \
|  \
|   Object 2
|   |   \
|   |    \
|   |      Int 2
|   Int 1   
|
String
```

In the example above, Object 2 is considered as non-trivial because it holds references to two other variables. Imko defines a very cool syntax of getting mutations of this object.

For this example, getting a version with int2 being doubled:

```kotlin
val version2 = 
object1{
        object2{
              int2{
                  times(2)
               }
        }
    }
```

Or alternatively, written in a more compact way

```kotlin
val version2 = object1{ object2{ int2{ times(2) } } }
```

### Usage
I will explain how to use imko by using the [AliG example](https://github.com/fablue/imko/blob/master/example/src/ImkoExample.kt). 

#### Creating an immutable object
Lets build an immutable definition of a car with varias properties!
This is very similar to defining plain old kotlin objects. Start a class wich extends from Immutable

```kotlin
class Car : Immutable<Car>(){
}
```

The first thing to notice here is, that we have pass the actual implementation to the Immutable as type param. There is little to no reason for passing in any other super-type of the implementation. 

Defining variables and values is done by using the ```kotlin ivar()``` and ```kotlin ival()``` functions. Lets assume that our car should have a color and an engine as variables

```kotlin
class Car : Immutable<Car>(){
   val color = ivar(Color.BLUE)
   val engine = ivar(Engine(cylinders = 6))
}
```

The second thing we notice here is: All attributes of immutable objects are defined using the ```val``` keyword. You can see here, that the syntax is very similar to creating poko's. 

So lets say we want to have a model number which cannot be changed at all and is passed to the object using a constructor

```kotlin
class Car(modelNumber: Long) : Immutable<Car>() {
    /*
    Always provide an empty constructor, so the system is able to instantiate a new car
    */
    constructor() : this(-1)
    /**
     * The cars model number (cannot be changed)
     */
    val modelNumber = ival(modelNumber)
    val color = ivar(Color.BLUE)
    val engine = ivar(Engine(cylinders = 6))
}
```

This is pretty straight forward as well. The third thing to notice: Providing an empty constructor is essential to make sure that the system can instantiate a new car for you. Don't worry the -1 does not appear in any mutations :relaxed:

Lets create an Engine and a TuningKit definition in a similar fashion:

```kotlin
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
```


### Creating different versions of a car
We add a little function telling us the power of the car into car 
```kotlin
    fun power(): Int {
        val cylinders = engine().cylinders()
        val baselinePower = engine().baselinePower()
        val tuningBoost = engine().tuningKit()?.boostPerCylinder?.get() ?: 0
        return cylinders * (baselinePower + tuningBoost)
    }
```
______
Start by using a car with model number 1
```kotlin
    val firstCar = Car(modelNumber = 1)
    print(firstCar.power())     //Our first car has 150 ps!   //150
    print(firstCar.color())     // And it is blue!            //java.awt.Color[r=0,g=0,b=255]
```
It obviously has 150 ps and is blue!

_______
So lets create a yellow version of the car!
```kotlin
    //lets get a yellow version of the car
    val yellowCar = firstCar.color { Color.YELLOW }

    // Now we have two nearly identical cars: One blue, One yellow.
    // Color of the first car: java.awt.Color[r=0,g=0,b=255] ; second car: java.awt.Color[r=255,g=255,b=0]
    print("Color of the first car: ${firstCar.color()} ; second car: ${yellowCar.color()}")

```
 You can see, that we created a new version of the car sharing all attributes with the old version except, that it is yellow now! We used the invoke operator to create this version
________
Let's get a tuned version of our yellow car to demonstrated how nested immutablity can be handled!

```kotlin
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
```

Pretty nice, hmm? :relaxed: 

You are free to write it even in a very compact way: 

```kotlin
    tunedYellowCar = yellowCar { engine { tuningKit { TuningKit(boost = 15) } } }
```

__________
Now lets get crazy and even mutate the tuningKit to get a new version of the car and explore another need feature. 
Say we ant to create a monster version of our car by multiplying the number of cylinders with the current boost per cylinder. 

```kotlin
    //But now we want to get professional with our yellow car. We want to further tune it, but instead
    //Of simply defining another boost we want to multiply the current boost with the count of cylinders

    val monsterCar = tunedYellowCar {
        engine {
            tuningKit {
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
```

As you can see: You have access of the cylinders() attribute of the engine when mutating the tuningKit. 

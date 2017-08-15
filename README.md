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

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

In the example above, Object 2 is considered as non-trivial because it holds references to 2 other variables. Imko defines a very cool syntax of getting mutations of this object.

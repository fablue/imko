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

package io.sellmair.imko


import kotlin.reflect.full.createInstance

abstract class Immutable<O: Immutable<O>> {

    protected val references = mutableMapOf<Int, Any?>()
    protected var referenceCounter = 0



    fun <T> ival(t: T): Val<T, O> {
        @Suppress("UNCHECKED_CAST")
        val ival = Val<T,O>(this as O, referenceCounter, t)
        referenceCounter ++
        return ival
    }

    fun<T> ivar(t: T): Var<T, O> {
        @Suppress("UNCHECKED_CAST")
        val ivar= Var<T, O>(this as O, referenceCounter, t)
        referenceCounter++
        return ivar
    }


    open inner class Val<out T, out ImmutableType:
    Immutable<*>>(protected val immutable: ImmutableType, protected val index: Int, value: T){
        init {
            immutable.references[index] = value
        }
        @Suppress("UNCHECKED_CAST")
        fun get(): T {
            return references[index] as T
        }
        operator fun invoke(): T = this.get()
    }

    inner class Var<T, out ImmutableType: Immutable<*>>(immutable: ImmutableType, index: Int, value: T) :
            Val<T, ImmutableType>(immutable, index, value) {

        fun set(t: T?): ImmutableType {
            immutable::class
            val newInstance = immutable::class.createInstance()
            (0 until immutable.references.size)
                    .asSequence()
                    .forEach { newInstance.references[it] = immutable.references[it] }
            newInstance.references[index] = t
            return newInstance
        }

        operator fun invoke(t: T?): ImmutableType = this.set(t)

        operator fun invoke(block: (T) -> T): ImmutableType = this.mutate(block)
        fun mutate(block: (T)->T): ImmutableType{
            val originalInstance = this.get()
            val newInstance = block(originalInstance)
            if(originalInstance === newInstance){
                throw IllegalStateException("Mutation returned same instance")
            }
            return this.set(block(this.get()))
        }
    }
}
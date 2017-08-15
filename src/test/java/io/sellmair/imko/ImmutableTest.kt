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

import org.junit.Assert.*
import org.junit.Test

val initialAString: String? = null
val expectedAString = "expectedA"
val initialAnotherString = "initial"
val expectedAnotherString = "expected"

class ImmutableTest{



    class Imm : Immutable<Imm>(){
        val aString = ivar<String?>(initialAString)
        val anotherString = ivar(initialAnotherString)
    }


    @Test
    fun ivar(){
        val mutation0 = Imm()

        //Test initial values
        assertEquals(initialAString, mutation0.aString())
        assertEquals(initialAnotherString, mutation0.anotherString())

        //first mutation round
        val mutation1 = mutation0.aString{expectedAString}
        assertEquals(expectedAString, mutation1.aString())
        assertEquals(initialAnotherString, mutation1.anotherString())

        //second mutation round
        val mutation2 = mutation1.anotherString{expectedAnotherString}
        assertEquals(expectedAString, mutation2.aString())
        assertEquals(expectedAnotherString, mutation2.anotherString())


        //retest everything

        //Test initial values
        assertEquals(initialAString, mutation0.aString())
        assertEquals(initialAnotherString, mutation0.anotherString())

        //first mutation round
        assertEquals(expectedAString, mutation1.aString())
        assertEquals(initialAnotherString, mutation1.anotherString())

        //second mutation round
        assertEquals(expectedAString, mutation2.aString())
        assertEquals(expectedAnotherString, mutation2.anotherString())

    }
}
/**
 * Copyright © 2019 https://github.com/kburger (burger.github@gmail.com)
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
package com.github.kburger.rdf4j.objectmapper.test.util

import com.github.kburger.rdf4j.objectmapper.annotations.Predicate

class TestUtils {
    public static findField = { Class clazz, String field = "value" -> clazz.declaredFields.find { it.name == field } }
    
    /**
     * Convenience closure for finding {@code annotation} on {@code clazz}.{@code field}.
     * @param clazz target class
     * @param field field name
     * @param annotation annotation class
     * @return the annotation on the target field, or {@code null} if not found.
     */
    public static findAnnotation = { Class clazz, String field = "value", Class annotation = Predicate ->
            TestUtils.findField(clazz, field)?.getAnnotation(annotation)
    }
    
    /**
     * Convenience closure for finding method {@code name} on target {@code clazz}.
     * @param clazz target class
     * @param name method name
     * @return the method on the target class, or {@code null} if not found.
     */
    public static findMethod = { Class clazz, String name = "getValue" -> clazz.methods.find { it.name == name } }
    
    /**
     * Convenience closure for invoking {@link #findMethod}, and returning the result wrapped in an
     * {@link Optional}.
     * @param clazz target class
     * @param name method name
     * @return {@code Optional} containing the method on the target class; or {@link Optional#empty}
     *         if the method could not be found.
     */
    public static findMethodOptional = { Class clazz, String name = "getValue" -> Optional.ofNullable(TestUtils.findMethod(clazz, name)) }
}

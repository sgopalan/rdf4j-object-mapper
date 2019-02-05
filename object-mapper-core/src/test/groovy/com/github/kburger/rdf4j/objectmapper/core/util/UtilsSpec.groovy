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
package com.github.kburger.rdf4j.objectmapper.core.util

import static com.github.kburger.rdf4j.objectmapper.test.util.TestUtils.*
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.RawCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.StringCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.CollectionClasses.WildcardCollectionClass
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass
import spock.lang.Specification

class UtilsSpec extends Specification {
    def "test the system class check"() {
        expect:
        Utils.isSystemClass(target) == result
        
        where:
        target             || result
        Object             || true
        String             || true
        int                || true
        Integer            || true
        int[]              || true
        Integer[]          || true
        Collection         || true
        List               || true
        StringLiteralClass || false
    }
    
    def "test the generic stuff"() {
        expect:
        Utils.resolveGenericTypeArgument(findMethod(type)) == result
        
        where:
        type                    || result
        RawCollectionClass      || Object
        WildcardCollectionClass || Object
        StringCollectionClass   || String
        StringLiteralClass      || String
    }
}

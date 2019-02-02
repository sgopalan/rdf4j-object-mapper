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
package com.github.kburger.rdf4j.objectmapper.core.reader.instance

import static com.github.kburger.rdf4j.objectmapper.test.util.TestUtils.*
import com.github.kburger.rdf4j.objectmapper.api.analysis.PropertyAnalysis
import com.github.kburger.rdf4j.objectmapper.core.util.Utils
import spock.lang.Specification

abstract class BaseInstanceStrategySpec extends Specification {
    /**
     * Convenience closure.
     */
    def prop = { clz, name = "value" -> 
        PropertyAnalysis.builder()
                .annotation(findAnnotation(clz, name))
                .name(name)
                .getter(findMethod(clz, "get${Utils.capitalize(name)}"))
                .setter(findMethod(clz, "set${Utils.capitalize(name)}"))
                .build()
    }
}

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
package com.github.kburger.rdf4j.objectmapper.core.reader.argument

import com.github.kburger.rdf4j.objectmapper.test.util.TestUtils
import spock.lang.Specification

abstract class BaseArgumentStrategySpec extends Specification {
    def create = { Class clz, String name = "value", int n = 1 ->
        def field = TestUtils.findField(clz, name)
        return factory.create(field, n)
    }
}

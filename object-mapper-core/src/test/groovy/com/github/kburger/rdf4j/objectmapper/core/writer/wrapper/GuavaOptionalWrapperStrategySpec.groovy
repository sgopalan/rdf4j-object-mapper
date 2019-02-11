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
package com.github.kburger.rdf4j.objectmapper.core.writer.wrapper

import com.google.common.base.Optional
import spock.lang.Specification

class GuavaOptionalWrapperStrategySpec extends Specification {
    /** Subject under test. */
    def strategy = new GuavaOptionalWrapperStrategy()
    
    def "supports"() {
        expect:
        strategy.supports(obj) == result
        
        where:
        obj     || result
        null    || false
        new Object() || false
        java.util.Optional.of("example") || false
        Optional.absent() || true
        Optional.of("example") || true
    }
    
    def "present"() {
        expect:
        strategy.isPresent(obj) == result
        
        where:
        obj                    || result
        Optional.absent()      || false
        Optional.of("example") || true
    }
    
    def "unwrap empty"() {
        when:
        strategy.unwrap(Optional.absent())
        
        then:
        thrown IllegalStateException
    }
    
    def "unwrap"() {
        expect:
        strategy.unwrap(Optional.of("example")) == "example"
    }
}

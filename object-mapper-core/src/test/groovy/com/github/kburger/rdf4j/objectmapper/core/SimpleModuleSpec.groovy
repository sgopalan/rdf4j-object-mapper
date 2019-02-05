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
package com.github.kburger.rdf4j.objectmapper.core

import org.eclipse.rdf4j.model.Namespace
import com.github.kburger.rdf4j.objectmapper.api.Module.Context
import com.github.kburger.rdf4j.objectmapper.api.reader.ArgumentStrategy
import com.github.kburger.rdf4j.objectmapper.api.reader.InstanceStrategy
import com.github.kburger.rdf4j.objectmapper.api.reader.ValueConverter
import spock.lang.Specification

class SimpleModuleSpec extends Specification {
    /** Subject under test. */
    def module = new SimpleModule()
    
    def "setting up a context with an empty module"() {
        given:
        def context = Mock(Context)
        
        when:
        module.setup(context)
        
        then:
        with (context) {
            0 * _
        }
    }
    
    def "happy flow"() {
        given:
        def context = Mock(Context)
        
        when:
        module.addInstanceStrategy(Mock(InstanceStrategy.Factory))
        and:
        module.addArgumentStrategy(Mock(ArgumentStrategy.Factory))
        and:
        module.addValueConverter(Object, Mock(ValueConverter))
        and:
        module.addNamespace(Mock(Namespace))
        and:
        module.setup(context)
        
        then:
        with (context) {
            1 * registerInstanceStrategy(_)
            1 * registerArgumentStrategy(_)
            1 * registerValueConverter(_, _)
            1 * registerNamespace(_)
        }
    }
}

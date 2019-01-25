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
package com.github.kburger.rdf4j.objectmapper.test;

import com.github.kburger.rdf4j.objectmapper.annotations.Predicate;
import com.github.kburger.rdf4j.objectmapper.test.LiteralClasses.StringLiteralClass;
import lombok.Data;

public class NestingClasses {
    @Data
    public static class NestingStringLiteralClass {
        @Predicate(Constants.NAMESPACE+"nested")
        private StringLiteralClass nested;
    }
    
    @Data
    public static class RecursiveNodeClass {
        @Predicate(Constants.NAMESPACE+"node")
        private RecursiveNodeClass node;
    }
}

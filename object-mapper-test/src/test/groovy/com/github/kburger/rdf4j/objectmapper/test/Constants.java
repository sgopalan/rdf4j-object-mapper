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

public class Constants {
    public static final String NAMESPACE = "http://example.com/";
    public static final String PREFIX = "ex";
    
    public static final String SUBJECT = NAMESPACE + "1";
    
    public static final String TYPE = NAMESPACE + "Type";
    public static final String TYPE_OTHER = NAMESPACE + "Other";
    
    public static final String PREDICATE_VALUE = NAMESPACE + "value";
    public static final String PREDICATE_DESCRIPTION = NAMESPACE + "description";
    
    public static final String RDF_HEADER = "@prefix " + PREFIX + ": <" + NAMESPACE + "> .";
    
    public static final String EXAMPLE_RDF =
            RDF_HEADER + "\n" +
            "ex:1 ex:value \"example\" .\n";
}

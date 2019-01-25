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
package com.github.kburger.rdf4j.objectmapper.core.analysis;

import com.github.kburger.rdf4j.objectmapper.api.exceptions.AbstractMapperException;

/**
 * Exception specific to the analyzer functionality.
 */
@SuppressWarnings("serial")
public class AnalysisException extends AbstractMapperException {
    /**
     * Constructs a new analysis exception with the specified detail message.
     * @param message the detail message.
     */
    public AnalysisException(String message) {
        super(message);
    }
}

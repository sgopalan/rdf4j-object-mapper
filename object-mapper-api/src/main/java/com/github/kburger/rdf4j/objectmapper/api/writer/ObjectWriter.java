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
package com.github.kburger.rdf4j.objectmapper.api.writer;

import org.eclipse.rdf4j.rio.RDFFormat;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ObjectWriterException;
import com.github.kburger.rdf4j.objectmapper.api.exceptions.ValidationException;

/**
 * API for object writers.
 * @param <W> the output sink type.
 */
public interface ObjectWriter<W> {
    /**
     * Writes the given {@code object} to the {@code writer} sink.
     * @param <T> the output source type.
     * @param writer the output sink instance.
     * @param object the output source.
     * @param subject the root subject of the output.
     * @param format the format of the output.
     * @throws ObjectWriterException when something went wrong serializing the object.
     * @throws ValidationException when a validation rule was violated.
     */
    <T> void write(W writer, T object, CharSequence subject, RDFFormat format);
}

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
package com.github.kburger.rdf4j.objectmapper.core;

import java.io.Reader;
import java.io.Writer;
import com.github.kburger.rdf4j.objectmapper.api.AbstractObjectMapper;
import com.github.kburger.rdf4j.objectmapper.api.analysis.ClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.core.analysis.CoreClassAnalyzer;
import com.github.kburger.rdf4j.objectmapper.core.reader.CoreObjectReader;
import com.github.kburger.rdf4j.objectmapper.core.writer.CoreObjectWriter;

public class CoreObjectMapper extends AbstractObjectMapper<Reader, Writer> {
    public CoreObjectMapper() {
        this(new CoreClassAnalyzer());
    }
    
    public CoreObjectMapper(ClassAnalyzer analyzer) {
        super(analyzer, new CoreObjectReader(analyzer), new CoreObjectWriter(analyzer));
    }
}

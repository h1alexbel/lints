/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints.units;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import matchers.DefectMatcher;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnitTestWithoutLiveFile}.
 *
 * @since 0.0.30
 */
final class LtUnitTestWithoutLiveFileTest {

    @Test
    void catchesAbsenceOfLiveFile() {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnitTestWithoutLiveFile().defects(
                new MapOf<String, XML>(
                    new MapEntry<>("abc-test", new XMLDocument("<program/>")),
                    new MapEntry<>("cde", new XMLDocument("<program/>"))
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void reportsProperly() {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtUnitTestWithoutLiveFile().defects(
                new MapOf<String, XML>(
                    new MapEntry<>("xyz-test", new XMLDocument("<program name='xyz-test'/>")),
                    new MapEntry<>("bar", new XMLDocument("<program name='bar'/>"))
                )
            ),
            Matchers.everyItem(new DefectMatcher())
        );
    }

    @Test
    void acceptsValidPackage() {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnitTestWithoutLiveFile().defects(
                new MapOf<String, XML>(
                    new MapEntry<>("foo-test", new XMLDocument("<program name='foo-test'/>")),
                    new MapEntry<>("foo", new XMLDocument("<program name='foo'/>"))
                )
            ),
            Matchers.emptyIterable()
        );
    }
}

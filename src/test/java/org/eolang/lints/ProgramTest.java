/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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
package org.eolang.lints;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.MayBeSlow;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.Together;
import com.yegor256.farea.Farea;
import com.yegor256.tojos.MnCsv;
import com.yegor256.tojos.TjCached;
import com.yegor256.tojos.TjDefault;
import com.yegor256.tojos.Tojos;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.iterable.Sticky;
import org.cactoos.iterable.Synced;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.eolang.parser.TrParsing;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test for {@link Program}.
 *
 * @since 0.0.1
 * @todo #134:45min Measure lint time for smaller/larger classes, similar way to JNA Pointer.
 *  Currently, we measure linting of just one big class. Would be good to measure
 *  other classes in size too, for instance smaller classes (standard program),
 *  large class (JNA pointer), x-large class, and xxl class. Don't forget to
 *  adjust lint-summary.txt file to capture all the measurements.
 */
@ExtendWith(MktmpResolver.class)
final class ProgramTest {

    @Test
    void returnsEmptyListOfDefects() throws IOException {
        MatcherAssert.assertThat(
            "no defects found since the code is clean",
            new Program(
                new EoSyntax(
                    "foo",
                    new InputOf(
                        String.join(
                            "\n",
                            "+home https://www.eolang.org",
                            "+package bar",
                            "+version 0.0.0",
                            "",
                            "# This is just a test object with no functionality.",
                            "[] > foo\n",
                            "  42 > x"
                        )
                    )
                ).parsed()
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @RepeatedTest(2)
    void checksInParallel(@Mktmp final Path dir) throws IOException {
        final Path path = dir.resolve("foo.xmir");
        Files.write(
            path,
            new EoSyntax(
                new InputOf("# first.\n[] > foo\n# second.\n[] > foo\n")
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "",
            new SetOf<>(
                new Together<>(
                    thread -> new Program(path).defects().size()
                )
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    @Timeout(10L)
    void simpleTest(@Mktmp final Path dir) throws IOException {
        final Path path = dir.resolve("foo.xmir");
        Files.write(
            path,
            new EoSyntax(
                new InputOf("# first.\n[] > foo\n# second.\n[] > foo\n")
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "the defect is found",
            new Program(path).defects().size(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void largerBrokenProgramTest() throws IOException {
        MatcherAssert.assertThat(
            "checking passes",
            new Program(
                new EoSyntax(
                    new InputOf(
                        String.join(
                            "\n",
                            "# This is the license",
                            "",
                            "+version 8.8.8-beta",
                            "+alias org.eolang.txt.sprintf",
                            "+alias org . eolang . txt . broken",
                            "+version 1.1-another maybe be wrong",
                            "+package Z.Y.Z",
                            "+home some-wrong-URL",
                            "+architect broken-email-here",
                            "",
                            "# комментарий здесь",
                            "[] > foo-bar",
                            "  (bar 42) > zzz",
                            "  44 > zzz",
                            "",
                            "[] > foo-bar",
                            "",
                            "[] > another",
                            "",
                            "42 > forty-two"
                        )
                    )
                ).parsed()
            ).defects(),
            Matchers.allOf(
                Matchers.iterableWithSize(Matchers.greaterThan(0)),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.containsString(
                            "[alias-too-long ERROR]:5 The alias has too many parts"
                        )
                    )
                )
            )
        );
    }

    @Test
    @Timeout(10L)
    void acceptsCanonicalCode() throws IOException {
        final XML xmir = new Xsline(new TrParsing()).pass(
            new EoSyntax(
                new ResourceOf(
                    "org/eolang/lints/canonical.eo"
                )
            ).parsed()
        );
        MatcherAssert.assertThat(
            String.format("no errors in canonical code in %s", xmir),
            new Program(xmir).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void doesNotThrowIoException() {
        Assertions.assertDoesNotThrow(
            () ->
                new Program(
                    new XMLDocument("<program/>")
                ).defects(),
            "Exception was thrown, but it should not be"
        );
    }

    @Test
    @Tag("benchmark")
    @ExtendWith(MktmpResolver.class)
    @ExtendWith(MayBeSlow.class)
    @Timeout(600L)
    void lintsLargeJnaClass(@Mktmp final Path home) throws Exception {
        final String path = "com/sun/jna/Pointer.class";
        final Path bin = Paths.get("target")
            .resolve("jna-classes")
            .resolve(path);
        new Farea(home).together(
            f -> {
                f.clean();
                f.files()
                    .file(String.format("target/classes/%s", path))
                    .write(Files.readAllBytes(bin));
                f.build()
                    .plugins()
                    .append("org.eolang", "jeo-maven-plugin", "0.6.26")
                    .execution("default")
                    .phase("process-classes")
                    .goals("disassemble");
                f.exec("process-classes");
                final Path pre = f.files().file(
                    "target/generated-sources/jeo-xmir/com/sun/jna/Pointer.xmir"
                ).path();
                final XML xmir = new XMLDocument(pre);
                final long start = System.currentTimeMillis();
                final Collection<Defect> defects = new ProgramTest.BcProgram(xmir).defects();
                final long msec = System.currentTimeMillis() - start;
                final Path target = Paths.get("target");
                Files.write(
                    target.resolve("lint-summary.txt"),
                    String.join(
                        "\n",
                        String.format("Input: %s", path),
                        Logger.format(
                            "Size of .class: %[size]s (%1$s bytes)",
                            bin.toFile().length()
                        ),
                        Logger.format(
                            "Size of .xmir after disassemble: %[size]s (%1$s bytes, %d lines)",
                            pre.toFile().length(),
                            Files.readString(pre, StandardCharsets.UTF_8).split("\n").length
                        ),
                        Logger.format(
                            "Lint time: %[ms]s (%d ms)",
                            msec, msec
                        )
                    ).getBytes(StandardCharsets.UTF_8)
                );
                MatcherAssert.assertThat(
                    "Defects are empty, but they should not be",
                    defects,
                    Matchers.hasSize(Matchers.greaterThan(0))
                );
            }
        );
    }

    /**
     * Benchmarked program.
     * @since 0.0.29
     */
    private static final class BcProgram {

        /**
         * XMIR.
         */
        private final XML xmir;

        /**
         * Lints to apply.
         */
        private final Iterable<Lint<XML>> lints;

        /**
         * Timings.
         */
        private final Tojos timings;

        /**
         * Ctor.
         * @param program XMIR program to lint
         */
        BcProgram(final XML program) {
            this(
                program,
                new Synced<>(new Sticky<>(new PkMono())),
                new TjCached(
                    new TjDefault(
                        new MnCsv("target/timings.csv")
                    )
                )
            );
        }

        /**
         * Ctor.
         * @param program XMIR program to lint
         * @param lnts Lints to apply
         * @param tmngs Timings
         */
        BcProgram(final XML program, final Iterable<Lint<XML>> lnts, final Tojos tmngs) {
            this.xmir = program;
            this.lints = lnts;
            this.timings = tmngs;
        }

        /**
         * Defects.
         * @return Defects
         * @todo #144:35min Resolve code duplication with Program.java class.
         *  Currently, BcProgram.java is duplication of Program.java. Let's make
         *  it use the original Program.java, so they will stay synced. Don't forget
         *  to remove this puzzle.
         */
        public Collection<Defect> defects() {
            try {
                final Collection<Defect> messages = new ArrayList<>(0);
                for (final Lint<XML> lint : this.lints) {
                    final long start = System.currentTimeMillis();
                    final Collection<Defect> defects = lint.defects(this.xmir);
                    final long done = System.currentTimeMillis() - start;
                    this.timings.add(lint.name()).set("ms", done);
                    messages.addAll(defects);
                }
                return messages;
            } catch (final IOException ex) {
                throw new IllegalStateException(
                    "Failed to find defects in the XMIR file",
                    ex
                );
            }
        }
    }
}

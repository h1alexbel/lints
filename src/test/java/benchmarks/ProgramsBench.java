/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import fixtures.BytecodeClass;
import fixtures.ProgramSize;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.cactoos.scalar.IoChecked;
import org.eolang.lints.Program;
import org.eolang.lints.Programs;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for {@link Program}.
 *
 * @since 0.0.34
 * @checkstyle DesignForExtensionCheck (10 lines)
 * @checkstyle NonStaticMethodCheck (100 lines)
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ProgramsBench {

    /**
     * Large XMIR document.
     */
    private final Path home;

    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public ProgramsBench() {
        try {
            this.home = Files.createTempDirectory("tmp");
            for (int idx = 0; idx < 10; ++idx) {
                final String name = String.format("program-%d.xmir", idx);
                Files.write(
                    this.home.resolve(String.format("%s.xmir", name)),
                    new IoChecked<>(new BytecodeClass(name, ProgramSize.L.java()))
                        .value().toString().getBytes(StandardCharsets.UTF_8)
                );
            }
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Benchmark
    public final void scansLargeProgram() throws IOException {
        new Programs(this.home).defects();
    }
}

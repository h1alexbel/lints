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
package benchmarks;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import fixtures.LargeXmir;
import java.util.concurrent.TimeUnit;
import org.cactoos.scalar.Unchecked;
import org.eolang.lints.Program;
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
public class ProgramBench {

    /**
     * Large XMIR document.
     */
    private static final XML LARGE = new Unchecked<>(new LargeXmir()).value();

    /**
     * Small XMIR document.
     */
    private static final XML SMALL = new XMLDocument("<program name='foo'/>");

    @Benchmark
    public final void scansLargeXmir() {
        new Program(ProgramBench.LARGE).defects();
    }

    @Benchmark
    public final void scansSmallXmir() {
        new Program(ProgramBench.SMALL).defects();
    }
}

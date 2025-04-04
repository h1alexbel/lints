/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.manifests.Manifests;
import java.util.Objects;

/**
 * A single defect found.
 * <p>
 * Defect is a node in the XMIR under `/program/defects`, that describes the
 * issue with EO/XMIR source code. Defect contains the message that addresses
 * source code and points to the problem in it. Some defects report the problems
 * on XMIR format itself, consider checking resources on XMIR in order to get
 * understanding how intermediate representation of EO is structured in XML format.
 * </p>
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR guide</a>
 * @see <a href="https://www.eolang.org/XMIR.html">XMIR specification</a>
 * @see <a href="https://www.eolang.org/XMIR.xsd">XMIR schema</a>
 * @since 0.0.1
 */
public interface Defect {

    /**
     * Rule name.
     * <p>
     * Returns the unique identifier of the rule that detected this defect.
     * </p>
     *
     * @return Unique name of the rule
     */
    String rule();

    /**
     * Severity level.
     * <p>
     * Returns the severity level of this defect.
     * </p>
     *
     * @return Severity of the defect
     */
    Severity severity();

    /**
     * Name of the program with defect.
     * <p>
     * Returns the name of the program where the defect was found.
     * </p>
     *
     * @return Name of it, taken from the {@code @name} attribute of
     *  the {@code program} element in XMIR
     */
    String program();

    /**
     * Line where the defect was found.
     * <p>
     * Returns the line number in the source code where the defect was detected.
     * </p>
     *
     * @return Line number in the source code
     */
    int line();

    /**
     * Error message.
     * <p>
     * Returns the descriptive message explaining the defect.
     * </p>
     *
     * @return Text of the error message
     */
    String text();

    /**
     * The linter's current version.
     * <p>
     * Returns the version of the linting tool that detected this defect.
     * </p>
     *
     * @return Linter's current version string
     */
    String version();

    /**
     * Defect context.
     * <p>
     * Returns additional contextual information about the defect,
     * which may help understand and fix the issue.
     * </p>
     *
     * @return Context of the defect as a string
     */
    String context();

    /**
     * Default implementation of {@link Defect}.
     * <p>
     * Provides a standard implementation with basic functionality.
     * </p>
     *
     * @since 0.0.1
     */
    final class Default implements Defect {
        /**
         * Rule.
         */
        private final String rle;

        /**
         * Severity.
         */
        private final Severity sev;

        /**
         * Name of the program.
         */
        private final String prg;

        /**
         * Line number with the defect.
         */
        private final int lineno;

        /**
         * The message of the problem.
         */
        private final String txt;

        /**
         * Ctor.
         * <p>
         * Constructs a defect with all required information.
         * </p>
         *
         * @param rule Rule name
         * @param severity Severity level
         * @param program Name of the program
         * @param line Line number
         * @param text Description of the defect
         * @checkstyle ParameterNumberCheck (5 lines)
         */
        public Default(
            final String rule, final Severity severity,
            final String program, final int line, final String text
        ) {
            this.rle = rule;
            this.sev = severity;
            this.prg = program;
            this.lineno = line;
            this.txt = text;
        }

        @Override
        public String toString() {
            final StringBuilder text = new StringBuilder(0)
                .append('[').append(this.prg).append(' ')
                .append(this.rle).append(' ')
                .append(this.sev).append(']');
            if (this.lineno > 0) {
                text.append(':').append(this.lineno);
            }
            return text.append(' ').append(this.txt).toString();
        }

        @Override
        public String rule() {
            return this.rle;
        }

        @Override
        public Severity severity() {
            return this.sev;
        }

        @Override
        public String program() {
            return this.prg;
        }

        @Override
        public int line() {
            return this.lineno;
        }

        @Override
        public String text() {
            return this.txt;
        }

        @Override
        public String version() {
            return Manifests.read("Lints-Version");
        }

        @Override
        public String context() {
            return "Context is empty";
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean result;
            if (obj == null || this.getClass() != obj.getClass()) {
                result = false;
            } else {
                final Defect.Default defect = (Defect.Default) obj;
                result = this.lineno == defect.lineno
                    && Objects.equals(this.rle, defect.rle)
                    && this.sev == defect.sev
                    && Objects.equals(this.prg, defect.prg)
                    && Objects.equals(this.txt, defect.txt);
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.rle, this.sev, this.prg, this.lineno, this.txt);
        }
    }

}

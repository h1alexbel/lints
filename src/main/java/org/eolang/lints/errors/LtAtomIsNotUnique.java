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
package org.eolang.lints.errors;

import com.jcabi.xml.XML;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * All FQNs that have `@atom` in the entire scope must be unique.
 * This lint already expects the presence of `@fqn` attribute for each atom `o`
 * in XMIR.
 *
 * @since 0.0.31
 */
public final class LtAtomIsNotUnique implements Lint<Map<String, XML>> {

    /**
     * Stylesheet for adding `@fqn` attribute for atoms.
     */
    private final XSL fqns;

    /**
     * Ctor.
     */
    public LtAtomIsNotUnique() {
        this(
            new XSLDocument(
                new UncheckedInput(
                    new ResourceOf("org/eolang/funcs/atom-fqns.xsl")
                ).stream()
            )
        );
    }

    /**
     * Ctor.
     *
     * @param sheet Sheet
     */
    public LtAtomIsNotUnique(final XSL sheet) {
        this.fqns = sheet;
    }

    @Override
    public String name() {
        return "atom-is-not-unique";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<XML, List<String>> index = pkg.values().stream()
            .map(this.fqns::transform)
            .collect(Collectors.toMap(Function.identity(), LtAtomIsNotUnique::fqns));
        final Collection<String> checked = new HashSet<>(0);
        index.forEach(
            (xmir, fqns) -> {
                fqns.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1L)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    .forEach(
                        (aname, count) ->
                            IntStream.range(0, Math.toIntExact(count)).forEach(
                                pos -> defects.add(this.singleDefect(xmir, aname, pos))
                            )
                    );
                index.forEach(
                    (next, names) -> {
                        if (!Objects.equals(next, xmir)) {
                            final String pair = LtAtomIsNotUnique.pairHash(xmir, next);
                            if (!checked.contains(pair)) {
                                checked.add(pair);
                                names.stream()
                                    .filter(fqns::contains)
                                    .forEach(
                                        aname -> {
                                            defects.add(this.sharedDefect(next, xmir, aname));
                                            defects.add(this.sharedDefect(xmir, next, aname));
                                        }
                                    );
                            }
                        }
                    }
                );
            }
        );
        return defects;
    }

    @Override
    public String motive() {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/errors/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    private Defect singleDefect(final XML xmir, final String fqn, final int pos) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            xmir.xpath("/program/@name").stream()
                .findFirst().orElse("unknown"),
            Integer.parseInt(
                xmir.xpath(
                    String.format(
                        "//o[@atom and @name='%s']/@line",
                        LtAtomIsNotUnique.oname(fqn)
                    )
                ).get(pos)
            ),
            String.format("Atom '%s' is duplicated", fqn)
        );
    }

    private Defect sharedDefect(final XML xmir, final XML original, final String fqn) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
            Integer.parseInt(
                xmir.xpath(
                    String.format(
                        "//o[@atom and @name='%s']/@line",
                        LtAtomIsNotUnique.oname(fqn)
                    )
                ).get(0)
            ),
            String.format(
                "Atom with FQN '%s' is duplicated, original was found in '%s'",
                fqn, original.xpath("/program/@name").stream().findFirst().orElse("unknown")
            )
        );
    }

    private static List<String> fqns(final XML xmir) {
        return new ListOf<>(xmir.xpath("//o[@fqn]/@fqn"));
    }

    private static String oname(final String fqn) {
        final String result;
        final List<String> parts = new ListOf<>(fqn.split("\\."));
        if (parts.size() > 1) {
            result = parts.get(parts.size() - 1);
        } else {
            result = parts.get(0);
        }
        return result;
    }

    private static String pairHash(final XML first, final XML second) {
        final String pair;
        if (first.hashCode() < second.hashCode()) {
            pair = String.format("%d:%d", first.hashCode(), second.hashCode());
        } else {
            pair = String.format("%d:%d", second.hashCode(), first.hashCode());
        }
        return pair;
    }
}

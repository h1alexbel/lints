/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtUnlint implements Lint<XML> {

    /**
     * Line number to unlint.
     */
    private static final Pattern LINE_NUMBER = Pattern.compile(".*:\\d+$");

    /**
     * The original lint.
     */
    private final Lint<XML> origin;

    /**
     * Ctor.
     * @param lint The lint to decorate
     */
    LtUnlint(final Lint<XML> lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new ArrayList<>(0);
        final String lname = this.origin.name();
        final Collection<Defect> found = this.origin.defects(xmir);
        final List<Integer> problematic = found.stream().
            filter(defect -> defect.rule().equals(lname))
            .map(Defect::line)
            .collect(Collectors.toList());
        final List<String> granular = xmir.xpath(
            String.format(
                "/program/metas/meta[head='unlint' and (tail='%s' or starts-with(tail, '%s:'))]/tail/text()",
                lname, lname
            )
        );
        final boolean global = !granular.isEmpty();
        granular.forEach(
            unlint -> {
                if (LtUnlint.LINE_NUMBER.matcher(unlint).matches()) {
                    final List<String> split = new ListOf<>(unlint.split(":"));
                    final int lineno = Integer.parseInt(
                        split.get(1)
                    );
                    problematic.removeIf(line -> line == lineno);
                }
            }
        );
        problematic.forEach(
            line -> found.forEach(
                defect -> {
                    if (line != 0 && defect.line() == line) {
                        defects.add(defect);
                    }
                }
            )
        );
        if (!global) {
            defects.addAll(this.origin.defects(xmir));
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MonoLints}.
 *
 * @since 0.0.43
 */
final class MonoLintsTest {

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("MonoLints")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }

    @Test
    void createsLintsWithIncorrectUnlintRule() {
        MatcherAssert.assertThat(
            "Lint `incorrect-unlint` is not present in the list, but it should be",
            new ListOf<>(new MonoLints()).stream().map(Lint::name).collect(Collectors.toList()),
            Matchers.hasItem(
                "incorrect-unlint"
            )
        );
    }

    @Test
    void lintsProgramCorrectly() throws IOException {
        final XML xmir = new EoSyntax(
            "app",
            String.join(
                "\n",
                "+home https://github.com/objectionary/eo",
                "+package f",
                "+version 0.0.0",
                "",
                "# No comments.",
                "[] > main",
                "  QQ.io.stdout",
                "    \"Hello world\""
            )
        ).parsed();
        Collection<Defect> found = new ListOf<>();
        new ListOf<>(new MonoLints()).forEach(
            lint -> {
                try {
                    found.addAll(lint.defects(xmir));
                } catch (final IOException exception) {
                    throw new IllegalStateException("Failed to lint XMIR", exception);
                }
            }
        );
        // check no duplicates by name
        MatcherAssert.assertThat(
            "Found defects size does not match with expected",
            found,
            Matchers.hasSize(1)
        );
    }
}

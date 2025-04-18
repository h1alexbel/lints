/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link WithoutLints}.
 * @since 0.0.46
 */
final class WithoutLintsTest {

    @ParameterizedTest
    @ValueSource(
        strings = {"unit-test-missing", "unit-test-without-live-file"}
    )
    void excludesLints(final String lid) {
        MatcherAssert.assertThat(
            String.format(
                "Lint with name '%s' was not excluded, but it should",
                lid
            ),
            new WithoutLints<>(
                new ListOf<>(
                    new LtUnitTestMissing(),
                    new LtUnitTestWithoutLiveFile()
                ),
                new ListOf<>(lid)
            ),
            Matchers.iterableWithSize(1)
        );
    }
}

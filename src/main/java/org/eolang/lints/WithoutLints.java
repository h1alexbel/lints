/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.Collection;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.IterableEnvelope;

/**
 * Lints without some lints.
 * @since 0.0.46
 */
final class WithoutLints<X extends Lint<?>> extends IterableEnvelope<X> {
    /**
     * Ctor.
     *
     * @param origin Origin lints
     * @param names Lint names to exclude
     */
    WithoutLints(final Iterable<X> origin, final Collection<String> names) {
        super(
            new Filtered<>(
                origin,
                lint -> () -> !names.contains(lint.name())
            )
        );
    }
}

/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package matchers;

import com.jcabi.xml.XML;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Hamcrest matcher to check story of {@link org.eolang.lints.WpaStory}.
 *
 * @since 0.0.43
 */
public final class WpaStoryMatcher extends BaseMatcher<Map<List<String>, XML>> {

    /**
     * Summary.
     */
    private String summary;

    @Override
    public boolean matches(final Object input) {
        final boolean match;
        if (input instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<List<String>, XML> story = (Map<List<String>, XML>) input;
            final List<String> xpaths = story.keySet().iterator().next();
            final XML defects = story.get(xpaths);
            final List<String> failures = new LinkedList<>();
            xpaths.forEach(
                xpath -> {
                    if (defects.nodes(xpath).isEmpty()) {
                        failures.add(xpath);
                    }
                }
            );
            if (failures.isEmpty()) {
                match = true;
            } else {
                match = false;
                final StringBuilder sum = new StringBuilder(1024)
                    .append(String.format("%d XPath expression(s) failed:\n", failures.size()));
                failures.forEach(
                    f -> sum.append('\n').append(String.format("FAIL: %s", f))
                );
                sum.append("\n\nFound defects:\n").append(defects);
                this.summary = sum.toString();
            }
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("story without failed asserts");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void describeMismatch(final Object input, final Description description) {
        description.appendText("\n").appendText(this.summary);
    }
}

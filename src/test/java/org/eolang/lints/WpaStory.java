/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;
import org.yaml.snakeyaml.Yaml;

/**
 * Test story for WPA.
 *
 * @since 0.0.43
 */
final class WpaStory {

    /**
     * Yaml.
     */
    private final String yaml;

    /**
     * WPA lints.
     */
    private final Map<String, Lint<Map<String, XML>>> wpa;

    /**
     * Ctor.
     *
     * @param yml  Yaml to load
     * @param lnts Lints
     */
    WpaStory(final String yml, final Map<String, Lint<Map<String, XML>>> lnts) {
        this.yaml = yml;
        this.wpa = lnts;
    }

    /**
     * Execute it.
     * @return Map of XPaths and found Defects as XML
     * @throws IOException if I/O fails
     */
    @SuppressWarnings("unchecked")
    public Map<List<String>, XML> execute() throws IOException {
        final Map<String, Object> loaded = new Yaml().load(this.yaml);
        final Map<String, XML> programs = new HashMap<>(0);
        loaded.forEach(
            (key, val) -> {
                if (key.endsWith(".eo")) {
                    try {
                        final String name = key.substring(0, key.length() - 3);
                        programs.put(name, new EoSyntax(name, (String) val).parsed());
                    } catch (final IOException exception) {
                        throw new IllegalStateException(
                            "Failed to parse EO syntax", exception
                        );
                    }
                }
            }
        );
        Object lints = loaded.get("lints");
        if (lints == null) {
            lints = List.of();
        }
        Object xpaths = loaded.get("asserts");
        if (xpaths == null) {
            xpaths = List.of();
        }
        final Collection<Defect> found = new LinkedList<>();
        for (final String lint : (Iterable<String>) lints) {
            final Lint<Map<String, XML>> wpl = this.wpa.get(lint);
            found.addAll(wpl.defects(programs));
        }
        return new MapOf<>(
            new MapEntry<>(
                (List<String>) xpaths, WpaStory.defectsAsXml(found)
            )
        );
    }

    private static XML defectsAsXml(final Collection<Defect> defects) {
        final Directives directives = new Directives().add("defects");
        defects.forEach(
            d -> directives.add("defect")
                .attr("program", d.program())
                .attr("line", d.line())
                .attr("severity", d.severity().toString().toLowerCase(Locale.ROOT))
                .attr("context", d.context())
                .set(d.text())
                .up()
        );
        try {
            return new XMLDocument(new Xembler(directives).xml());
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                "Failed to create XML document from defects", exception
            );
        }
    }
}

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
package fixtures;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.farea.Farea;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.Scalar;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Large XMIR document.
 *
 * @since 0.0.31
 */
public final class LargeXmir implements Scalar<XML> {

    /**
     * Name of the program.
     */
    private final String name;

    /**
     * Constructor.
     */
    public LargeXmir() {
        this("unknown");
    }

    /**
     * Constructor.
     * @param nme Program name.
     */
    public LargeXmir(final String nme) {
        this.name = nme;
    }

    @Override
    public XML value() throws Exception {
        final Path home = Files.createTempDirectory("tmp");
        final String path = "com/sun/jna/Pointer.class";
        final AtomicReference<XML> ref = new AtomicReference<>();
        new Farea(home).together(
            f -> {
                f.clean();
                f.files()
                    .file(String.format("target/classes/%s", path))
                    .write(
                        new UncheckedBytes(
                            new BytesOf(
                                new ResourceOf(
                                    "com/sun/jna/Pointer.class"
                                )
                            )
                        ).asBytes()
                    );
                f.build()
                    .plugins()
                    .append("org.eolang", "jeo-maven-plugin", "0.7.2")
                    .execution("default")
                    .phase("process-classes")
                    .goals("disassemble");
                f.exec("process-classes");
                ref.set(
                    new XMLDocument(
                        f.files().file(
                            "target/generated-sources/jeo-xmir/com/sun/jna/Pointer.xmir"
                        ).path()
                    )
                );
            }
        );
        final XML xml = ref.get();
        new Xembler(
            new Directives().xpath("/program").attr("name", this.name)
        ).apply(xml.inner());
        return xml;
    }
}

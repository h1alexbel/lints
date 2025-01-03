<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2024 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="unique-metas" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8"/>
  <xsl:variable name="unique" select="('version', 'architect', 'home', 'package')"/>
  <xsl:variable name="metas" select="/program/metas/meta"/>
  <xsl:variable name="heads" select="/program/metas/meta/head"/>
  <xsl:template match="/">
    <defects>
      <xsl:for-each select="$unique">
        <xsl:variable name="head" select="current()"/>
        <xsl:if test="count($heads[text() = $head])&gt;1">
          <xsl:element name="defect">
            <xsl:attribute name="line">
              <xsl:value-of select="eo:lineno($metas[head = $head][2]/@line)"/>
            </xsl:attribute>
            <xsl:attribute name="severity">
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>There are more than one "+</xsl:text>
            <xsl:value-of select="$head"/>
            <xsl:text>" meta specified</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>
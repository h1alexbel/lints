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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:math="http://www.w3.org/2005/xpath-functions/math" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="wrong-sprintf-arguments">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:function name="eo:hex-to-placeholder" as="xs:integer">
    <xsl:param name="hex" as="xs:string"/>
    <xsl:variable name="hex-upper" select="upper-case($hex)"/>
    <xsl:variable name="length" select="string-length($hex-upper)"/>
    <xsl:variable name="decimal" select="sum(for $i in 1 to $length return (index-of(string-to-codepoints('0123456789ABCDEF'), string-to-codepoints(substring($hex-upper, $i, 1))) - 1) * xs:integer(math:pow(16, $length - $i)))"/>
    <xsl:sequence select="$decimal"/>
  </xsl:function>
  <xsl:variable name="sprintf" select="//o[@base='.sprintf'][o[@base='.txt']/o[@base='QQ']]"/>
  <xsl:variable name="placeholder">
    <xsl:for-each select="tokenize($sprintf, '-')">
      <xsl:value-of select="codepoints-to-string(eo:hex-to-placeholder(.))"/>
    </xsl:for-each>
  </xsl:variable>
  <xsl:variable name="declared" select="count(matches($placeholder, '%s')) + count(matches($placeholder, '%d'))"/>
  <xsl:variable name="tupled" select="//o[@base='.sprintf']/o[@base='tuple']/o[not(@base='.empty')]"/>
  <xsl:variable name="nested">
    <xsl:for-each select="$tupled">
      <xsl:call-template name="nested-args">
        <xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:variable>
  <xsl:template name="nested-args">
    <xsl:param name="node"/>
    <xsl:for-each select="$node/o[not(@base='tuple') and not(@base='.empty')]">
      <xsl:value-of select="@base"/>
      <xsl:text> </xsl:text>
    </xsl:for-each>
    <xsl:for-each select="$node/o[@base='tuple']">
      <xsl:call-template name="nested-args">
        <xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>
  <xsl:variable name="used" select="count($tupled[not(@base='tuple')]/@base) + count(tokenize(substring($nested, 1, string-length($nested) - 1), '\s+'))"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="$sprintf != '' and $declared != $used">
        <defect>
          <xsl:attribute name="line">
            <xsl:value-of select="eo:lineno($sprintf/@line)"/>
          </xsl:attribute>
          <xsl:attribute name="severity">
            <xsl:text>warning</xsl:text>
          </xsl:attribute>
          <xsl:text>The sprintf object has wrong number of arguments: </xsl:text>
          <xsl:value-of select="$declared"/>
          <xsl:text> in the placeholder, but </xsl:text>
          <xsl:value-of select="$used"/>
          <xsl:text> are passed</xsl:text>
        </defect>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>
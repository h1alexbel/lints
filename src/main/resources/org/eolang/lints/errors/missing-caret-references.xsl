<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="missing-caret-references" version="2.0">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <!-- starts-with(@base, '$.^') -->
      <xsl:for-each select="//o[@base and starts-with(@base, '$.^')]">
        <xsl:variable name="carets">
          <xsl:analyze-string select="@base" regex="\^\." flags="m">
            <xsl:matching-substring>
              <xsl:value-of select="'1'"/>
            </xsl:matching-substring>
          </xsl:analyze-string>
        </xsl:variable>
        <xsl:variable name="uncareted" select="translate(translate(@base, '.^', ''), '$', '')"/>
        <xsl:variable name="owner" select="../.."/>
        <xsl:if test="$owner/o[not(@name=$uncareted)]">
          <xsl:element name="defect">
            <xsl:variable name="line" select="eo:lineno(@line)"/>
            <xsl:attribute name="line">
              <xsl:value-of select="$line"/>
            </xsl:attribute>
            <xsl:if test="$line = '0'">
              <xsl:attribute name="context">
                <xsl:value-of select="eo:defect-context(.)"/>
              </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="severity">
              <xsl:text>error</xsl:text>
            </xsl:attribute>
            <xsl:text>The caret reference "</xsl:text>
            <xsl:value-of select="$uncareted"/>
            <xsl:text>" </xsl:text>
            <xsl:text>is missing in "</xsl:text>
            <xsl:value-of select="$owner/@name"/>
            <xsl:text>" object</xsl:text>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>

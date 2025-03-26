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
      <xsl:for-each select="//o[@base and starts-with(@base, '$.^')]">
        <xsl:variable name="uncareted" select="translate(translate(@base, '.^', ''), '$', '')"/>
        <xsl:variable name="position" select="(count(tokenize(@base, '\^')) - 1) * 2"/>
        <!-- if $current has auto named objects -> move up -->
        <xsl:variable name="owner" select="ancestor::node()[$position]"/>
        <xsl:choose>
<!--          <xsl:when test="contains($owner/o[1]/@name, 'a🌵')">-->
          <xsl:when test="not(empty($owner/o[starts-with(@name, 'a🌵')]))">
            <!-- up to parent relatively to $owner -->
            <xsl:variable name="upper" select="ancestor::node()[$position + 1]"/>
            <xsl:if test="not($upper/o[@name=$uncareted])">
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
                <xsl:value-of select="$upper/@name"/>
                <xsl:text>" object</xsl:text>
              </xsl:element>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="not($owner/o[@name=$uncareted])">
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
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </defects>
  </xsl:template>
</xsl:stylesheet>

<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:eo="https://www.eolang.org" id="sparse-decoration" version="2.0">
  <xsl:import href="/org/eolang/parser/_funcs.xsl"/>
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:import href="/org/eolang/funcs/defect-context.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:if test="not(/program[metas/meta[head='tests']])">
        <xsl:for-each select="//o[eo:abstract(.) and count(o)=1 and o[1][@name='@']]">
          <xsl:variable name="link" select="@line"/>
          <xsl:if test="not(//comment[@line=$link]/text() and ..[@name])">
            <xsl:element name="defect">
              <xsl:variable name="line" select="eo:lineno($link)"/>
              <xsl:attribute name="line">
                <xsl:value-of select="$line"/>
              </xsl:attribute>
              <xsl:if test="$line = '0'">
                <xsl:attribute name="context">
                  <xsl:value-of select="eo:defect-context(.)"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="severity">
                <xsl:text>warning</xsl:text>
              </xsl:attribute>
              <xsl:text>Sparse decoration is prohibited</xsl:text>
            </xsl:element>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>
    </defects>
  </xsl:template>
</xsl:stylesheet>

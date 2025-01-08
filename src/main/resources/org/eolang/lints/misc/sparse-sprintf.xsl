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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:math="http://www.w3.org/2005/xpath-functions/math" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eo="https://www.eolang.org" version="2.0" id="one-high-level-object">
  <xsl:import href="/org/eolang/funcs/lineno.xsl"/>
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:variable name="sprintf" select="/program/objects//o[@base = '.sprintf']"/>
  <xsl:variable name="tupled" select="//o[@base='.sprintf']/o[@base='tuple']/o[2]/@base"/>
  <!--<objects>
      <o line="2" name="app" pos="0">
         <o base=".stdout" line="3" name="@" pos="7">
            <o base=".io" line="3" pos="4">
               <o base="QQ" line="3" pos="2"/>
            </o>
            <o base=".sprintf" line="4" pos="10">
               <o base=".txt" line="4" pos="6">
                  <o base="QQ" line="4" pos="4"/>
               </o>
               <o base="string" line="5" pos="6">48-65-6C-6C-6F-2C-20-25-73-21-20-59-6F-75-72-20-61-63-63-6F-75-6E-74-20-69-73-20-25-64-2E</o>
               <o base="tuple" line="6" pos="6">
                  <o base="tuple">
                     <o base=".empty">
                        <o base="tuple"/>
                     </o>
                     <o base="name" line="6" pos="8">
                  </o>
                  <o base="acc" line="6" pos="13"/>
               </o>
            </o>
         </o>
      </o>
  </objects> -->
   <xsl:function name="eo:convert-hex-to-decimal" as="xs:integer">
    <xsl:param name="hex" as="xs:string"/>
    <xsl:variable name="hex-upper" select="upper-case($hex)"/>
    <xsl:variable name="length" select="string-length($hex-upper)"/>
    <xsl:variable name="decimal" select="
      sum(
        for $i in 1 to $length
        return (index-of(string-to-codepoints('0123456789ABCDEF'), 
                         string-to-codepoints(substring($hex-upper, $i, 1))) - 1)
               * xs:integer(math:pow(16, $length - $i))
      )
    "/>
    <xsl:sequence select="$decimal"/>
  </xsl:function>
  <xsl:template match="/">
    <xsl:variable name="placeholder">
      <xsl:for-each select="tokenize($sprintf, '-')">
        <xsl:value-of select="codepoints-to-string(eo:convert-hex-to-decimal(.))"/>
      </xsl:for-each>
    </xsl:variable>
    <defects>
      <xsl:value-of select="$tupled"/>
        <!--<xsl:value-of select="count(matches($placeholder, '%s')) + count(matches($placeholder, '%d'))"/>-->
    </defects>
  </xsl:template>
</xsl:stylesheet>


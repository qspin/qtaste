<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:db="http://docbook.org/ns/docbook"
                version="1.0">
<xsl:import href="urn:docbkx:stylesheet"/>

<xsl:param name="section.autolabel" select="1"/>
<xsl:param name="section.label.includes.component.label" select="1"/>

<xsl:attribute-set name="component.title.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>
<xsl:attribute-set name="section.title.level1.properties"
                   use-attribute-sets="section.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>
<xsl:attribute-set name="section.title.level2.properties"
                   use-attribute-sets="section.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>
<xsl:attribute-set name="section.title.level3.properties"
                   use-attribute-sets="section.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>
<xsl:attribute-set name="section.title.level4.properties"
                   use-attribute-sets="section.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>
<xsl:attribute-set name="section.title.level5.properties"
                   use-attribute-sets="section.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>
<xsl:attribute-set name="section.title.level6.properties"
                   use-attribute-sets="section.properties">
  <xsl:attribute name="color">#01ABCE</xsl:attribute>
</xsl:attribute-set>

<!-- Defining Titles Font Color -->
<xsl:template match="db:title/db:emphasis">
  <fo:inline color="#01ABCE">
     <xsl:apply-templates/>
  </fo:inline>
</xsl:template>

<!-- Defining Backgroung Color for lines of code examples -->
<xsl:template match="db:programlisting/db:emphasis">
  <fo:inline background-color="#999999">
     <xsl:apply-templates/>
  </fo:inline>
</xsl:template>
<xsl:template match="db:screen/db:para">
  <fo:inline background-color="#999999">
     <xsl:apply-templates/>
  </fo:inline>
</xsl:template>

<xsl:template match="db:ulink">
  <fo:basic-link external-destination="{@url}"
        xsl:use-attribute-sets="xref.properties"
        text-decoration="underline"
        color="blue">
    <xsl:choose>
        <xsl:when test="count(child::node())=0">
            <xsl:value-of select="@url"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates/>
        </xsl:otherwise>
    </xsl:choose>
  </fo:basic-link>
</xsl:template>

<!-- Defining Margins -->
<xsl:param name="page.margin.inner">0.50in</xsl:param>
<xsl:param name="page.margin.outer">0.50in</xsl:param>
<xsl:param name="body.margin.bottom">1.35in</xsl:param>
<xsl:param name="region.after.extent">1.35in</xsl:param>
<xsl:param name="page.margin.bottom">0.15in</xsl:param>
<xsl:param name="footer.column.widths">1 3 0</xsl:param>

<!-- Defining Footer text and graphics -->
<xsl:template name="footer.content">
  <xsl:param name="pageclass" select="''"/>
  <xsl:param name="sequence" select="''"/>
  <xsl:param name="position" select="''"/>

  <fo:block>
    <xsl:choose>
      <!-- Add copyrights and current page number -->
      <xsl:when test="$position = 'center'">
        <fo:block font-size="9pt" text-align="left">
          <xsl:text>&#x00A9; 2014 QSpin SA</xsl:text>
        </fo:block>
        <fo:block> </fo:block>
        <fo:block font-size="8pt" text-align="left">
          <xsl:text>All rights are reserved.</xsl:text>
        </fo:block>
        <fo:block font-size="8pt" text-align="left">
          <xsl:text>Reproduction in whole or in part is prohibited without the written consent of the copyright onwer.</xsl:text>
        </fo:block>
        <fo:block text-align="right">
          <fo:page-number/>
        </fo:block>
      </xsl:when>
      <!-- Add QSpin Logo Figure-->
      <xsl:when test="$position = 'left'">
          <fo:external-graphic content-height="1.00in" content-width="1.50in" src="url(res/qspin_logo.png)">
            <xsl:call-template name="fo-external-image"/>
          </fo:external-graphic>
      </xsl:when>

    </xsl:choose>
  </fo:block>
</xsl:template>

</xsl:stylesheet>

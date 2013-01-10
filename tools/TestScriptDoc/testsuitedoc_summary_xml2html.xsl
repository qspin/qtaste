<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output version="4.0" method="html" indent="yes" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
    <xsl:param name="SV_OutputFormat" select="'HTML'"/>

    <xsl:template name="getTestScriptDocFileName">
        <xsl:value-of select="substring-before(@docfilename, '.xml')"/><xsl:text>.html</xsl:text>
    </xsl:template>


    <xsl:template match="/testsuite">
        <html>
            <head>
                <title>Test suite <xsl:value-of select="@name"/> scripts summary</title>
                <style type="text/css">
                    div.indent { margin-top: 3pt; margin-bottom: 6pt; margin-left: 12pt; margin-right: 0pt }
                </style>
            </head>
            <body>
                <h3>Test suite <xsl:value-of select="@name"/></h3>
                <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
                    <tr bgcolor="#CCCCFF"><th align="left" colspan="2"><font size="+2"><b>Test scripts summary</b></font></th></tr>
                </table>
                <br/>
                <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
                    <xsl:apply-templates  select="testscript"/>
                </table>
                <xsl:apply-templates  select="directory"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="directory">
        <b><xsl:value-of select="@name"/></b>
        <div class="indent">
            <table border="1" width="100%" cellpadding="3" cellspacing="0" summary="">
            <xsl:apply-templates  select="testscript"/>
            </table>
            <xsl:apply-templates  select="directory"/>
        </div>
    </xsl:template>

    <xsl:template match="testscript">
        <tr>
            <td width="10%">
                <a target="testScriptFrame"><xsl:attribute name="href"><xsl:call-template name="getTestScriptDocFileName"/></xsl:attribute>
                    <i><xsl:value-of select="@name"/></i>
                </a>
            </td>
            <td width="70%">
                <xsl:copy-of select="summary/node()"/>
                <xsl:if test="(count(summary)=0) or not(summary/node())">&#160;</xsl:if>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>




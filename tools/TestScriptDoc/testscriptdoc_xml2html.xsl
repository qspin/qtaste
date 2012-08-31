<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <xsl:output version="4.0" method="html" indent="no" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
    <xsl:param name="SV_OutputFormat" select="'HTML'"/>

    <xsl:template name="getTestScriptDescription">
            <xsl:copy-of select="info/description/node()"/>
    </xsl:template>

    <xsl:template name="getTestScriptVersion">
            <xsl:if test="(count(info/version)!=0) and info/version/node()">
                <h3>Version</h3>
                <xsl:copy-of select="info/version/node()"/>
            </xsl:if>
    </xsl:template>

    <xsl:template name="getTestScriptPreparation">
            <xsl:if test="(count(info/preparation)!=0) and info/preparation/node()">
                <h3>Preparation</h3>
                <xsl:copy-of select="info/preparation/node()"/>
            </xsl:if>
    </xsl:template>

    <xsl:template name="getStepDescription">
            <xsl:copy-of select="description/node()"/>
    </xsl:template>

    <xsl:template name="getStepExpectedResult">
            <xsl:copy-of select="expected/node()"/>
            <xsl:if test="(count(expected)=0) or not(expected/node())">&#160;</xsl:if>
    </xsl:template>


    <xsl:template match="/testscript">
        <html>
            <head>
                <title>Test script <xsl:value-of select="@name"/> documentation</title>
            </head>
            <body>
                <h2><xsl:value-of select="@name"/></h2>
                <h3>Description</h3>
                <xsl:call-template name="getTestScriptDescription"/>
                <xsl:call-template name="getTestScriptVersion"/>
                <xsl:call-template name="getTestScriptPreparation"/>
				<xsl:if test="count(info/data)!=0">
					<h3>Required data</h3>
					<table border="1" cellspacing="0" cellPadding="2">
					<tr><th>Name</th><th>Type</th><th>Description</th></tr>
					<xsl:for-each select="info/data">
						<tr>
							<td><code><xsl:value-of select="@name"/></code></td>
							<td><code><xsl:value-of select="@type"/></code></td>
							<td><xsl:copy-of select="node()"/></td>
						</tr>
					</xsl:for-each>
					</table>
				</xsl:if>
		<h3>Verified requirement(s)</h3>
		<xsl:choose>
			<xsl:when test="count(testRequirement/REQ)!=0">
				<table border="1" cellspacing="0" cellPadding="2">
					<tr><th>Requirement ID</th><th>Requirement description</th></tr>
					<xsl:for-each select="testRequirement/REQ">
						<tr>
							<td><xsl:value-of select="ID"/></td>
							<td><xsl:value-of select="DESCRIPTION"/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:when>
			<xsl:otherwise>
				Not specified
			</xsl:otherwise>
		</xsl:choose>
                <h3>Steps</h3>
                <table border="1" cellspacing="0" cellPadding="2" width="100%">
                <tr><th width="3%">Step</th><th width="7%">Name</th><th width="45%">Description</th><th width="45%">Expected result</th></tr>
                <xsl:for-each select="steps/step">
                    <tr>
                        <td align="center"><xsl:value-of select="@id"/></td>
                        <td><xsl:value-of select="@name"/></td>
                        <td><xsl:call-template name="getStepDescription"/></td>
                        <td><xsl:call-template name="getStepExpectedResult"/></td>
                    </tr>
                </xsl:for-each>
                </table>
                <h3>Test data</h3>
                <table border="1" cellspacing="0" cellPadding="2">
                <tr>
                    <th>Row</th>
                    <xsl:for-each select="testdata/names/name">
                        <th><code><xsl:value-of select="."/></code></th>
                    </xsl:for-each>
                </tr>
                <xsl:for-each select="testdata/row">
                    <tr>
                        <td><xsl:value-of select="@id"/></td>
                        <xsl:for-each select="value">
                            <td><xsl:value-of select="."/><xsl:if test="not(./node())">&#160;</xsl:if></td>
                        </xsl:for-each>
                    </tr>
                </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>

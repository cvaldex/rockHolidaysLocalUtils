<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/*">
        <table><xsl:apply-templates select="row"/></table>
    </xsl:template>

    <xsl:template match="row[1]">
        <tr><xsl:apply-templates select="*" mode="header"/></tr>
        <xsl:call-template name="standardRow"/>
    </xsl:template>

    <xsl:template match="row" name="standardRow">
        <tr><xsl:apply-templates select="*"/></tr>
    </xsl:template>

    <xsl:template match="row/*">
        <td><xsl:apply-templates select="node()"/></td>
    </xsl:template>

    <xsl:template match="row/*" mode="header">
        <th><xsl:value-of select="name()"/></th>
    </xsl:template>
</xsl:stylesheet>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text"/>
    <xsl:template match="/">
        <xsl:apply-templates select="plist/dict/array/dict"/>
    </xsl:template>
    <xsl:template match="dict">
        <xsl:value-of select="key[. = 'Playlist Persistent ID']/following-sibling::string[1]"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="key[. = 'Name']/following-sibling::string[1]"/>
        <xsl:text>#</xsl:text>
        <xsl:for-each select="array/dict">
            <xsl:value-of select="integer"/>
            <xsl:text>,</xsl:text>
        </xsl:for-each>
        <xsl:text>&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>

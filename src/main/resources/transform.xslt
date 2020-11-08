<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text"/>

    <xsl:template match="/">
        <xsl:apply-templates select="plist/dict/dict/key"/>
    </xsl:template>

    <xsl:template match="key">
        <xsl:value-of select="."/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="following-sibling::dict[1]/key[. = 'Name']/following-sibling::string[1]"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="following-sibling::dict[1]/key[. = 'Artist']/following-sibling::string[1]"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="following-sibling::dict[1]/key[. = 'Album']/following-sibling::string[1]"/>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="following-sibling::dict[1]/key[. = 'Genre']/following-sibling::string[1]"/>
        <xsl:text>&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>

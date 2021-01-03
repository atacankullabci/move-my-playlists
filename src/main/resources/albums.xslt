<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>

    <xsl:template match="dict">
        <xsl:apply-templates select="dict/dict"/>
    </xsl:template>

    <xsl:template match="dict/dict">
        <xsl:value-of select="key[.='Album']/following-sibling::string"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="key[.='Album Artist']/following-sibling::string"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="key[.='Genre']/following-sibling::string"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="key[.='Year']/following-sibling::integer"/>
        <xsl:text>&#x9;</xsl:text>
        <xsl:value-of select="key[.='Track Count']/following-sibling::integer"/>
        <xsl:text>&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>

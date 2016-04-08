<?xml version="1.0" encoding="UTF-8"?>

<!-- was there long ago, probably not useful -->
<!--    xmlns:ja="http://www.elsevier.com/xml/ja/dtd" -->
<!--    xmlns:ce="http://www.elsevier.com/xml/common/dtd" -->


<!-- this is used in the preamble -->
<!-- xmnls="http://www.openarchives.org/OAI/2.0/"  -->
<!-- xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  -->
<!-- xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd" -->

<!-- this is used in the actual article -->

<!-- xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  -->
<!-- xsi:schemaLocation="http://dtd.nlm.nih.gov/2.0/xsd/archivearticle http://dtd.nlm.nih.gov/2.0/xsd/archivearticle.xsd"  -->


<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:xlink="http://www.w3.org/1999/xlink" 
   xmlns:mml="http://www.w3.org/1998/Math/MathML" 
   xmlns:x="fakenamespace" 

   version="1.0"
>

<xsl:strip-space elements="*"/>

<xsl:output method="xml" 
version="1.0" 
encoding="UTF-8" 
indent="no" 
omit-xml-declaration="no"/> 

<xsl:template match="/">

  <!-- skip all preamble -->
<!--   example of preamble  -->
<!--  <responseDate>2013-01-25T18:36:21Z</responseDate> -->
<!--   <request verb="GetRecord" identifier="oai:pubmedcentral.nih.gov:1274295" metad -->
<!-- ataPrefix="pmc">http://www.pubmedcentral.gov/oai/oai.cgi</request> -->
<!--   <GetRecord> -->
<!--     <record> -->
<!--       <header> -->
<!--         <identifier>oai:pubmedcentral.nih.gov:1274295</identifier> -->
<!--         <datestamp>2005-10-31</datestamp> -->
<!--         <setSpec>ploscomp</setSpec> -->
<!--         <setSpec>pmc-open</setSpec> -->
<!--       </header> -->
<!--       <metadata> -->
 
  <xsl:apply-templates select=".//article"/>
</xsl:template>


<xsl:template match="article">
  <xsl:element name="article">
    <!-- article is composed of three sections -->
    <xsl:apply-templates select="front"/>
    <xsl:apply-templates select="body"/>
    <xsl:apply-templates select="back"/>
  </xsl:element>
</xsl:template>

<xsl:template match="front">

  <!-- contains:  -->
  <!-- journal-meta -->
  <!-- article-meta -->

    <xsl:attribute name="pid"><xsl:value-of select=".//article-id[@pub-id-type='pmid']"/></xsl:attribute>
    <xsl:attribute name="pmcid"><xsl:value-of select=".//article-id[@pub-id-type='pmc']"/></xsl:attribute>
    <xsl:attribute name="doi"><xsl:value-of select=".//article-id[@pub-id-type='doi']"/></xsl:attribute>
    <xsl:attribute name="journal"><xsl:value-of select=".//journal-id[@journal-id-type='nlm-ta']"/></xsl:attribute>
    <xsl:attribute name="year"><xsl:value-of select=".//pub-date[@pub-type='ppub']/year/text()"/></xsl:attribute>
    <xsl:attribute name="month"><xsl:value-of select=".//pub-date[@pub-type='ppub']/month/text()"/></xsl:attribute>
    <xsl:attribute name="type">Article</xsl:attribute>
   <xsl:apply-templates select=".//article-title"/>
   <xsl:apply-templates select=".//abstract"/>
</xsl:template>


<xsl:template match="article-title">
  <xsl:param name="depth"/>
  <xsl:element name="title">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="abstract">
  <xsl:param name="depth"/>
  <xsl:element name="abstract">
    <xsl:apply-templates select="sec|p"/>
  </xsl:element>
</xsl:template>

<xsl:template match="sec">
  <xsl:element name="sec">
    <xsl:element name="title">
      <xsl:value-of select="title"/>
    </xsl:element>
    <xsl:apply-templates select="sec|p|fig|table-wrap"/>
  </xsl:element>
</xsl:template>

<xsl:template match="body">
  <xsl:param name="depth"/>
  <xsl:element name="body">
    <xsl:apply-templates select="sec|p">
      <xsl:with-param name="depth" select="body"/>
    </xsl:apply-templates>
  </xsl:element>
</xsl:template>

<xsl:template match="p">
  <xsl:param name="depth"/>
  <xsl:element name="text">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="back">
  <xsl:param name="depth"/>
  <!-- contains: -->
  <!-- ack -->
  <!-- glossary -->
  <!-- ref-list -->
  <!-- fn-group -->
</xsl:template>

<xsl:template match="italic">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="x:italic">
  <xsl:element name="i">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="fig">
  <xsl:element name="fig">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<xsl:template match="fig/label">
  <xsl:element name="fig-label">
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>

<xsl:template match="fig/caption">
  <xsl:element name="fig-caption">
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>

<xsl:template match="table-wrap">
  <xsl:element name="table-wrap">
    <xsl:apply-templates/>
  </xsl:element>
</xsl:template>

<!-- Mute actual table, only keep title, caption, header, footer -->
<xsl:template match="table-wrap/table" />

<xsl:template match="table-wrap/label">
  <xsl:element name="table-label">
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>

<xsl:template match="table-wrap/*">
  <xsl:element name="text">
    <xsl:value-of select="."/>
  </xsl:element>
</xsl:template>

<xsl:template match="xref">
  <!-- skip cross-references -->
  <!-- BUT ACTUALLY: text should be processed to remove statements such as -->
  <!-- see [N] -->
  <!--
  <ref>
    <xsl:apply-templates/>
  </ref>
  -->
  <xsl:value-of select="."/>
</xsl:template>

<!-- Nodes that will be deleted as they do not contain much linguistically
interesting material. -->
<!-- BUG: ce:floats contains all the figures, including the figure captions,
which do contain linguistic material, so it might be better to keep them. -->
<xsl:template
   match="article-id|object-id|table|year|custom-meta-wrap|copyright-year|supplementary-material|back|journal-meta|contrib-group|aff|author-notes|pub-date|volume|issue|fpage|lpage|ext-link|history|copyright-statement|subj-group|elocation-id"/>

</xsl:stylesheet>

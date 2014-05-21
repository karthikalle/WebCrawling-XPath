<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/documentcollection">
		<html>
			<body>
				<h2>
					RSS 2.0 Aggregator
				</h2>
				<table border="1">
					<tr>
						<th>Channel Link</th>
						<th>Item Title</th>
						<th>Item Description</th>
						<th>Item Link</th>
					</tr>
					<xsl:for-each select="document">
						<xsl:for-each select="rss/channel">
							<tr>
								<td>
									<xsl:element name="a">
										<xsl:attribute name="href">
									        <xsl:value-of select="link" />
									    </xsl:attribute>
										<xsl:value-of select="title" />
									</xsl:element>
								</td>
								<td>
								</td>
								<td>
								</td>
								<td>
								</td>
							</tr>
							<xsl:for-each select="item">
								<tr>
									<td></td>
									<td>
										<xsl:value-of select="title">
										</xsl:value-of>
									</td>
									<td>
										<xsl:value-of select="description">
										</xsl:value-of>
									</td>
									<td>
										<xsl:element name="a">
											<xsl:attribute name="href">
									        <xsl:value-of select="link" />
									    </xsl:attribute>
											<xsl:value-of select="link" />
										</xsl:element>
									</td>
								</tr>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:for-each>

				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>

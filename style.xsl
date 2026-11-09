<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>Звіт успішності студентів</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                        margin: 20px;
                        background-color: #fdfdfd;
                        color: #333;
                    }
                    h1 {
                        color: #0d6efd;
                        border-bottom: 2px solid #0d6efd;
                        padding-bottom: 8px;
                    }
                    .student-card {
                        background: #fff;
                        border: 1px solid #dee2e6;
                        border-radius: 6px;
                        padding: 15px;
                        margin-bottom: 20px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                    }
                    .student-name {
                        font-size: 1.2em;
                        font-weight: bold;
                        color: #212529;
                        margin-bottom: 6px;
                    }
                    .dept-info {
                        color: #6c757d;
                        font-size: 0.95em;
                        margin-bottom: 12px;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 8px;
                    }
                    th, td {
                        border: 1px solid #e9ecef;
                        padding: 8px 12px;
                        text-align: left;
                    }
                    th {
                        background-color: #f8f9fa;
                        font-weight: 600;
                    }
                    tr:nth-child(even) {
                        background-color: #fafafa;
                    }
                    .badge {
                        display: inline-block;
                        padding: 2px 8px;
                        font-size: 0.85em;
                        font-weight: bold;
                        border-radius: 4px;
                        background-color: #e7f1ff;
                        color: #0c63e4;
                    }
                </style>
            </head>
            <body>
                <h1>Звіт успішності студентів</h1>
                <xsl:for-each select="students_performance/student">
                    <div class="student-card">
                        <div class="student-name">
                            <xsl:value-of select="full_name"/>
                        </div>
                        <div class="dept-info">
                            <strong>Факультет:</strong> <xsl:value-of select="department/faculty"/> |
                            <strong>Кафедра:</strong> <xsl:value-of select="department/chair"/> |
                            <strong>Курс:</strong> <xsl:value-of select="department/course_year"/> |
                            <strong>Група:</strong> <xsl:value-of select="department/group"/>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Дисципліна</th>
                                    <th>Кредити</th>
                                    <th>Оцінка</th>
                                    <th>Буквений бал</th>
                                </tr>
                            </thead>
                            <tbody>
                                <xsl:for-each select="courses/course">
                                    <tr>
                                        <td><xsl:value-of select="title"/></td>
                                        <td><xsl:value-of select="credits"/></td>
                                        <td><xsl:value-of select="grade"/></td>
                                        <td><span class="badge"><xsl:value-of select="grade_letter"/></span></td>
                                    </tr>
                                </xsl:for-each>
                            </tbody>
                        </table>
                    </div>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>

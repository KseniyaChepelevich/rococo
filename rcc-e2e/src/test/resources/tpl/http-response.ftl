<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset = UTF-8">

    <link type="text/css" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/xml.min.js"></script>
    <script type="text/javascript">hljs.initHighlightingOnLoad();</script>

    <style>
        pre {
            white-space: pre-wrap;
        }
        table {
             border-collapse: collapse;
             width: 100%;
             margin-bottom: 10px;
        }
        th, td {
             border: 1px solid #ddd;
             padding: 6px;
             text-align: left;
        }
        th {
              background-color: #f5f5f5;
        }
        .status-2xx { color: #28a745; font-weight: bold; }
                .status-3xx { color: #0366d6; font-weight: bold; }
                .status-4xx { color: #fd7e14; font-weight: bold; }
                .status-5xx { color: #dc3545; font-weight: bold; }
    </style>
</head>
<body>
<h5>HTTP Response</h5>
<div>
    <#if data.responseCode??>
        <#assign statusClass = "status-2xx">
        <#if data.responseCode gte 300 && data.responseCode lt 400>
            <#assign statusClass = "status-3xx">
        <#elseif data.responseCode gte 400 && data.responseCode lt 500>
            <#assign statusClass = "status-4xx">
        <#elseif data.responseCode gte 500>
            <#assign statusClass = "status-5xx">
        </#if>
        <span class="${statusClass}">Status: ${data.responseCode}</span>
    </#if>
</div>

<#if data.headers?? && (data.headers?size > 0)>
     <h4>Headers</h4>
     <table>
         <#list data.headers as name, value>
             <tr>
                 <th>${name?html}</th>
                 <td>${value?html}</td>
             </tr>
         </#list>
     </table>
</#if>
<#if data.body?? && (data.body?length > 0)>
    <h4>Body</h4>
    <div>
        <#assign lang = "">
        <#if data.headers?? && data.headers?size gt 0>
            <#list data.headers as name, value>
                <#if name?lower_case == "content-type">
                    <#if value?lower_case?contains("json")>
                        <#assign lang = "json">
                    <#elseif value?lower_case?contains("html") || value?lower_case?contains("xml")>
                        <#assign lang = "xml">
                    </#if>
                </#if>
            </#list>
        </#if>
        <#if lang == "">
            <#if data.body?trim?starts_with("{") || data.body?trim?starts_with("[")>
                <#assign lang = "json">
            <#elseif data.body?contains("<") && data.body?contains(">")>
                <#assign lang = "xml">
            <#else>
                <#assign lang = "no-highlight">
            </#if>
        </#if>
        <pre>
            <code class="${lang}">
                <#if lang == "json">${data.body}<#else>${data.body?html}</#if>
            </code>
        </pre>
    </div>
</#if>
</body>
</html>
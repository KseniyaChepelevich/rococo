<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset = UTF-8">

    <link type="text/css" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/xml.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/bash.min.js"></script>
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
        .method {
               font-weight: bold;
               color: #0366d6;
        }
    </style>
</head>
<body>
<h5>HTTP Request</h5>
<div>
    <pre>
       <code class="http"><span class="method">${data.method}</span> <#if data.url??>${data.url?html}</#if></code>
    </pre>
</div>

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
<#if data.formParams?? && (data.formParams?size > 0)>
    <h4>Form Params</h4>
    <table>
        <#list data.formParams as name, value>
            <tr>
                <th>${name}</th>
                <td>${value}</td>
            </tr>
        </#list>
    </table>
</#if>
<#if data.curl??>
    <h4>cURL</h4>
    <div>
        <pre>
            <code class="bash">
                ${data.curl?html}
            </code>
        </pre>
    </div>
</#if>

</body>
</html>
<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<head>
    <base target="_blank"/>
    <script src="http://tjs.sjs.sinajs.cn/t35/apps/opent/js/frames/client.js" language="JavaScript"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/index.js" />"></script>
    <style>
        body{
            background:url('<c:url value="/resources/image/auth-background.jpg" />');
        }
    </style>

</head>
<body>
<input id="appKey" name="appKey" value="${appKey}" type="hidden"/>
<input id="callBackUrl" name="callBackUrl" value="${callBackUrl}" type="hidden"/>
</body>
</html>
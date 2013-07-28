<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<head>
    <base target="_blank"/>
    <script src="http://tjs.sjs.sinajs.cn/t35/apps/opent/js/frames/client.js" language="JavaScript"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.10.2.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/index.js" />"></script>
</head>
<body>
<input id="authUrl" name="authUrl" value="${authUrl}" type="hidden"/>
<input id="appKey" name="appKey" value="${appKey}" type="hidden"/>
<input id="callBackUrl" name="callBackUrl" value="${callBackUrl}" type="hidden"/>
</body>
</html>
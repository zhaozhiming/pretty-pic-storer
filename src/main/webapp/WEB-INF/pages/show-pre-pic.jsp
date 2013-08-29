<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<html lang="en">
<head>
    <title>pretty picture storer</title>

    <link rel="stylesheet" type="text/css" href="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/css/bootstrap-responsive.min.css">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/pps/show-pre-pic.css" />">

    <script type="text/javascript" src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=${appKey}" charset="utf-8"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/show-pre-pic.js" />"></script>
</head>

<body>
<input id="token" name="token" value="${token}" type="hidden"/>
<input id="picturesUrl" name="picturesUrl" value="<c:url value="/pictures" />" type="hidden"/>


<div id="pictureDisplay" class="row-fluid"></div>

</body>
</html>
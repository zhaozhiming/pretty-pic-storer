<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>pretty picture storer</title>

    <link href="<c:url value="/resources/css/bootstrap/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/css/bootstrap/bootstrap-responsive.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/css/jquery/jquery-ui.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/css/pps/pretty-picture.css" />" rel="stylesheet">

    <script type="text/javascript" src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=${appKey}" charset="utf-8"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.10.2.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-ui.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery.fileDownload.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/pretty-picture.js" />"></script>
</head>

<body>

<div class="container">
    <div class="row">
        <div class="span8 offset2">
            <h1>Users</h1>

            <div class="control-group">
                <form action="save" method="post" class="form-horizontal">
                    <div class="controls">
                        <input id="friends" name="friends" type="text"/>
                        <input id="currentUid" name="currentUid" value="${currentUid}" type="hidden"/>
                        <input id="token" name="token" value="${token}" type="hidden"/>
                        <input id="friendSelector" type="button" value="选择好友" class="btn"/>
                    </div>
                    <div class="controls">
                        <input type="button" id="saveBtn" value="Do it!" class="btn"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="dialog" title="Basic dialog">
</div>

</body>
</html>
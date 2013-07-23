<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<html lang="en">
<head>
    <title>pretty picture storer</title>

    <link href="<c:url value="/resources/css/bootstrap/bootstrap.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/css/bootstrap/bootstrap-responsive.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/css/jquery/jquery-ui.css" />" rel="stylesheet">
    <link href="<c:url value="/resources/css/pps/pretty-picture.css" />" rel="stylesheet">

    <script type="text/javascript" src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=${appKey}"
            charset="utf-8"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.10.2.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-ui.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery.fileDownload.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery.watermark.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/pretty-picture.js" />"></script>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="span8 offset2">
            <div class="control-group">
                <form>
                    <fieldset>
                        <legend>Legend</legend>
                        <input id="friends" name="friends" type="text" readonly required/>
                        <input id="uids" name="uids" type="hidden"/>
                        <input id="currentUid" name="currentUid" value="${currentUid}" type="hidden"/>
                        <input id="token" name="token" value="${token}" type="hidden"/>
                        <input id="friendSelector" type="button" value="选择好友" class="btn-info"/>
                        <span class="help-block">好友数量不能超过5个</span>
                        <input type="button" id="saveBtn" value="Do it!" class="btn-primary"/>
                    </fieldset>
                </form>
            </div>
        </div>
    </div>

    <div id="dialog" title="Basic dialog">
    </div>

</body>
</html>
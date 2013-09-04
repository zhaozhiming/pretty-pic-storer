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
<input id="backgroundUrl" name="backgroundUrl" value="<c:url value="/resources/image/main-background.jpg" />" type="hidden"/>
<input id="currentPage" name="currentPage" value="1" type="hidden"/>

<div class="pagination">
    <ul>
        <li class="active li1"><a href="#">1</a></li>
        <li class="li2"><a href="#">2</a></li>
        <li class="li3"><a href="#">3</a></li>
        <li class="li4"><a href="#">4</a></li>
        <li class="li5"><a href="#">5</a></li>
        <li><a href="#">&gt;&gt;</a></li>
    </ul>
</div>

<div id="pictureDisplay" class="row-fluid"></div>
<div>
    <span id="fullSelect" hidden="true">
        <input type="checkbox">全选
        <button id="saveBtn" type='button' class='btn btn-success'>保存</button>
    </span>
    <button id="batchSaveBtn" type='button' class='btn btn-primary'>批量保存图片</button>
</div>

<div class="pagination">
    <ul>
        <li class="active li1"><a href="#">1</a></li>
        <li class="li2"><a href="#">2</a></li>
        <li class="li3"><a href="#">3</a></li>
        <li class="li4"><a href="#">4</a></li>
        <li class="li5"><a href="#">5</a></li>
        <li><a href="#">&gt;&gt;</a></li>
    </ul>
</div>

</body>
</html>
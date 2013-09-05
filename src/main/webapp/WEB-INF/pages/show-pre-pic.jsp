<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<html lang="en">
<head>
    <title>pretty picture storer</title>

    <link rel="stylesheet" type="text/css"
          href="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css"
          href="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/css/bootstrap-responsive.min.css">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/pps/show-pre-pic.css" />">

    <script type="text/javascript" src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=${appKey}"
            charset="utf-8"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/show-pre-pic.js" />"></script>
</head>

<body>
<input id="token" name="token" value="${token}" type="hidden"/>
<input id="currentUid" name="currentUid" value="${currentUid}" type="hidden"/>
<input id="userTasksUrl" name="userTasksUrl" value="<c:url value="/tasks/${currentUid}" />" type="hidden"/>
<input id="picturesUrl" name="picturesUrl" value="<c:url value="/pictures" />" type="hidden"/>
<input id="saveUrl" name="saveUrl" value="<c:url value="/task/create" />" type="hidden"/>
<input id="backgroundUrl" name="backgroundUrl" value="<c:url value="/resources/image/main-background.jpg" />" type="hidden"/>
<input id="currentPage" name="currentPage" value="1" type="hidden"/>


<ul class="nav nav-tabs" id="myTab">
    <li class="active"><a href="#picturesLabel" data-toggle="tab">微博图片</a></li>
    <li><a href="#taskLabel" data-toggle="tab">任务</a></li>
</ul>

<div class="tab-content">
    <div class="tab-pane active" id="picturesLabel">

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
            <div id="error" class="alert alert-error" hidden="true"></div>
            <span id="fullSelectSpan" hidden="true">
                <button id="fullSelect" type='button' class='btn btn-success'>全选</button>
                <button id="saveBtn" type='button' class='btn btn-success'>保存</button>
            </span>
            <button id="batchSaveBtn" type='button' class='btn btn-primary'>批量保存图片</button>
            <button id="publiswb" type='button' class='btn btn-danger'>分享微博</button>

            <div id="dialogModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="dialogModalLabel" aria-hidden="true">
                <div class="modal-header">
                    <h3 id="dialogModalLabel">保存图片</h3>
                </div>
                <div id="dialogModalBody" class="modal-body">
                </div>
                <div id="dialogModalFooter" class="modal-footer">
                </div>
            </div>

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
    </div>
    <div class="tab-pane" id="taskLabel">
        <h3>我的任务</h3>
        <div class="alert alert-info">
            任务完成后请及时下载，任务超过<strong class="text-error">1天</strong>将被删除
        </div>
        <table id="taskTable" class="table table-bordered table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>下载地址</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
</div>


</body>
</html>
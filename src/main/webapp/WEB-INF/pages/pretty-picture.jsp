<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html; charset=UTF-8" %>
<html lang="en">
<head>
    <title>pretty picture storer</title>

    <link href="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet">
    <link href="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/css/bootstrap-responsive.min.css" rel="stylesheet">

    <script type="text/javascript" src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=${appKey}" charset="utf-8"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
    <script type="text/javascript" src="http://cdn.staticfile.org/twitter-bootstrap/2.3.2/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/pretty-picture.js" />"></script>
</head>

<body>
<div id="ppscontainer" class="container">
    <div class="row">
        <div class="span8 offset2">
            <div class="control-group">
                <fieldset>
                    <h3>美图微存</h3>
                    <div class="alert alert-info">
                        美图微存可以选择好友获取<strong class="text-error">今日</strong>的微博图片，但注意好友数量不能超过<strong
                            class="text-error">5</strong>个
                    </div>

                    <div class="controls">
                        <input id="friends" placeholder="请选择好友..." name="friends" type="text" readonly required/>
                        <input id="uids" name="uids" type="hidden"/>
                        <input id="currentUid" name="currentUid" value="${currentUid}" type="hidden"/>
                        <input id="token" name="token" value="${token}" type="hidden"/>
                        <input id="backgroundUrl" name="backgroundUrl" value="<c:url value="/resources/image/main-background.jpg" />" type="hidden"/>
                        <input id="saveUrl" name="saveUrl" value="<c:url value="/task/create" />" type="hidden"/>
                        <input id="userTasksUrl" name="userTasksUrl" value="<c:url value="/tasks/${currentUid}" />" type="hidden"/>
                        <input id="friendSelector" type="button" value="选择好友" class="btn-info"/>
                        <input type="button" id="saveBtn" value="保存图片" class="btn-primary"/>
                    </div>
                    <div id="error" class="alert alert-error"></div>
                </fieldset>
            </div>

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

    <div id="dialogModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="dialogModalLabel" aria-hidden="true">
        <div class="modal-header">
            <h3 id="dialogModalLabel">保存图片</h3>
        </div>
        <div id="dialogModalBody" class="modal-body">
        </div>
        <div id="dialogModalFooter" class="modal-footer">
        </div>
    </div>

    <input id="publiswb" type="button" class="btn btn-danger" value="分享微博"/>
</div>
</body>
</html>
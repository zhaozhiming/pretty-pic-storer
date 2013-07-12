<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta charset="utf-8">
    <title>Spring MVC Application</title>

    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="http://twitter.github.io/bootstrap/assets/css/bootstrap.css" rel="stylesheet">
    <link href="http://twitter.github.io/bootstrap/assets/css/bootstrap-responsive.css" rel="stylesheet">

    <script type="text/javascript" src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=${appKey}" charset="utf-8"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery/jquery-1.10.2.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/pps/pretty-picture.js" />"></script>

    <script>
        WB2.init({
            'access_token': '${token}'
        });
    </script>
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
                        <input id="friendSelector" type="button" value="select" class="btn"/>
                    </div>
                    <div class="controls">
                        <input id="rootPath" name="rootPath" type="text"/>
                    </div>
                    <div class="controls">
                        <input type="submit" value="Save Pictures" class="btn"/>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
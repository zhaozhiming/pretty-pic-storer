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
    <link href="http://img.t.sinajs.cn/t4/appstyle/widget/css/selector/selector.css" type="text/css" rel="stylesheet">

    <script src="http://tjs.sjs.sinajs.cn/open/api/js/wb.js?appkey=789351627" type="text/javascript"
            charset="utf-8"></script>
    <script>

        //回调函数，返回选择结果
        function cbk(data) {
            var friends = "";
            for(var i=0;i < data.length; i++) {
                friends = friends + data[i].id + ";";
            }

            document.getElementById("friends").value = friends;
        }

        WB2.init({
            'access_token': '${token}'
        });

        WB2.anyWhere(function (W) {
            W.widget.selector({
                'id': "friendSelector",	//必选
                'callback': cbk,
                tab: {
                    'list': [3]
                }
            });
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

            <h3>Users</h3>
            <table class="table table-bordered table-striped">
                <thead>
                <tr>
                    <th>token</th>
                    <th>uid</th>
                    <th>remindIn</th>
                    <th>expiresIn</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${token}</td>
                    <td>${uid}</td>
                    <td>${remindIn}</td>
                    <td>${expiresIn}</td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>
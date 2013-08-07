$(document).ready(function () {
    $("#error").hide();

    function selectCallback(data) {
        if (data.length > 5) {
            $("#error").show().fadeOut(4000);
            return;
        }

        var friends = "";
        var uids = "";
        for (var i = 0; i < data.length; i++) {
            friends = friends + data[i].screen_name + ";";
            uids = uids + data[i].id + ";";
        }

        $("#friends").val(friends);
        $("#uids").val(uids);
    }

    WB2.init({
        'access_token': $("#token").val()
    });

    WB2.anyWhere(function (W) {
        W.widget.selector({
            'id': "friendSelector",
            'callback': selectCallback,
            tab: {
                'list': [3]
            }
        });
    });

    $("#dialog").dialog({
        autoOpen: false,
        dialogClass: "no-close",
        modal: "true",
        title: "结果"
    });

    function createTd(key, value) {
        var tdContent = "";
        if (key === "taskStatus") {
            switch (value) {
                case "new" :
                    tdContent = "<tr class='error'>新建";
                    break;
                case "running" :
                    tdContent = "<tr class='warning'>处理中";
                    break;
                case "done" :
                    tdContent = "<tr class='success'>已完成";
                    break;
                case "nothing" :
                    tdContent = "<tr class='info'>无图片";
                    break;
            }
        } else if (key === "zipFileUrl") {
            if (value === "no") {
                tdContent = "<tr>无";
            } else {
                tdContent = "<tr><a href='" + value + "'>下载</a>"
            }
        } else {
            tdContent = "<tr>" + value;
        }
        return tdContent;
    }

    function putTasksToTable(tasks) {
        var tblBody = "";
        $.each(tasks, function (index) {
            var tblRow = "<td>" + (index + 1) + "</td>";
            $.each(this, function (k, v) {
                var trContent = createTd(k, v);
                tblRow += "<td>" + trContent + "</td>";
            });
            tblBody += tblRow + "</tr>";
        });
        $("#taskTable tbody").html(tblBody);
        createPagination();
    }

    function createPagination() {
        if($("#pages").length) $("#pages").remove();

        var rows = $('#taskTable').find('tbody tr').length;
        var rowNumPerPage = 5;
        var pagesNo = Math.ceil(rows / rowNumPerPage);
        var pageNumbers = $('<div id="pages" class="pagination"></div>');
        var pageUl = $('<ul></ul>');
        pageUl.appendTo(pageNumbers);
        for (i = 0; i < pagesNo; i++) {
            $('<li><a class="page" href="#">' + (i + 1) + '</a></li>').appendTo(pageUl);
        }
        pageNumbers.insertAfter('#taskTable');
        $('#taskTable').find('tbody tr').hide();
        var tr = $('#taskTable tbody tr');
        for (var i = 0; i <= rowNumPerPage - 1; i++) {
            $(tr[i]).show();
        }

        $('.page').click(function () {
            $('#taskTable').find('tbody tr').hide();
            for (i = ($(this).text() - 1) * rowNumPerPage; i <= $(this).text() * rowNumPerPage - 1; i++) {
                $(tr[i]).show();
            }
        });
    }

    function queryUserTasks() {
        $.ajax({
            url: $("#userTasksUrl").val(),
            type: "GET",
            dataType: "text"
        }).done(function (data) {
                var tasks = jQuery.parseJSON(data);
                putTasksToTable(tasks);
                setTimeout(queryUserTasks, 3000);
        });
    }

    queryUserTasks();

    $("#saveBtn").click(function () {
        var ajaxData = {
            uids: $("#uids").val(),
            currentUid: $("#currentUid").val(),
            token: $("#token").val()
        };

        $.ajax({
            url: $("#saveUrl").val(),
            data: ajaxData,
            type: "POST",
            dataType: "text",
            beforeSend: function () {
                $("#dialog").html("处理中...请稍候");
                $("#dialog").dialog("option", "buttons", []);
                $("#dialog").dialog("open");
            }
        }).done(function (data) {
                var result = jQuery.parseJSON(data);
                var message = result.message;
                var htmlContent;

                if (message === "OK") {
                    htmlContent = "已开始为您保存图片，请稍后查看任务";
                }

                $("#dialog").html(htmlContent);
                $("#dialog").dialog("option", "buttons", [
                    { text: "Ok", click: function () {
                        $(this).dialog("close");
                    } }
                ]);

            }).fail(function () {
                $("#dialog").html("出错了！");
                $("#dialog").dialog("option", "buttons", [
                    { text: "Ok", click: function () {
                        $(this).dialog("close");
                    } }
                ]);
            });
    });

});

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

    function getUrlTd(zipFileUrl) {
        var urlTd = "";
        if (zipFileUrl === "no") {
            urlTd = "无";
        } else {
            urlTd = "<a href=" + zipFileUrl + ">下载</a>";
        }
        return urlTd;
    }

    function dealWithTaskStatus(taskStatus) {
        var tblRow = "";
        var statusTd = "";
        switch (taskStatus) {
            case "new":
                tblRow += "<tr class='info'>"
                statusTd = "新建";
                break;
            case "running":
                tblRow += "<tr class='warning'>"
                statusTd = "处理中";
                break;
            case "nothing":
                tblRow += "<tr class='success'>"
                statusTd = "无图片";
                break;
            case "done":
                tblRow += "<tr class='success'>"
                statusTd = "完成";
                break;
        }
        return {statudTd: statusTd, tblRow: tblRow};
    }

    function putTasksToTable(tasks) {
        var tblBody = "";
        $.each(tasks, function (index) {
            var tblRow = "";
            var result = dealWithTaskStatus(this.taskStatus);
            tblRow += result.tblRow;
            tblRow += "<td>" + (index + 1) + "</td>";
            tblRow += "<td>" + result.statudTd + "</td>";
            tblRow += "<td>" + this.createdAt + "</td>";
            tblRow += "<td>" + getUrlTd(this.zipFileUrl) + "</td>";
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
                $('#dialogModal').modal({
                    keyboard: false
                });
                $("#dialogModalBody").html("处理中...请稍候");
            }
        }).done(function (data) {
                $("#dialogModalBody").html("已开始为您保存图片，请稍后查看任务");
                $("dialogModalFooter").html('<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">close</button>');
            }).fail(function () {
                $("#dialogModalBody").html("出错了！");
                $("dialogModalFooter").html('<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">close</button>');
            });
    });

});

$(document).ready(function () {
    $('body').css('background-image', 'url(' + $("#backgroundUrl").val() + ')');
    $("#error").hide();
    $("#publiswb").hide();

    function selectCallback(data) {
        if (data.length > 5) {
            $("#error").html("好友数量不能超过5个").show().fadeOut(4000);
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

    WB2.anyWhere(function(W){
        W.widget.publish({
            'id' : "publiswb",
            'default_text' : '我通过"美图微存"保存了好友的图片，你们也来试试吧!http://apps.weibo.com/prettypicturequery/'
        });
    });

    function dealWithTaskStatus(taskStatus, zipFileUrl) {
        var tblRow = "";
        var statusTd = "";
        var urlTd = "";
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
                statusTd = "完成";
                urlTd = "没有搜索到图片可以下载";
                break;
            case "done":
                tblRow += "<tr class='success'>"
                statusTd = "完成";
                urlTd = "<a href=" + zipFileUrl + ">下载</a>";
                break;
        }
        return {statudTd: statusTd, tblRow: tblRow, urlTd: urlTd};
    }

    function putTasksToTable(tasks) {
        var tblBody = "";
        $.each(tasks, function (index) {
            var tblRow = "";
            var result = dealWithTaskStatus(this.taskStatus, this.zipFileUrl);
            tblRow += result.tblRow;
            tblRow += "<td>" + (index + 1) + "</td>";
            tblRow += "<td>" + result.statudTd + "</td>";
            tblRow += "<td>" + this.createdAt + "</td>";
            tblRow += "<td>" + result.urlTd + "</td>";
            tblBody += tblRow + "</tr>";
        });
        $("#taskTable tbody").html(tblBody);
        createPagination();
    }

    var currentPageNo = 1;
    function createPagination() {
        if ($("#pages").length) $("#pages").remove();

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
        for (var i = (currentPageNo - 1) * rowNumPerPage; i <= currentPageNo * rowNumPerPage - 1; i++) {
            $(tr[i]).show();
        }

        $('.page').click(function () {
            currentPageNo = $(this).text();
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
        var friends = $("#friends").val();
        if (friends === "") {
            $("#error").html("请选择好友").show().fadeOut(4000);
            return;
        }

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
                $("#dialogModalBody").html("保存图片任务已创建，请稍后查看");
                var okBtn = '<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">关闭</button>';
                var publishBtnClick = "$('#publiswb').click()";
                var publishBtn = '<button id="publishBtn" class="btn btn-danger" data-dismiss="modal" aria-hidden="true" onclick="' + publishBtnClick + '">分享微博</button>';
                var buttons = okBtn + publishBtn;
                $("#dialogModalFooter").html(buttons);
            }).fail(function () {
                $("#dialogModalBody").html("出错了！");
                $("#dialogModalFooter").html('<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">关闭</button>');
            });
    });
});

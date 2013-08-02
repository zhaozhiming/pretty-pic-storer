$(document).ready(function () {

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

    $("#errorMsg").hide();

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
                var fileUrl = result.fileUrl;

                $("#dialog").html("保存图片完成,地址:" + fileUrl);
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

    function checkProgress() {
        $.ajax({
            url: "/pretty-pic-storer/check",
            data: {currentUid: $("#currentUid").val()},
            type: "POST",
            dataType: "text"
        }).done(function (data) {
                var result = jQuery.parseJSON(data);
                var status = result.checkStatus;

                if (status === "get") {
                    $("#dialog").html("开始获取微博...");
                } else if (status === "save") {
                    var content = "获取微博完成，开始下载图片..." + "<br/>";
                    content += "已下载: " + result.alreadySave;
                    content += ", 总共: " + result.totalCount;
                    $("#dialog").html(content);
                }
            }).fail(function () {
                $("#dialog").html("出错了！");
                $("#dialog").dialog("option", "buttons", [
                    { text: "Ok", click: function () {
                        $(this).dialog("close");
                    } }
                ]);
            }).always(function (data) {
                var result = jQuery.parseJSON(data);
                var status = result.checkStatus;

                if (status !== "zip") {
                    setTimeout(checkProgress, 1000);
                }
            });
    }

    $("#dialog").dialog({
        autoOpen: false,
        dialogClass: "no-close",
        modal: "true",
        title: "结果"
    });

});

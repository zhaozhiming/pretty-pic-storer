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
                var saveStatus = result.saveStatus;
                var htmlContent;

                if(saveStatus === "nothing") {
                    htmlContent = "没有图片可以保存";
                }
                if(saveStatus === "success") {
                    htmlContent = "保存图片完成";
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

    $("#dialog").dialog({
        autoOpen: false,
        dialogClass: "no-close",
        modal: "true",
        title: "结果"
    });

});

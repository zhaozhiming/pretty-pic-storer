function selectCallback(data) {
    var friends = "";
    for (var i = 0; i < data.length; i++) {
        friends = friends + data[i].id + ";";
    }

    $("#friends").val(friends);
}

WB2.anyWhere(function (W) {
    W.widget.selector({
        'id': "friendSelector",
        'callback': selectCallback,
        tab: {
            'list': [3]
        }
    });
});

$(document).ready(function () {
    $("#saveBtn").click(function () {
        var ajaxData = {
            friends: $("#friends").val(),
            rootPath: $("#rootPath").val()
        };

        $.fileDownload('/pretty-pic-storer/save', {
            httpMethod: "POST",
            data: ajaxData,
            prepareCallback: function (url) {
                $("#dialog").html("处理中...请稍候");
                $("#dialog").dialog("option", "buttons", []);
                $("#dialog").dialog("open");
            }
        }).done(function () {
                $("#dialog").html("保存图片完成");
                $("#dialog").dialog("option", "buttons", [
                    { text: "Ok", click: function () {
                        $(this).dialog("close");
                    } }
                ]);
            })
            .fail(function () {
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
        width: 500,
        title: "结果"
    });

});

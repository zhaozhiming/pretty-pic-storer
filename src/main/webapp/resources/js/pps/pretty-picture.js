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
        $.ajax({
            url: '/pretty-pic-storer/save',
            data: ajaxData,
            type: 'post',
            dataType: 'text',
            success: function (data) {
                var result = jQuery.parseJSON(data);
                var dialogText = "文件个数:" + result.fileCount + "<br/>";
                dialogText += "存储路径:" + result.savePath + "<br/>";
                dialogText += "耗费时间:" + result.consumeTime;
                $("#dialog").html(dialogText);
                $("#dialog").dialog("open");
            },
            error: function (e) {
                $("#dialog").html("ERROR:<br/>" + e.statusText);
                $("#dialog").dialog("open");
            }
        });
    });

    $("#dialog").dialog({
        autoOpen: false,
        dialogClass: "no-close",
        width: 500,
        title: "结果",
        buttons: [
            {
                text: "OK",
                click: function () {
                    $(this).dialog("close");
                }
            }
        ]
    });

});

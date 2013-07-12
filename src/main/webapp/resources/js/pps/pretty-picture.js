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
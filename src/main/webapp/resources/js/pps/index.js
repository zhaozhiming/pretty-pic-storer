$(document).ready(function () {
    App.AuthDialog.show({
        client_id: $("#appKey").val(),
        redirect_uri: $("#callBackUrl").val(),
        response_type: "code",
        display: "default"
    });

});
$(document).ready(function () {
    $.ajax({
        url: $("#picturesUrl").val(),
        type: "POST",
        data: {token: $("#token").val()},
        dataType: "text"
    }).done(function (data) {
            var statuses = jQuery.parseJSON(data);

            var table = "<table class='table'><tbody>";
            $.each(statuses, function (index) {
                var mod = index % 5;

                if(mod === 0) {
                    table += "<tr>";
                }

                table += "<td width='20%'><div>";
                table += "<img src='" + this.thumbnailPic + "' title='" + this.text + "'>";
                table += "<p>" + this.screenName + "</p>";
                table += "<p><button class='btn btn-success' title='zoom in'>";
                table += "<i class='icon-zoom-in icon-white'></i>";
                table += "</button> ";
                table += "<button class='btn btn-success' title='save file'>";
                table += "<i class='icon-file icon-white'></i>";
                table += "</button></p></div></td>";

                if(mod === 4) {
                    table += "</tr>";
                }
            });
            table += "</tbody></table>";

            $("#pictureDisplay").html(table);
        });


});
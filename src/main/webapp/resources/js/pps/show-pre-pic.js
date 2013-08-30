$(document).ready(function () {

    function setcurrentPageNo(currentPage) {
        var pageNo = parseInt($("#currentPage").val());
        if (currentPage.html() === "&lt;&lt;") {
            pageNo = pageNo - 1;
        } else if (currentPage.html() === "&gt;&gt;") {
            pageNo = pageNo + 1;
        } else {
            pageNo = parseInt(currentPage.html());
        }

        $("#currentPage").val(pageNo);
        return pageNo;
    }

    function createPagination(currentPage) {
        var currentPageJquery = $(currentPage);
        var pageNo = setcurrentPageNo(currentPageJquery);

        var ulContent = "<ul>";
        if (pageNo !== 1) {
            ulContent += "<li><a href='#'>&lt;&lt;</a></li>";
        }
        if (pageNo === 1 || pageNo === 2) {
            ulContent += "<li class='li1' ><a href='#'>1</a></li>";
            ulContent += "<li class='li2' ><a href='#'>2</a></li>";
            ulContent += "<li class='li3' ><a href='#'>3</a></li>";
            ulContent += "<li class='li4' ><a href='#'>4</a></li>";
            ulContent += "<li class='li5' ><a href='#'>5</a></li>";
            ulContent += "<li><a href='#'>&gt;&gt;</a></li>";
        } else {
            ulContent += "<li class='li" + (pageNo - 2) + "' ><a href='#'>" + (pageNo - 2) + "</a></li>";
            ulContent += "<li class='li" + (pageNo - 1) + "' ><a href='#'>" + (pageNo - 1) + "</a></li>";
            ulContent += "<li class='li" + pageNo + "' ><a href='#'>" + pageNo + "</a></li>";
            ulContent += "<li class='li" + (pageNo + 1) + "' ><a href='#'>" + (pageNo + 1) + "</a></li>";
            ulContent += "<li class='li" + (pageNo + 2) + "' ><a href='#'>" + (pageNo + 2) + "</a></li>";
            ulContent += "<li><a href='#'>>></a></li>";
        }
        ulContent += "</ul>";

        $(".pagination").html(ulContent);
        $(".pagination li a").click(function() {
            createPagination(this);
            getPictures();

        });
        $(".pagination li.li" + pageNo).addClass("active");
        $(".pagination li.li" + pageNo + " a").off("click");
    }

    function createTable(statuses) {
        var table = "<table class='table'><tbody>";
        $.each(statuses, function (index) {
            var mod = index % 5;

            if (mod === 0) {
                table += "<tr>";
            }

            table += "<td width='20%'><div>";
            table += "<img src='" + this.thumbnailPic + "' title='" + this.text + "'>";
            table += "<p>" + this.screenName + "</p>";
            table += "<p><button class='btn btn-success' title='zoom in'>";
            table += "<i class='icon-zoom-in icon-white'></i>";
            table += "</button></p></div></td>";

            if (mod === 4) {
                table += "</tr>";
            }
        });
        table += "</tbody></table>";
        return table;
    }

    function getPictures() {
        var ajaxData = {
            token: $("#token").val(),
            currentPage: $("#currentPage").val()
        };

        $.ajax({
            url: $("#picturesUrl").val(),
            type: "POST",
            data: ajaxData,
            dataType: "text"
        }).done(function (data) {
                var statuses = jQuery.parseJSON(data);

                var table = createTable(statuses);

                $("#pictureDisplay").html(table);
            });
    }

    $(".pagination li a").click(function() {
        createPagination(this);
        getPictures();
    });

    $(".pagination li.li1 a").click();
    $(".pagination li.li1 a").off("click");

});
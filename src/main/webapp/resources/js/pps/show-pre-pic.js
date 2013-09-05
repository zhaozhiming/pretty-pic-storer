$(document).ready(function () {
    $('body').css('background-image', 'url(' + $("#backgroundUrl").val() + ')');

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
        } else {
            ulContent += "<li class='li" + (pageNo - 2) + "' ><a href='#'>" + (pageNo - 2) + "</a></li>";
            ulContent += "<li class='li" + (pageNo - 1) + "' ><a href='#'>" + (pageNo - 1) + "</a></li>";
            ulContent += "<li class='li" + pageNo + "' ><a href='#'>" + pageNo + "</a></li>";
            ulContent += "<li class='li" + (pageNo + 1) + "' ><a href='#'>" + (pageNo + 1) + "</a></li>";
            ulContent += "<li class='li" + (pageNo + 2) + "' ><a href='#'>" + (pageNo + 2) + "</a></li>";
        }
        ulContent += "<li><a href='#'>&gt;&gt;</a></li>";
        ulContent += "</ul>";

        $(".pagination").html(ulContent);
        $(".pagination li a").click(function () {
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
            var rowIndex = index / 5;

            if (mod === 0) {
                table += "<tr>";
            }

            table += "<td width='20%'><div>";
            table += "<div class='image-container' name='smallImage'>";
            table += "<img class='img-polaroid' src='" + this.thumbnailPic + "' title='" + this.text + "'>";
            table += "<input type='checkbox' name='batchPics' class='image-checkbox' hidden='true' value='" + this.id + "'/></div>";
            table += "<p>" + this.screenName + "</p>";
            table += "<p><a href='#myModal" + index + "' role='button' class='btn btn-success zoomIn' data-toggle='modal' title='查看原图'>";
            table += "<i class='icon-zoom-in icon-white'></i></a>";
            table += "<div id='myModal" + index + "' data-rowindex='" + rowIndex + "' class='modal hide fade' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' aria-hidden='true'>";
            table += "<div class='modal-header'>";
            table += "<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>X</button></div>";
            table += "<div class='modal-body'>";
            table += "<img src='" + this.originalPic + "'></div>";
            table += "</div></p></div></td>";

            if (mod === 4 || index === (statuses.length - 1)) {
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

                $(".modal").on('shown', function () {
                    var firstRowTop = $(".modal").first().position().top;
                    $(this).css("top", firstRowTop);
                    var rowIndex = parseInt($(this).data("rowindex"));
                    $(this).css("top", firstRowTop + 200 * rowIndex);
                })

                $("[name=smallImage]").click(function () {
                    var checkbox = $(this).find("[name=batchPics]");
                    checkbox.prop("checked", !checkbox.prop("checked"));

                });
            });
    }

    $(".pagination li a").click(function () {
        createPagination(this);
        getPictures();
    });

    $("#batchSaveBtn").click(function () {
        $("[name=batchPics]").toggle();
        $("#fullSelectSpan").toggle();
    });

    $("#fullSelect").click(function () {
        var checkedSize = $("input[name=batchPics]:checked").length;
        var checkboxSize = $("input[name=batchPics]").length;
        if (checkedSize === 0 || (checkedSize > 0 && checkedSize != checkboxSize)) {
            $("input[name=batchPics]").prop("checked", true);
        }

        if (checkedSize === checkboxSize) {
            $("input[name=batchPics]").prop("checked", false);
        }
    });

    $("#saveBtn").click(function () {


    });

    $(".pagination li.li1 a").click();
    $(".pagination li.li1 a").off("click");

});
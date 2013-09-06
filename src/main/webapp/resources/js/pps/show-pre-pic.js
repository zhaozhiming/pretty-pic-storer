$(document).ready(function () {
    $('body').css('background-image', 'url(' + $("#backgroundUrl").val() + ')');

    function setCurrentPageNo(currentPage) {
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

    function createPicPaginationBy(pageNo) {
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

        $("#picturesLabel .pagination").html(ulContent);
        $("#picturesLabel .pagination li a").click(function () {
            createPicPagination(this);
            getPictures();

        });
        $("#picturesLabel .pagination li.li" + pageNo).addClass("active");
        $("#picturesLabel .pagination li.li" + pageNo + " a").off("click");

        if ($("#fullSelectSpan").is(":visible")) {
            $("#fullSelectSpan").toggle();
            $("[name=batchPics]").toggle();
        }
    }

    function createPicPagination(currentPage) {
        var currentPageJquery = $(currentPage);
        var pageNo = setCurrentPageNo(currentPageJquery);
        createPicPaginationBy(pageNo);
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

    var currentPageNo = 1;
    function createTaskPagination() {
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

    WB2.init({
        'access_token': $("#token").val()
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
                urlTd = "任务还未处理，请稍后查看";
                break;
            case "running":
                tblRow += "<tr class='warning'>"
                statusTd = "处理中";
                urlTd = "任务处理中，请稍后查看";
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
        createTaskPagination();
    }

    function queryUserTasks() {
        $.ajax({
            url: $("#userTasksUrl").val(),
            type: "GET",
            dataType: "text"
        }).done(function (data) {
                var tasks = jQuery.parseJSON(data);
                putTasksToTable(tasks);
            });
    }

    $("#saveBtn").click(function () {
        var statueIds = "";
        if ($("[name=batchPics]:checked").length === 0) {
            $("#error").html("请选择至少一张图片").show().fadeOut(4000);
            return;
        }

        $('[name=batchPics]:checked').each(function (i) {
            statueIds += $(this).val() + ";";
        });

        var ajaxData = {
            token: $("#token").val(),
            currentUid: $("#currentUid").val(),
            statueIds: statueIds
        };

        $.ajax({
            url: $("#saveUrl").val(),
            type: "POST",
            data: ajaxData,
            dataType: "text",
            beforeSend: function () {
                $('#dialogModal').modal({
                    keyboard: false
                });
                $("#dialogModalBody").html("处理中...请稍候");
            }
        }).done(function (data) {
                $("#dialogModalBody").html("已为您创建保存图片任务，请到任务标签页下载");
                var okBtn = '<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">关闭</button>';
                $("#dialogModalFooter").html(okBtn);
            }).fail(function () {
                $("#dialogModalBody").html("出错了！");
                $("#dialogModalFooter").html('<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">关闭</button>');
            });

    });

    $('#taskLabelHref').on('shown', function (e) {
        queryUserTasks();
    });

    createPicPaginationBy(1);
    getPictures();
});
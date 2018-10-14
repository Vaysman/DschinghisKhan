$(document).ready(function () {
    $(".list-group").each(function (index, listGroup) {
        $(this).find("input").keyup(function () {
            let inputValue = $(this).val().toUpperCase();
            $(listGroup).find("a.list-group-item").each(function (index,groupItem) {
                if ($(groupItem).text().toUpperCase().includes(inputValue)) {
                    $(groupItem).show();
                } else {
                    $(groupItem).hide();
                }
            });
        })
    });
});
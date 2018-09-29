$(document).ready(function () {
    $("#editUser").on("click",function () {
        $(this).prop("disabled", true);
        $(this).html("<i class='fa fa-refresh fa-spin' aria-hidden='true'></i> "+$(this).text());
        let that = this;
        let userData = {};

        let userId = $("#userId").val();
        let userEmail = $("#userEmail").val();
        let username = $("#username").val();
        let newPassword = $("#newPassword").val();

        if (userEmail!=null && userEmail!=""){
            userData.email = userEmail;
        }

        if(username!=null && username!=""){
            userData.username = username;
        }

        if(newPassword!=null && newPassword!=""){
            userData.passAndSalt=newPassword;
            userData.salt="";
        }

        $.ajax({
            url: `/api/users/${userId}`,
            type: "PATCH",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data:
                JSON.stringify(userData),
            success: function (response) {
                $(that).html("<i class='fa fa-check'></i> Обновлено!");
                $(that).prop("disabled", false);
            }})
    });

    $("#editCompany").on("click",function () {
        $(this).prop("disabled", true);
        $(this).html("<i class='fa fa-refresh fa-spin' aria-hidden='true'></i> "+$(this).text());

        let companyData = {};

        let companyId = $("#companyId").val();
        companyData.shortName = $("#shortName").val();
        companyData.atiCode = $("#atiCode").val();
        companyData.email = $("#companyEmail").val();
        companyData.ocved = $("#ocved").val();
        companyData.ocpo = $("#ocpo").val();
        companyData.ogrn = $("#ogrn").val();
        companyData.kpp = $("#kpp").val();
        companyData.bik = $("#bik").val();
        companyData.corresAccount = $("#corresAccount").val();
        companyData.curAccount = $("#curAccount").val();
        companyData.bankName = $("#bankName").val();
        companyData.directorFullname = $("#directorFullname").val();
        companyData.chiefAccFullname = $("#chiefAccFullname").val();

        for(let key in companyData){
            if (companyData[key]==null || companyData[key]==""){
                delete companyData[key];
            }
        }

        //i hate this
        let that = this;
        $.ajax({
            url: `/api/companies/${companyId}`,
            type: "PATCH",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data:
                JSON.stringify(companyData),
            success: function (response) {
                $(that).html("<i class='fa fa-check'></i> Обновлено!");
                $(that).prop("disabled", false);
            }})


    });

    $("#editPoint").on("click", function () {
        $(this).prop("disabled", true);
        $(this).html("<i class='fa fa-refresh fa-spin' aria-hidden='true'></i> "+$(this).text());
        let that = this;
        let pointData = {};

        let pointId = $("#pointId").val();
        pointData.name = $("#pointName").val();
        pointData.address = $("#pointAddress").val();

        $.ajax({
            url: `/api/points/${pointId}`,
            type: "PATCH",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data:
                JSON.stringify(pointData),
            success: function (response) {
                $(that).html("<i class='fa fa-check'></i> Обновлено!");
                $(that).prop("disabled", false);
            }})
    });

    $("#editContact").on("click", function () {
        $(this).prop("disabled", true);
        $(this).html("<i class='fa fa-refresh fa-spin' aria-hidden='true'></i> "+$(this).text());
        let that = this;
        let contactData = {};

        let contactId = $("#contactId").val();

        contactData.phone = $("#phone").val();
        contactData.name = $("#contactName").val();
        contactData.email = $("#contactEmail").val();
        contactData.position = $("#position").val();

        $.ajax({
            url: `/api/contacts/${contactId}`,
            type: "PATCH",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data:
                JSON.stringify(contactData),
            success: function (response) {
                $(that).html("<i class='fa fa-check'></i> Обновлено!");
                $(that).prop("disabled", false);
            }})

    });

    let checkMap;

    ymaps.ready(function () {
        if (typeof ymaps !== 'undefined') {
            checkMap = new ymaps.Map('addressCheckMap', {
                center: [40, 50],
                zoom: 15
            }, {
                searchControlProvider: 'yandex#search'
            });
        } else {
            console.log("ymaps is undefined (????)");
        }
    });
    $("#checkAddress").on("click",function() {

        if (typeof ymaps !== 'undefined') {
            let address = $('#pointAddress').val();
            console.log(address);

            ymaps.geocode(address).then(function (coords) {
                console.log(coords.geoObjects.get(0));
                checkMap.setCenter(coords.geoObjects.get(0).geometry._coordinates, 15, {
                    checkZoomRange: true
                });
                checkMap.geoObjects.add(coords.geoObjects.get(0));
            });
            $('#addressCheckMap').show();

        } else {
        }
    });

    $("#loadFromOgrn").on("click", function(){
        let inn = $("#inn").val();
        $.ajax({
                url:`https://creditnet.ru/nkbrelation/api/company?ogrn=${inn}`,
                type: "GET",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function (response) {
                    console.log(response);
                }})
    })
});
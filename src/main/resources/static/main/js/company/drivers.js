// <th>id</th>
// <th>ФИО</th>
// <th>Телефон</th>
// <th>Паспорт</th>
// <th>Водительское удостверение</th>
// <th>Рейтинг</th>
// <th>Мобильное приложение</th>
// <th>Отслеживание по телефону</th>
// <th>В штате</th>
// <th>Оплата</th>

$(document).ready(function () {

    $("#uploadDriverDocument").click(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();


        // Get form
        var form = $('#driverDocumentUploadForm')[0];

        // Create an FormData object
        var data = new FormData(form);

        // If you want to add an extra field for the FormData
        // data.append("CustomField", "This is some extra data, testing");

        let id = data.get("id");
        console.log(id);
        console.log(data);


        // disabled the submit button
        $("#uploadDriverDocument").prop("disabled", true);

        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: `/upload/driver/${id}`,
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {

                $("#uploadDriverDocument").prop("disabled", false);
                $("#driverDocumentUploadModal").modal('hide');
                document.getElementById("driverDocumentUploadForm").reset();
                $("#driverDocumentUploadError").text("")
            },
            error: function (e) {
                console.log("ERROR : ", e);
                if(e.responseJSON.message.includes("Maximum upload size exceeded")){
                    $("#driverDocumentUploadError").text("Ошибка: файл слишком большой")
                } else{
                    $("#driverDocumentUploadError").text(e.responseJSON.message);
                }
                $("#uploadDriverDocument").prop("disabled", false);
            }
        });

    });

    let driverEditor = new $.fn.dataTable.Editor({
        table: '#driverDataTable',
        idSrc: 'id',
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/drivers',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.originator = currentCompanyId;
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    driverDataTable.draw();
                    driverEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/drivers/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    driverDataTable.draw();
                    driverEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/drivers/_id_',
                data: function (d) {
                    return '';
                }
            }
        },
        fields: [
            {
                label: 'ФИО',
                name: 'name',
                type: 'text'
            },
            {
                label: 'Телефон',
                name: 'phone',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'Номер паспорта',
                name: 'passportNumber',
                type: 'mask',
                mask: '00 00 000000'
            },
            {
                label: 'Номер водительского удостверения',
                name: 'licenseNumber',
                type: 'mask',
                mask: '00 00 000000'
            },
            {
                label: 'Рейтинг',
                name: 'rating',
                type: 'mask',
                mask: '######',
                placeholder: ""
            },
            {
                label: 'Мобильное приложение', name: 'hasMobileApp', type: "radio", options: [{ label:"Есть", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Телефон для приложения',
                name: 'mobileAppNumber',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'Отслеживание по телефону', name: 'isTracked', type: "radio", options: [{ label:"Есть", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Телефон для отслеживания',
                name: 'trackingNumber',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'В штате', name: 'isHired', type: "radio", options: [{ label:"Да", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Способ оплаты',
                name: 'paymentType',
                type: 'selectize',
                options: paymentTypeOptions,
                opts: {
                    searchField: "label", create: false
                }
            }
        ]

    });


    var driverDataTable = $("#driverDataTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/driversForUser", // json datasource
                type: "post"  // method  , by default get
            },
            dom: 'Btp',
            language: {
                url: '/localization/dataTablesRus.json'
            },
            select: {
                style: 'single'
            },
            "buttons": [
                {
                    extend: "create",
                    editor: driverEditor
                },
                {
                    extend: "edit",
                    editor: driverEditor
                },
                {
                    extend: 'selectedSingle',
                    text: '<i class="fa fa-file"></i> Прикрепить документ',
                    action: function (e, dt, node, config) {
                        $("#driverDocumentUploadError").text("")
                        document.getElementById("driverDocumentUploadForm").reset();
                        $("#driverIdInput").val(dt.rows( { selected: true } ).data()[0].id);
                        $("#driverDocumentUploadModal").modal();
                        console.log(dt.rows( { selected: true } ).data()[0].id);
                    }
                },
                {
                    extend: "remove",
                    editor: driverEditor
                },
                {
                    text: 'Показать все',
                    action: function () {
                        driverDataTable.page.len(-1).draw();
                    }
                },{
                    extend: "excelHtml5",
                    text: "<i class='fa fa-file-excel-o'></i> Экспорт",
                    title: `Водители ${new Date().getDate()}.${(new Date().getMonth()+1)}.${new Date().getFullYear()}`
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "name", "data": "name", "targets": 1},
                {"name": "phone", "data": "phone", searchable: false, orderable: false, "targets": 2},
                {"name": "passportNumber", "data": "passportNumber", searchable: false, orderable: false, "targets": 3},
                {"name": "licenseNumber", "data": "licenseNumber", searchable: false, orderable: false, "targets": 4},
                {"name": "rating", "data": "rating", searchable: false, "targets": 5},
                {
                    "name": "hasMobileApp",
                    "data": "hasMobileApp",
                    "targets": 6,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Есть" : "Нет"
                    }
                },
                {"name": "mobileAppNumber", "data": "mobileAppNumber", searchable: false, orderable: false, "targets": 7},
                {
                    "name": "isTracked",
                    "data": "isTracked",
                    "targets": 8,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Да" : "Нет"
                    }
                },
                {"name": "trackingNumber", "data": "trackingNumber", searchable: false, orderable: false, "targets": 9},
                {
                    "name": "isHired",
                    "data": "isHired",
                    "targets": 10,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Да" : "Нет"
                    }
                },
                {
                    "name": "paymentType",
                    "data": "paymentType",
                    "targets": 11,
                    searchable: false,
                    orderable: false
                }
            ]
        }
    );
});
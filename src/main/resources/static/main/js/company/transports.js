$(document).ready(function () {

    $("#uploadTransportDocument").click(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();


        // Get form
        var form = $('#transportDocumentUploadForm')[0];

        // Create an FormData object
        var data = new FormData(form);

        // If you want to add an extra field for the FormData
        // data.append("CustomField", "This is some extra data, testing");

        let id = data.get("id");
        console.log(id);
        console.log(data);


        // disabled the submit button
        $("#submit").prop("disabled", true);

        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: `/upload/transport/${id}`,
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {

                $("#uploadTransportDocument").prop("disabled", false);
                $("#transportDocumentUploadModal").modal('hide');
                document.getElementById("transportDocumentUploadForm").reset();

            },
            error: function (e) {
                console.log("ERROR : ", e);
                $("#uploadTransportDocument").prop("disabled", false);
                $("#transportDocumentUploadModal").modal('hide');
                document.getElementById("transportDocumentUploadForm").reset();

            }
        });

    });

    let transportEditor = new $.fn.dataTable.Editor({
        table: '#transportDataTable',
        idSrc: 'id',
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/transports',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.originator = currentCompanyId;
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    transportDataTable.draw();
                    transportEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/transports/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    transportDataTable.draw();
                    transportEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/transports/_id_',
                data: function (d) {
                    return '';
                }
            }
        },
        fields: [
            {
                label: 'Номер',
                name: 'number',
                type: 'text'
            },
            {
                label: 'Тип',
                name: 'type',
                type: 'selectize',
                options: vehicleTypeOptions,
                opts: {
                    searchField: "label", create: false
                }
            },
            {
                label: 'Тип кузова',
                name: 'bodyType',
                type: 'selectize',
                options: vehicleBodyTypeOptions,
                opts: {
                    searchField: "label", create: false
                }
            },
            {
                label: 'Тип погрузки',
                name: 'loadingType',
                type: 'selectize',
                options: loadingTypeOptions,
                opts: {
                    searchField: "label", create: false, maxItems: 3,
                }
            },
            {
                label: 'Тоннаж (т)', name: 'tonnage', type: "mask", mask: "###", placeholder:""
            },
            {
                label: 'Объем (м<sup>3</sup>)', name: 'volume', type: "mask", mask: "###", placeholder:""
            },
            {
                label: 'Коники', name: 'conics', type: "radio", options: [{ label:"Есть", value:true}, {label:"Отсутствует",value:false}]
            },
            {
                label: 'Гидроборт', name: 'hydrobort', type: "radio", options: [{ label:"Есть", value:true}, {label:"Отсутствует",value:false}]
            },
            {
                label: 'GPS', name: 'isGps', type: "radio", options: [{ label:"Есть", value:true}, {label:"Отсутствует",value:false}]
            },
            {
                label: 'Комментарий', name: 'comment', type: "textarea"
            }
        ]

    });


    var transportDataTable = $("#transportDataTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/transportsForUser", // json datasource
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
                    editor: transportEditor
                },
                {
                    extend: "edit",
                    editor: transportEditor
                },
                {
                    extend: 'selectedSingle',
                    text: '<i class="fa fa-file"></i> Прикрепить документ',
                    action: function (e, dt, node, config) {
                        $("#transportIdInput").val(dt.rows( { selected: true } ).data()[0].id);
                        $("#transportDocumentUploadModal").modal();
                        console.log(dt.rows( { selected: true } ).data()[0].id);
                    }
                },
                {
                    extend: "remove",
                    editor: transportEditor
                },
                {
                    text: 'Показать все',
                    action: function () {
                        transportDataTable.page.len(-1).draw();
                    }
                },{
                    extend: "excelHtml5",
                    text: "<i class='fa fa-file-excel-o'></i> Экспорт",
                    title: `Транспорт ${new Date().getDate()}.${(new Date().getMonth()+1)}.${new Date().getFullYear()}`
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "number", "data": "number", "targets": 1},
                {"name": "type", "data": "type", searchable: false, orderable: false, "targets": 2},
                {
                    "name": "bodyType",
                    "data": "bodyType",
                    "targets": 3,
                    searchable: false,
                    orderable: false
                },
                {
                    "name": "loadingType",
                    "data": "loadingType[, ]",
                    searchable: false,
                    orderable: false,
                    "targets": 4
                },
                {
                    "name": "tonnage",
                    "data": "tonnage",
                    "targets": 5,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "volume",
                    "data": "volume",
                    "targets": 6,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "conics",
                    "data": "conics",
                    "targets": 7,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Есть" : "Нет"
                    }
                },
                {
                    "name": "hydrobort",
                    "data": "hydrobort",
                    "targets": 8,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Есть" : "Нет"
                    }
                },
                {
                    "name": "isGps",
                    "data": "isGps",
                    "targets": 9,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Есть" : "Нет"
                    }
                },
                {
                    "name": "wialonId",
                    "data": "wialonId",
                    "targets": 10,
                    searchable: false,
                    orderable: false,
                    fieldInfo: "В случае, если есть отслеживание по GPS"
                },
                {
                    "name": "comment",
                    "data": "comment",
                    "targets": 10,
                    searchable: false,
                    orderable: false
                }
            ]
        }
    );
});
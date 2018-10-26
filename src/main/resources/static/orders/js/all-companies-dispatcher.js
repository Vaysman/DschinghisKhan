$(document).ready(function () {
    $('#tab-companies-link a').one('shown.bs.tab', function () {

        $("#uploadContract").click(function (event) {

            //stop submit the form, we will post it manually.
            event.preventDefault();


            // Get form
            var form = $('#contractUploadForm')[0];

            // Create an FormData object
            var data = new FormData(form);

            // If you want to add an extra field for the FormData
            // data.append("CustomField", "This is some extra data, testing");

            let id = data.get("id");
            console.log(id);
            console.log(data);


            // disabled the submit button
            $("#uploadContract").prop("disabled", true);

            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: `/upload/contract/${id}`,
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                timeout: 600000,
                success: function (data) {

                    $("#uploadContract").prop("disabled", false);
                    $("#contractUploadModal").modal('hide');
                    document.getElementById("contractUploadForm").reset();
                    $("#contractUploadError").text("")

                },
                error: function (e) {
                    console.log("ERROR : ", e);
                    $("#uploadContract").prop("disabled", false);
                    if(e.responseJSON.message.includes("Maximum upload size exceeded")){
                        $("#contractUploadError").text("Ошибка: файл слишком большой")
                    } else{
                        $("#contractUploadError").text(e.responseJSON.message);
                    }
                }
            });

        })

        let companyInfoEditor = new $.fn.dataTable.Editor({
            ajax: {
                edit: {
                    contentType: 'application/json',
                    type: 'PATCH',
                    url: 'api/companies/_id_',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            newdata = JSON.stringify(value);
                        });
                        console.log(newdata);
                        return newdata;
                    },
                    success: function (response) {
                        companiesTable.draw();
                        companyEditor.close();
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                }
            },
            fields: [
                {label: 'Кол-во наемного  транспорта', name: 'numberOfTransports', type: 'mask', mask: "###"},
                {label: 'Код ati.su', name: 'atiCode', type: 'text'},
                {label: 'ФИО главного бухгалтера', name: 'accountantName', type: 'text'},
                {label: 'ОКВЕД', name: 'ocved', type: 'text'},
                {label: 'ОКПО', name: 'ocpo', type: 'text'},
                {label: 'ОГРН', name: 'ogrn', type: 'text'},
                {
                    label: 'Налогообложение',
                    name: 'taxationType',
                    type: 'selectize',
                    options: [{label: "С НДС", value: "С НДС"}, {label: "Без НДС", value: "Без НДС"}]
                }
            ],
            table: '#transportCompaniesTable',
            idSrc: 'id',
        });

        let companyEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/companies',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            value.originator = currentCompanyId;
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {
                        companiesTable.draw();
                        companyEditor.close();
                        // alert(response.responseText);
                    },
                    error: function (jqXHR, exception) {
                        console.log(jqXHR);
                        console.log(exception);
                        var cause = jqXHR.responseJSON.cause.cause.message;
                        if (cause.includes("Duplicate entry")){
                            companyEditor.field("inn").error("Указанный ИНН уже существует");
                        } else {
                            companyInfoEditor.field("email").error(cause);
                        }

                    }
                }
            },
            table: '#transportCompaniesTable',
            idSrc: 'id',

            fields: [

                {label: 'Название', name: 'name', type: 'text'},
                {label: 'ИНН', name: 'inn', type: 'text'},
                {
                    label: "E-mail",
                    name: "email",
                    type: 'text',
                    fieldInfo: "На данный адрес будет отправлен логин и пароль"
                }
            ]
        });

        var companiesTable = $("#transportCompaniesTable").DataTable({
                processing: true,
                serverSide: true,
                searchDelay: 800,
                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/transportCompanies", // json datasource
                    type: "post"  // method  , by default get
                },
                dom: 'Bfrtip',
                language: {
                    url: '/localization/dataTablesRus.json'
                },
                select: {
                    style: 'single'
                },
                "buttons": [
                    {
                        extend: "create",
                        editor: companyEditor
                    },{
                        extend: 'selectedSingle',
                        text: '<i class="fa fa-file"></i> Прикрепить договор',
                        action: function (e, dt, node, config) {
                            $("#contractUploadError").text("");
                            document.getElementById("contractUploadForm").reset();
                            $("#companyIdInput").val(dt.rows( { selected: true } ).data()[0].id);
                            $("#contractUploadModal").modal();
                            console.log(dt.rows( { selected: true } ).data()[0].id);
                        }
                    }
                ],
                "paging": 10,
                "columnDefs": [

                    {"name": "id", "data": "id", "targets": 0, visible: false},
                    {
                        "name": "name", "data": "name", "targets": 1,
                        render: function(data, type, full){
                            return `<a target="_blank" href='info/company/${full.id}'>${data}</a>`;
                        }
                    },
                    {"name": "shortName", "data": "shortName", "targets": 2},
                    {"name": "inn", "data": "inn", "targets": 3, defaultContent: ""},
                    {
                        "name": "numberOfTransports",
                        "data": "numberOfTransports",
                        "targets": 4,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "accountantName",
                        "data": "accountantName",
                        "targets": 5,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "ocved",
                        "data": "ocved",
                        "targets": 6,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {"name": "ocpo", "data": "ocpo", "targets": 7, defaultContent: "", orderable: false, searchable: false},
                    {"name": "ogrn", "data": "ogrn", "targets": 8, defaultContent: "", orderable: false, searchable: false},
                    {
                        "name": "email",
                        "data": "email",
                        "targets": 9,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "taxationType",
                        "data": "taxationType",
                        "targets": 10,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                ],
                createdRow: function (row, data, dataIndex) {
                    if(sentContracts.some(contract => contract.companyId==data.id)){
                        $(row).addClass("table-warning");
                    }
                    if (data.originator == currentCompanyId) {
                        $(row).addClass("table-success");
                    }
                },
            },
        );

        companiesTable.on('select', function (e, dt, type, indexes) {
            if (type === 'row') {
                var data = companiesTable.rows(indexes).data('id');

                if(currentCompanyId==data[0].originator){
                    $('#companyContactsTableWrapper').show();
                    reInitCompanyContactsTable(data[0].id);
                } else {
                    $('#companyContactsTableWrapper').hide();
                }

            }
        });

        function reInitCompanyContactsTable(id) {
            $.get(`api/companies/${id}`).success(function (companyData) {
                let companyHref = companyData._links.self.href;

                var companyContactsEditor = new $.fn.dataTable.Editor({
                    ajax: {
                        create: {
                            type: 'POST',
                            contentType: 'application/json',
                            url: 'api/contacts',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    value.originator = currentCompanyId;
                                    value.company = companyHref;
                                    value.type="SECONDARY";
                                    newdata = JSON.stringify(value);
                                });
                                return newdata;
                            },
                            success: function (response) {
                                companyContactsTable.draw();
                                companyContactsEditor.close();
                                // alert(response.responseText);
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        },
                        edit: {
                            contentType: 'application/json',
                            type: 'PATCH',
                            url: 'api/contacts/_id_',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    newdata = JSON.stringify(value);
                                });
                                console.log(newdata);
                                return newdata;
                            },
                            success: function (response) {
                                companyContactsTable.draw();
                                companyContactsEditor.close();
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        }
                        ,
                        remove: {
                            type: 'DELETE',
                            contentType: 'application/json',
                            url: 'api/contacts/_id_',
                            data: function (d) {
                                return '';
                            }
                        }
                    },
                    table: '#companyContactsTable',
                    idSrc: 'id',

                    fields: [
                        {label: 'ФИО контакта', name: 'name', type: 'text', attr:{maxlength: 128}},
                        {label: 'Телефон', name: 'phone', type: "text", attr:{maxlength: 20,placeholder:"+7 (000) 000 00-00"}},
                        {label: 'E-mail', name: 'email', type: "text", attr:{maxlength: 64}},
                        {label: 'Должность', name: 'position', type: "text", attr:{maxlength: 64}},
                    ]
                });

                var companyContactsTable = $("#companyContactsTable").DataTable({
                        processing: true,
                        serverSide: true,
                        destroy: true,
                        searchDelay: 800,
                        ajax: {
                            contentType: 'application/json',
                            processing: true,
                            data: function (d) {
                                return JSON.stringify(d);
                            },
                            url: `dataTables/contactsForCompany/${id}`, // json datasource
                            type: "post"  // method  , by default get
                        },
                        dom: 'Bfrtip',
                        language: {
                            url: '/localization/dataTablesRus.json'
                        },
                        select: {
                            style: 'single'
                        },
                        "buttons": [
                            {
                                extend: "create",
                                editor: companyContactsEditor
                            },
                            {
                                extend: "edit",
                                editor: companyContactsEditor
                            },
                            {
                                extend: "remove",
                                editor: companyContactsEditor
                            }
                        ],
                        "paging": 10,
                        "columnDefs": [
                            {"name": "id", "data": "id", "targets": 0, visible: false},
                            {"name": "name", "data": "name", "targets": 1},
                            {"name": "phone", "data": "phone", "targets": 2},
                            {"name": "email", "data": "email", "targets": 3},
                            {"name": "position", "data": "position", "targets": 4},
                        ]
                    }
                );

            });


        }

    });
});
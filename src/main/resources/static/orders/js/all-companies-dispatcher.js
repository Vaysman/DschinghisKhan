$(document).ready(function () {
    $('#tab-companies-link a').one('shown.bs.tab', function () {
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
                    }
                ],
                "paging": 10,
                "columnDefs": [

                    {"name": "id", "data": "id", "targets": 0, visible: false},
                    {
                        "name": "name", "data": "name", "targets": 1
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
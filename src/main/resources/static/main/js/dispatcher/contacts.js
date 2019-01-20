$(document).ready(function () {
    $("#clientSelectForContacts").selectize({
            maxItems: 1,
            valueField: "value",
            labelField: "label",
            searchField: "label", create: false, placeholder: "Выберите...",
            delimiter: null,
            load: function (query, callback) {
                $.get(`api/clients/search/findTop10ByOriginatorAndNameContaining/?name=${query}&originator=${currentCompanyId}`,
                    function (data) {
                        let clientOptions = [];
                        data._embedded.clients.forEach(function (entry) {
                            clientOptions.push({
                                "label": entry.name,
                                "value": entry.id
                            });
                        });
                        callback(clientOptions);
                    }
                )
            },
            onChange: function (value) {
                reInitContactsTable(value);
            },
            preload: true,
            loadThrottle: 500
        }
    );

    function reInitContactsTable(clientId) {
        if (!clientId) return;
        $.get(`api/clients/${clientId}`).success(function (clientData) {
            let clientHref = clientData._links.self.href;
            let contactsEditor = new $.fn.dataTable.Editor({
                ajax: {
                    create: {
                        type: 'POST',
                        contentType: 'application/json',
                        url: 'api/contacts',
                        data: function (d) {
                            let newdata;
                            $.each(d.data, function (key, value) {
                                value.client = clientHref;
                                value.originator = currentCompanyId;
                                value.type = "RECEIVER";
                                newdata = JSON.stringify(value);
                            });
                            return newdata;
                        },
                        success: function (response) {
                            contactsDataTable.draw();
                            contactsEditor.close();
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
                                if (value['point'] == "") {
                                    delete value['point'];
                                }
                                if (value['company'] == "") {
                                    delete value['company'];
                                }
                                newdata = JSON.stringify(value);
                            });
                            console.log(newdata);
                            return newdata;
                        },
                        success: function (response) {
                            contactsDataTable.draw();
                            contactsEditor.close();
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
                table: '#contactsTable',
                idSrc: 'id',

                fields: [
                    {label: "Название компании", name: "companyName", type: 'text', attr: {maxlength: 128}},
                    {label: 'ФИО', name: 'name', type: 'text', attr: {maxlength: 128}},
                    {
                        label: 'Пункт', name: 'point', type: 'selectize', options: [], opts: {
                            searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
                            delimiter: null,
                            load: function (query, callback) {
                                $.get(`api/points/search/findTop10ByClientIdAndNameContaining/?name=${query}&clientId=${clientId}`,
                                    function (data) {
                                        let pointOptions = [];
                                        data._embedded.points.forEach(function (entry) {
                                            pointOptions.push({"label": entry.name, "value": entry._links.self.href});
                                        });
                                        callback(pointOptions);
                                    }
                                )
                            },
                            preload: true,
                            loadThrottle: 500
                        }
                    },
                    {
                        label: 'Телефон',
                        name: 'phone',
                        type: "text",
                        attr: {maxlength: 20, placeholder: "+7 (000) 000 00-00"}
                    },
                    {label: 'E-mail', name: 'email', type: "text", attr: {maxlength: 64}},
                    {label: 'Должность', name: 'position', type: "text", attr: {maxlength: 64}},
                ]
            });

            var contactsDataTable = $("#contactsTable").DataTable({
                    processing: true,
                    serverSide: true,
                    searchDelay: 800,
                    destroy: true,
                    ajax: {
                        contentType: 'application/json',
                        processing: true,
                        data: function (d) {
                            return JSON.stringify(d);
                        },
                        url: `dataTables/contactsForClient/${clientId}`, // json datasource
                        type: "post"  // method  , by default get
                    },
                    dom: 'Bfrtp',
                    language: {
                        url: '/localization/dataTablesRus.json'
                    },
                    select: {
                        style: 'single'
                    },
                    "buttons": [
                        {
                            extend: "create",
                            editor: contactsEditor
                        },
                        {
                            extend: "edit",
                            editor: contactsEditor
                        },
                        {
                            extend: "remove",
                            editor: contactsEditor
                        },
                        {
                            text: 'Показать все',
                            action: function () {
                                contactsDataTable.page.len(-1).draw();
                            }
                        }, {
                            extend: "excelHtml5",
                            text: "<i class='fa fa-file-excel-o'></i> Экспорт",
                            title: `Контакты ${new Date().getDate()}.${(new Date().getMonth() + 1)}.${new Date().getFullYear()}`
                        }
                    ],
                    "paging": 10,
                    "columnDefs": [
                        {"name": "id", "data": "id", "targets": 0, visible: false},
                        {name: "companyName", data: "companyName", targets: 1},
                        {"name": "name", "data": "name", "targets": 2},
                        {"name": "point.address", "data": "point.address", "targets": 3, defaultContent: ""},
                        {"name": "phone", "data": "phone", "targets": 4},
                        {"name": "email", "data": "email", "targets": 5},
                        {"name": "position", "data": "position", "targets": 6},
                        // {"name": "company.name", "data": "company.name", "targets": 6, defaultContent:""},
                    ]
                }
            );
        });
    }
});


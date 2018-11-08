$(document).ready(function () {
    $('#tab-contacts-link a').one('shown.bs.tab', function () {
        let clientsEditor = null;
        clientsEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/clients',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            value.originator = currentCompanyId;
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {
                        clientsDataTable.draw();
                        clientsEditor.close();
                        // alert(response.responseText);
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                },
                edit: {
                    contentType: 'application/json',
                    type: 'PATCH',
                    url: 'api/clients/_id_',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {
                        clientsDataTable.draw();
                        clientsEditor.close();
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                }
                ,
                remove: {
                    type: 'DELETE',
                    contentType: 'application/json',
                    url: 'api/clients/_id_',
                    data: function (d) {
                        return '';
                    }
                }
            },
            table: '#clientsTable',
            idSrc: 'id',

            fields: [
                {label: 'Наименование', name: 'name', type: 'text', attr: {maxlength: 120}},
                {
                    label: 'Телефон',
                    name: 'phone',
                    type: 'mask',
                    mask: '+7 (000)-000-00-00'
                },
                {
                    label: 'Контактное лицо',
                    name: 'contact',
                    type: 'text',
                    attr: {maxlength: 256}
                }
            ]
        });

        var clientsDataTable = $("#clientsTable").DataTable({
                processing: true,
                serverSide: true,
                searchDelay: 800,
                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/clientsForUser", // json datasource
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
                        editor: clientsEditor
                    },
                    {
                        extend: "edit",
                        editor: clientsEditor
                    },
                    {
                        extend: "remove",
                        editor: clientsEditor
                    }
                ],
                "paging": 10,
                "columnDefs": [
                    {"name": "id", "data": "id", "targets": 0, visible: false},
                    {"name": "name", "data": "name", "targets": 1},
                    {"name": "phone", "data": "phone", "targets": 2},
                    {"name": "contact", "data": "contact", "targets": 3}
                ]
            }
        );

        clientsDataTable.on('select', function (e, dt, type, indexes) {
            if (type === 'row') {
                var data = clientsDataTable.rows(indexes).data('id');
                reInitPointsAndContactsTable(data[0].id);
            }
        });


        function reInitPointsAndContactsTable(clientId) {
            let pointsEditor = null;
            $.get(`api/clients/${clientId}`).success(function (clientData) {
                let clientHref = clientData._links.self.href;
                pointsEditor = new $.fn.dataTable.Editor({
                    ajax: {
                        create: {
                            type: 'POST',
                            contentType: 'application/json',
                            url: 'api/points',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    console.log(clientHref);
                                    value.client = clientHref;
                                    value.originator = currentCompanyId;
                                    newdata = JSON.stringify(value);
                                });
                                return newdata;
                            },
                            success: function (response) {
                                pointsDataTable.draw();
                                pointsEditor.close();
                                // alert(response.responseText);
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        },
                        edit: {
                            contentType: 'application/json',
                            type: 'PATCH',
                            url: 'api/points/_id_',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    newdata = JSON.stringify(value);
                                });
                                return newdata;
                            },
                            success: function (response) {
                                pointsDataTable.draw();
                                pointsEditor.close();
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        }
                        ,
                        remove: {
                            type: 'DELETE',
                            contentType: 'application/json',
                            url: 'api/points/_id_',
                            data: function (d) {
                                return '';
                            }
                        }
                    },
                    table: '#pointsTable',
                    idSrc: 'id',

                    fields: [
                        {label: 'Грузополучатель', name: 'name', type: 'text', attr: {maxlength: 256}},
                        {
                            label: 'Адрес',
                            name: 'address',
                            type: 'text',
                            attr: {maxlength: 256},
                            fieldInfo: "Будет использован только в геосервисах. <br>Не будет показываться диспетчерам/перевозчикам."
                        },
                        {
                            label: 'Полный адрес',
                            name: 'fullAddress',
                            type: 'text',
                            attr: {maxlength: 256},
                            fieldInfo: "Для отображения другим диспетчерам и перевозчикам. "
                        },
                        {label: 'Комментарий', name: 'comment', type: 'textarea', attr: {maxlength: 256}},
                        {label: 'Рабочее время', name: 'workTime', type: 'textarea', attr: {maxlength: 128}},
                    ]
                });

                var pointsDataTable = $("#pointsTable").DataTable({
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
                            url: `dataTables/pointsForClient/${clientId}`, // json datasource
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
                                editor: pointsEditor
                            },
                            {
                                extend: "edit",
                                editor: pointsEditor
                            },
                            {
                                extend: "remove",
                                editor: pointsEditor
                            },
                            {
                                text: 'Показать все',
                                action: function () {
                                    pointsDataTable.page.len(-1).draw();
                                }
                            },
                            {
                                extend: "excelHtml5",
                                text: `<i class='fa fa-file-excel-o'></i> Экспорт`,
                                title: `Пункты ${new Date().getDate()}.${(new Date().getMonth() + 1)}.${new Date().getFullYear()}`
                            }
                        ],
                        "paging": 10,
                        "columnDefs": [
                            {"name": "id", "data": "id", "targets": 0},
                            {"name": "name", "data": "name", "targets": 1},
                            {"name": "address", "data": "address", "targets": 2},
                            {"name": "fullAddress", "data": "fullAddress", "targets": 3},
                            {"name": "comment", "data": "comment", "targets": 4},
                            {"name": "workTime", "data": "workTime", targets: 5}
                        ]
                    }
                );


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
                                    value.type="RECEIVER";
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
                        {label: "Название компании", name:"companyName", type:'text', attr:{maxlength:128}},
                        {label: 'ФИО', name: 'name', type: 'text', attr:{maxlength: 128}},
                        {
                            label: 'Пункт', name: 'point', type: 'selectize', options: [], opts: {
                                searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
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
                        {label: 'Телефон', name: 'phone', type: "text", attr:{maxlength: 20,placeholder:"+7 (000) 000 00-00"}},
                        {label: 'E-mail', name: 'email', type: "text", attr:{maxlength: 64}},
                        {label: 'Должность', name: 'position', type: "text", attr:{maxlength: 64}},
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
                            },{
                                extend: "excelHtml5",
                                text: "<i class='fa fa-file-excel-o'></i> Экспорт",
                                title: `Контакты ${new Date().getDate()}.${(new Date().getMonth()+1)}.${new Date().getFullYear()}`
                            }
                        ],
                        "paging": 10,
                        "columnDefs": [
                            {"name": "id", "data": "id", "targets": 0, visible: false},
                            {name: "companyName", data:"companyName",targets:1},
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
});
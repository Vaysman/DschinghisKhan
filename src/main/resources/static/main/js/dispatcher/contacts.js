$(document).ready(function () {
    $('#tab-contacts-link a').one('shown.bs.tab', function () {
        let contactsEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/contacts',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            value.originator = currentCompanyId;
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
                {label: 'ФИО', name: 'name', type: 'text', attr:{maxlength: 128}},
                {
                    label: 'Пункт', name: 'point', type: 'selectize', options: [], opts: {
                        searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
                        load: function (query, callback) {
                            $.get(`api/points/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
                                function (data) {
                                    let pointOptions = [];
                                    data._embedded.points.forEach(function (entry) {
                                        pointOptions.push({"label": entry.name, "value": entry._links.self.href});
                                    });
                                    callback(pointOptions);
                                }
                            )
                        },
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
                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/contactsForUser", // json datasource
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
                    }
                ],
                "paging": 10,
                "columnDefs": [
                    {"name": "id", "data": "id", "targets": 0, visible: false},
                    {"name": "name", "data": "name", "targets": 1},
                    {"name": "point.address", "data": "point.address", "targets": 2, defaultContent: ""},
                    {"name": "phone", "data": "phone", "targets": 3},
                    {"name": "email", "data": "email", "targets": 4},
                    {"name": "position", "data": "position", "targets": 5},
                ]
            }
        );
    })
});
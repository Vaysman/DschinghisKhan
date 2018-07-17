$(document).ready(function () {
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
            {label: 'Название', name: 'name', type: 'text'},
            {label: 'Адрес', name: 'address', type: 'text'},
            {label: 'Фактический адрес', name: 'factAddress', type: 'text'},
            {label: 'Телефон', name: 'phone', type: "mask", mask:"+7 (000) 000-00-00"},
            {label: 'Кол-во собственного транспорта', name: 'numberOfTransports', type: "mask", mask:"###"},
            {label: 'Налогообложение', name: 'taxation', type: 'text'},
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
                {"name": "address", "data": "address", "targets": 2},
                {"name": "factAddress", "data": "factAddress", "targets": 3},
                {"name": "phone", "data": "phone", "targets": 4},
                {"name": "numberOfTransports", "data": "numberOfTransports", "targets": 5},
                {"name": "taxation", "data": "taxation", "targets": 6},
            ]
        }
    );
});
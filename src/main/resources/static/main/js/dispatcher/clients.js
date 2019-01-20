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

        // clientsDataTable.on('select', function (e, dt, type, indexes) {
        //     if (type === 'row') {
        //         var data = clientsDataTable.rows(indexes).data('id');
        //         reInitPointsAndContactsTable(data[0].id);
        //     }
        // });



    });
});
$(document).ready(function () {
    let pointsEditor = null;
        pointsEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/points',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            value.originator = currentUser.id;
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
                        console.log(newdata);
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
                {label: 'Название', name: 'name', type: 'text'},
                {label: 'Адрес', name: 'address', type: 'text'}
            ]
        });

        var pointsDataTable = $("#pointsTable").DataTable({
                processing: true,
                serverSide: true,
                searchDelay: 800,
                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/pointsForUser", // json datasource
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
                    }
                ],
                "paging": 10,
                "columnDefs": [
                    {"name": "id", "data": "id", "targets": 0, visible: false},
                    {"name": "name", "data": "name", "targets": 1},
                    {"name": "address", "data": "address", "targets": 2},
                ]
            }
        );
});
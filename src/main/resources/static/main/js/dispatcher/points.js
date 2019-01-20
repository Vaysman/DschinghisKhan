$(document).ready(function () {
    $("#clientSelectForPoints").selectize({
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
                            console.log(entry);
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
                reInitPointsTable(value);
            },
            preload: true,
            loadThrottle: 500
        }
    );

    function reInitPointsTable(clientId) {
        if (!clientId) return;
        $.get(`api/clients/${clientId}`).success(function (clientData) {
            let clientHref = clientData._links.self.href;


            let pointsEditor = new $.fn.dataTable.Editor({
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
                    {
                        label: '<span data-tooltip tooltiptext="Будет использован только в геосервисах. Не будет показываться диспетчерам/перевозчикам.">Грузополучатель <i class="fa fa-info"></i></span>',
                        name: 'name',
                        type: 'text',
                        attr: {maxlength: 256}
                    },
                    {
                        label: 'Адрес',
                        name: 'address',
                        type: 'text',
                        attr: {maxlength: 256},
                        // fieldInfo: "Будет использован только в геосервисах. <br>Не будет показываться диспетчерам/перевозчикам."
                    },
                    {
                        label: '<span data-tooltip tooltiptext="Для отображения в документах">Полный адрес <i class="fa fa-info"></i></span>',
                        name: 'fullAddress',
                        type: 'text',
                        attr: {maxlength: 256},
                        // fieldInfo: "Для отображения другим диспетчерам и перевозчикам. "
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

        });
    }

});



$(document).ready(function () {
    // $("#clientSelectForPoints").selectize({
    //         maxItems: 1,
    //         valueField: "value",
    //         labelField: "label",
    //         searchField: "label", create: false, placeholder: "Выберите...",
    //         delimiter: null,
    //         load: function (query, callback) {
    //             $.get(`api/clients/search/findTop10ByOriginatorAndNameContaining/?name=${query}&originator=${currentCompanyId}`,
    //                 function (data) {
    //                     let clientOptions = [];
    //                     data._embedded.clients.forEach(function (entry) {
    //                         console.log(entry);
    //                         clientOptions.push({
    //                             "label": entry.name,
    //                             "value": entry.id
    //                         });
    //                     });
    //                     callback(clientOptions);
    //                 }
    //             )
    //         },
    //         onChange: function (value) {
    //             reInitPointsTable(value);
    //         },
    //         preload: true,
    //         loadThrottle: 500
    //     }
    // );




        let pointsEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/points',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
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
                            if (value['client'] == "") {
                                delete value['client'];
                            }
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
                    label: 'Клиент',
                    name: 'client',
                    type: 'selectize',
                    opts: {
                        maxItems: 1,
                        valueField: "value",
                        labelField: "label",
                        searchField: "label", create: false, placeholder: "Нажмите чтобы изменить",
                        delimiter: null,
                        load: function (query, callback) {
                            $.get(`api/clients/search/findTop10ByOriginatorAndNameContaining/?name=${query}&originator=${currentCompanyId}`,
                                function (data) {
                                    let clientOptions = [];
                                    data._embedded.clients.forEach(function (entry) {
                                        console.log(entry);
                                        clientOptions.push({
                                            "label": entry.name,
                                            "value": entry._links.self.href
                                        });
                                    });
                                    callback(clientOptions);
                                }
                            )
                        },
                        preload: true,
                        loadThrottle: 500
                    }
                },
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
                    url: `dataTables/points`, // json datasource
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
                "columns": [
                    {"name": "id", "data": "id", "title": "id", visible: false},
                    {"name": "client.name", "data": "client.name", "title": "Клиент",defaultContent: ""},
                    {"name": "name", "data": "name", "title": "Наименование"},
                    {"name": "address", "data": "address", "title": "Адрес"},
                    {"name": "fullAddress", "data": "fullAddress", "title": "Полный адрес"},
                    {"name": "comment", "data": "comment", "title": "Комментарий"},
                    {"name": "workTime", "data": "workTime", title: "Рабочее время"}
                ]
            }
        );


});



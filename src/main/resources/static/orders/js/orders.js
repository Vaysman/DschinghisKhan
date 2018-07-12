$(document).ready(function () {

    let orderEditor = new $.fn.dataTable.Editor({
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/orders',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.originator = currentUser.id;
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    alert(`Номер заявки: ${response.number}`);
                    orderDataTable.draw();
                    orderEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/orders/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        if (value['route'] == "") {
                            delete value['route'];
                        }
                        if (value['dropPoints'].length===0) {
                            delete value['dropPoints'];
                        }
                        if (value['assignedCompanies'].length===0) {
                            delete value['assignedCompanies'];
                        }
                        if(value['requirements'].length===0){
                            delete value['requirements'];
                        }
                        if(value['cargo'].length===0){
                            delete value['cargo'];
                        }

                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    orderDataTable.draw();
                    orderEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            }
            ,
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/orders/_id_',
                data: function (d) {
                    return '';
                }
            }
        },
        table: '#orderTable',
        idSrc: 'id',

        fields: [
            {
                label: 'Маршрут', name: 'route', type: 'selectize',
                options: [],
                opts: {
                    searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
                    load: function (query, callback) {
                        $.get(`api/routes/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentUser.id}`,
                            function (data) {
                                var routeOptions = [];
                                data._embedded.routes.forEach(function (entry) {
                                    routeOptions.push({"label": entry.name, "value": entry._links.self.href});
                                });
                                callback(routeOptions);
                            }
                        );

                    }
                },
                fieldInfo:"Найти подходящий маршрут можно <a href='/routes'>здесь</a>"
            },
            {
                label: 'Точки разгрузки',
                name: 'dropPoints',
                type: 'selectize',
                options: [],
                opts: {
                    searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить", maxItems: 10,
                    load: function (query, callback) {
                        $.get(`api/points/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentUser.id}`,
                            function (data) {
                                var pointOptions = [];
                                data._embedded.points.forEach(function (entry) {
                                    pointOptions.push({"label": entry.name, "value": entry._links.self.href});
                                });
                                callback(pointOptions);
                            }
                        );

                    }
                }
            },
            {label: 'Обязательность заявки', name: 'orderObligation', type: "selectize", options: obligationOptions},
            {
                label: 'Компании', name: 'assignedCompanies', type: 'selectize', placeholder: "Нажмите, чтобы изменить",
                options: [],
                opts: {
                    searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить", maxItems: 10,
                    load: function (query, callback) {
                        $.get(`api/companies/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentUser.id}`,
                            function (data) {
                                var companyOptions = [];
                                data._embedded.companies.forEach(function (entry) {
                                    companyOptions.push({"label": entry.name, "value": entry._links.self.href});
                                });
                                callback(companyOptions);
                            }
                        );

                    }
                },
                fieldInfo:"Можно указать до 10 компаний. Если заявка обязательная - то только 1."
            },

            {
                label: 'Груз',
                name: 'cargo',
                type: 'selectize',
                options: [],
                opts: {
                    searchField: "label", create: true, maxItems: 10, placeholder: "Нажмите, чтобы изменить",
                    load: function (query, callback) {
                        $.get(`api/cargoTypes/search/findTop10ByNameStartingWith/?name=${query}`,
                            function (data) {
                                var cargoOptions = [];
                                data._embedded.cargoTypes.forEach(function (entry) {
                                    cargoOptions.push({"label": entry.name, "value": entry.name});
                                });
                                callback(cargoOptions);
                            }
                        );

                    }
                }
            },
            {
                label: 'Требования',
                name: 'requirements',
                type: 'selectize',
                options: requirementOptions,
                opts: {
                    searchField: "label", create: true, maxItems: 10,
                }
            },
            {
                label: 'Дата оплаты', name: 'paymentDate', type: "datetime", format: "D/M/YYYY"
            },
            {
                label: 'Дата возврата документов', name: 'documentReturnDate', type: "datetime", format: "D/M/YYYY"
            },
            {
                label: 'Коэф. изменения рейтинга', name: 'rating', type: "mask", mask: "###",
                maskOptions: {
                    reverse: true,
                    placeholder: "1000"
                }
            },


        ]
    });


    var orderDataTable = $("#orderTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/ordersForUser", // json datasource
                type: "post"  // method  , by default get
            },
            dom: 'Btp',
            language: {
                url: '/localization/dataTablesRus.json'
            },
            select: {
                style: 'single'
            },
            "buttons": [
                {
                    extend: "create",
                    editor: orderEditor
                },
                {
                    extend: "edit",
                    editor: orderEditor
                },
                {
                    extend: "remove",
                    editor: orderEditor
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "number", "data": "number", "targets": 1,},
                {"name": "status", "data": "status", searchable: false, orderable: false, "targets": 2},
                {
                    "name": "route.name",
                    "data": "route.name",
                    "targets": 3,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "dropPoints",
                    "data": "dropPoints[, ].name",
                    "targets": 4,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "company.name",
                    "data": "company.name",
                    searchable: false,
                    orderable: false,
                    "targets": 5,
                    defaultContent: ""
                },
                {
                    "name": "transport.number",
                    "data": "transport.number",
                    searchable: false,
                    orderable: false,
                    "targets": 6,
                    defaultContent: ""
                },
                {
                    "name": "driver.name",
                    "data": "driver.name",
                    searchable: false,
                    orderable: false,
                    "targets": 7,
                    defaultContent: ""
                },
                {
                    "name": "requirements",
                    "data": "requirements[, ]",
                    "targets": 8,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "cargo",
                    "data": "cargo[, ]",
                    "targets": 9,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "paymentDate",
                    "data": "paymentDate",
                    "targets": 10,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "documentReturnDate",
                    "data": "documentReturnDate",
                    "targets": 11,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "rating",
                    "data": "rating",
                    "targets": 12,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "orderObligation",
                    "data": "orderObligation",
                    "targets": 13,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                // {"name": "vehicleType", "data": "vehicleType", searchable:false, orderable: false, "targets": 9,defaultContent: ""},
                // {"name": "volume", "data": "volume", searchable:false, orderable: false, "targets": 10,defaultContent: "" , render: function (data, type, row, meta) {
                //         return (data!==null) ? `${data}м<sup>3</sup>` : "";
                //     }},
                // {"name": "tonnage", "data": "tonnage", searchable:false, orderable: false, "targets": 11,defaultContent: "", render: function (data, type, row, meta) {
                //         return (data!==null) ? `${data}т` : "";
                //     }},
                // {"name": "loadingType", "data": "loadingType", searchable:false, orderable: false, "targets": 12,defaultContent: ""},
                // {"name": "comment", "data": "comment", searchable:false, orderable: false, "targets": 13,defaultContent: ""},
            ]
        }
    );

});
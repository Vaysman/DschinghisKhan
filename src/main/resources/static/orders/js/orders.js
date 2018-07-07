$(document).ready(function () {

    let orderAssignEditor = new $.fn.dataTable.Editor({
        table: '#orderTable',
        idSrc: 'id',
        ajax: {
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/orders/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.status = "Назначено";
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    alert("Заявка назначена успешно. \n Назначенные компании получат уведомление в личном кабинете.");
                    orderDataTable.draw();
                    orderEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            }
        },
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
                label: 'Компании', name: 'assignedCompanies', type: 'selectize',
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
            }
            ]
    });

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
                            delete value['company'];
                        }
                        if (value['company'] == "") {
                            delete value['company'];
                        }
                        if (value['dropPoint'] == "") {
                            delete value['dropPoint'];
                        }
                        if (value['pickupPoint'] == "") {
                            delete value['pickupPoint'];
                        }
                            delete value['requirements'];
                            delete value['cargo'];

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
                label: 'Точка погрузки',
                name: 'pickupPoint',
                type: 'selectize',
                options: [],
                opts: {
                    searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
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
            {
                label: 'Точка разгрузки',
                name: 'dropPoint',
                type: 'selectize',
                options: [],
                opts: {
                    searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
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
            {label: 'Обязательность заявки', name: 'orderObligation', type: "selectize", options: obligationOptions},

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
                },{
                    extend: "edit",
                    text: "Назначить маршрут и компании",
                    editor: orderAssignEditor
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
                    "name": "company.name",
                    "data": "company.name",
                    searchable: false,
                    orderable: false,
                    "targets": 4,
                    defaultContent: ""
                },
                {
                    "name": "pickupPoint.name",
                    "data": "pickupPoint.name",
                    "targets": 5,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "dropPoint.name",
                    "data": "dropPoint.name",
                    "targets": 6,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "requirements",
                    "data": "requirements[, ]",
                    "targets": 7,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "cargo",
                    "data": "cargo[, ]",
                    "targets": 8,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "paymentDate",
                    "data": "paymentDate",
                    "targets": 9,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "documentReturnDate",
                    "data": "documentReturnDate",
                    "targets": 10,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "rating",
                    "data": "rating",
                    "targets": 11,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                },
                {
                    "name": "orderObligation",
                    "data": "orderObligation",
                    "targets": 12,
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
    
    orderDataTable.on('select', function (e, dt, type, indexes) {
        if(orderDataTable.rows(indexes[0]).data.status!=="Создано"){
            orderDataTable.button(3).disable();
        }
        console.log(indexes);
    });
});
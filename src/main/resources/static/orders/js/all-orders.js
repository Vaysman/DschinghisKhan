$(document).ready(function () {

    let statusChangeEditorOnAllOrders = new $.fn.dataTable.Editor({
        table: '#orderTable',
        idSrc: 'id',
        ajax: {
            edit: {
                contentType: 'application/json',
                type: 'POST',
                url: 'orderLifecycle/changeStatus/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    if (response === "Success") {
                        orderDataTable.draw();
                        statusChangeEditorOnAllOrders.close();
                        orderDataTable.button(4).disable();
                    } else {
                        alert(response)
                    }

                },
                error: function (jqXHR, exception) {
                    alert(exception.responseText);
                }
            }
        },
        fields: [
            {
                label: 'Статус', name: 'status', data: "status", type: "selectize", options: statusesForDispatcherOptions
            }
        ]
    });


    let orderAssignEditor = new $.fn.dataTable.Editor({
        table: '#orderTable',
        idSrc: 'id',
        ajax: {
            edit: {
                contentType: 'application/json',
                type: 'POST',
                url: 'orderLifecycle/assign/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    if (response === "Success") {
                        alert("Заявка назначена успешно. \n Назначенные компании получат уведомление в личном кабинете.");
                        orderDataTable.draw();
                        orderAssignEditor.close();
                    } else {
                        alert(response)
                    }

                },
                error: function (jqXHR, exception) {
                    alert(exception.responseText);
                }
            }
        },
        fields: [
            {
                label: 'Компании', name: 'assignedCompanies', type: 'selectize',
                options: [],
                opts: {
                    searchField: "label",
                    create: false,
                    placeholder: "Нажмите, чтобы изменить",
                    maxItems: 10,
                    loadThrottle: 400,
                    load: function (query, callback) {
                        $.get(`api/companies/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
                            function (data) {
                                var companyOptions = [];
                                data._embedded.companies.forEach(function (entry) {
                                    companyOptions.push({"label": entry.name, "value": entry.id});
                                });
                                callback(companyOptions);
                            }
                        );
                    }
                },
                fieldInfo: "Можно указать до 10 компаний. Если заявка обязательная - то только 1."
            },
            {
                label: 'Стоимость', name: 'dispatcherPrice', data: "routePrice", type: 'mask',
                mask: "#",
                fieldInfo: "Если стоимость отличается от указанной в маршруте - измените её здесь."
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
                        value.originator = currentCompanyId;
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
                        if (value['assignedCompanies'].length === 0) {
                            delete value['assignedCompanies'];
                        }
                        if (value['requirements'].length === 0) {
                            delete value['requirements'];
                        }
                        if (value['cargo'].length === 0) {
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
                    searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить", loadThrottle: 400,
                    load: function (query, callback) {
                        $.get(`api/routes/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
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
                // fieldInfo:"Найти подходящий маршрут можно <a href='/routes'>здесь</a>"
            },
            {
                label: 'Груз',
                name: 'cargo',
                type: 'selectize',
                options: [],
                opts: {
                    searchField: "label",
                    create: true,
                    maxItems: 10,
                    placeholder: "Нажмите, чтобы изменить",
                    loadThrottle: 400,
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
                label: 'Дней для оплаты', name: 'paymentDate', type: "mask", mask: "#",
                fieldInfo:"Через сколько дней статус заявки сменится с 'Документы получены' на 'Ожидает оплаты'"
            },
            {
                label: 'Дней для возврата документов', name: 'documentReturnDate', type: "mask", mask: "#",
                fieldInfo:"Через сколько дней статус заявки сменится с 'Доставлено'/'Подтверждение доставки' на 'Ожидает возврата документов'"
            },
            {
                label: 'Коэф. изменения рейтинга', name: 'rating', type: "mask", mask: "###",
                maskOptions: {
                    reverse: true,
                    placeholder: "1000"
                }
            },
            {label: 'Обязательность заявки', name: 'orderObligation', type: "selectize", options: obligationOptions},
            {label: "Оплата", name: "paymentType", type: "selectize", options: orderPaymentOptions}
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
                }, {

                    text: "Назначить компании",
                    action: function (e, dt, node, config) {
                        orderAssignEditor.edit(orderDataTable.rows('.selected', {select: true}), 'Назначить компании', {
                            "label": "Назначить",
                            "fn": function () {
                                this.submit();
                            }
                        });
                    },
                    enabled: false
                },{

                    text: "Изменить статус",
                    action: function (e, dt, node, config) {
                        statusChangeEditorOnAllOrders.edit(orderDataTable.rows('.selected', {select: true}), 'Изменить статус', {
                            "label": "Изменить статус",
                            "fn": function () {
                                this.submit();
                            }
                        });
                    },
                    enabled: false
                },
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "number", "data": "number", "targets": 1, render: function(data, type, full){
                        return `<a target="_blank" href='info/orders/${full.id}'>${data}</a>`;
                    }},
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
                    "name": "transport.number",
                    "data": "transport.number",
                    searchable: false,
                    orderable: false,
                    "targets": 5,
                    defaultContent: ""
                },
                {
                    "name": "driver.name",
                    "data": "driver.name",
                    searchable: false,
                    orderable: false,
                    "targets": 6,
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
                {
                    "name": "paymentType",
                    "data": "paymentType",
                    "targets": 13,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                }
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
            if (orderDataTable.row(indexes[0]).data().status == "Создано") {
                dt.button(3).enable();
            } else {
                dt.button(3).disable();
            }

            let currentStatus = orderDataTable.row(indexes[0]).data().status;
            if (changeableStatuses.includes(currentStatus)) {
                dt.button(4).enable();
            } else {
                dt.button(4).disable();

            }
        }
        );

    orderDataTable.on('deselect', function () {
        orderDataTable.button(3).disable();
    })
});
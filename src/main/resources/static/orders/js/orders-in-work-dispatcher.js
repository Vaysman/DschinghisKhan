$(document).ready(function () {
    $('#tab-orders-in-work-link a').one('shown.bs.tab', function () {

        let statusChangeEditor = new $.fn.dataTable.Editor({
            table: '#ordersInWorkTable',
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
                            ordersInWorkDataTable.draw();
                            statusChangeEditor.close();
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
                    label: 'Статус',
                    name: 'status',
                    data: "status",
                    type: "selectize",
                    options: statusesForDispatcherOptions
                }
            ]
        });


        var ordersInWorkDataTable = $("#ordersInWorkTable").DataTable({
                processing: true,
                serverSide: true,
                searchDelay: 800,
                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/ordersForUserInWork", // json datasource
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

                        text: "Изменить статус",
                        action: function (e, dt, node, config) {
                            statusChangeEditor.edit(ordersInWorkDataTable.rows('.selected', {select: true}), 'Изменить статус', {
                                "label": "Изменить статус",
                                "fn": function () {
                                    this.submit();
                                }
                            });
                        },
                        enabled: false
                    }
                ],
                "paging": 10,
                "columnDefs": [{"name": "id", "data": "id", "targets": 0, visible: false},
                    {
                        "name": "number", "data": "number", "targets": 1, render: function (data, type, full) {
                            return `<a target="_blank" href='info/orders/${full.id}'>${data}</a>`;
                        }
                    },
                    {"name": "status", "data": "status", searchable: false, orderable: false, "targets": 2},
                    {
                        "name": "route.name",
                        "data": "route.name",
                        "targets": 3,
                        orderable: false,
                        defaultContent: ""
                    },
                    {
                        "name": "company.name",
                        "data": "company.name",
                        orderable: false,
                        "targets": 4,
                        defaultContent: ""
                    },
                    {
                        "name": "transport.number",
                        "data": "transport.number",
                        orderable: false,
                        "targets": 5,
                        defaultContent: ""
                    },
                    {
                        "name": "driver.name",
                        "data": "driver.name",
                        orderable: false,
                        "targets": 6,
                        defaultContent: "",
                        render: function (data, type, full) {
                            return `<a target="_blank" href='info/drivers/${full.driver.id}'>${data}</a>`;
                        }
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
                    },
                    {
                        "name": "dispatchDate",
                        "data": "dispatchDate",
                        "targets": 14,
                        searchable: false,
                        defaultContent: ""
                    }
                ]
            }
        );

        ordersInWorkDataTable.on('select', function (e, dt, type, indexes) {
            let currentStatus = ordersInWorkDataTable.row(indexes[0]).data().status;
            if (changeableStatuses.includes(currentStatus)) {
                dt.button(0).enable();
                if (currentStatus !== "Подтверждение доставки") {
                    dt.button(1).enable();
                }
            } else {
                dt.button(0).disable();
                dt.button(1).disable();
            }
            if (currentStatus === "Ожидает возврата документов" || currentStatus === "Подтверждение доставки") {
                dt.button(2).enable()
            } else {
                dt.button(2).disable()
            }

            if (currentStatus === "Ожидает оплаты") {
                dt.button(3).enable()
            } else {
                dt.button(3).disable()
            }

        });

        ordersInWorkDataTable.on('deselect', function () {
            ordersInWorkDataTable.buttons().disable();
        })
    })
});
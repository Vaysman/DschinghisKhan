$(document).ready(function () {

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
                        newdata = JSON.stringify(value.status);
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
                label: 'Новый статус', name: 'status', data: "status", type: "selectize", options: statusesForCompanyOptions
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
                url: "dataTables/ordersForCompanyInWork", // json datasource
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
                },
                {
                    text: "Подтвердить доставку",
                    action: function (e, dt, node, config) {
                        let id = dt.rows({selected:true}).data()[0].id;
                        $.ajax({
                            url: `/orderLifecycle/confirmDelivery/${id}`,
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                                if(response=="Success"){
                                    dt.draw("page");
                                } else {
                                    alert(response);
                                }
                            }
                        })
                    },
                    enabled: false
                },
                {
                    text: "Заявить о неоплате",
                    action: function (e, dt, node, config) {
                        let id = dt.rows({selected:true}).data()[0].id;
                        $.ajax({
                            url: `/orderLifecycle/claimNonPayment/${id}`,
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                                if(response=="Success"){
                                    dt.draw("page");
                                } else {
                                    alert(response);
                                }
                            }
                        })
                    },
                    enabled: false
                },
                {
                    text: "Подтвердить оплату",
                    action: function (e, dt, node, config) {
                        let id = dt.rows({selected:true}).data()[0].id;
                        if (confirm("Вы уверены, что хотите подтвердить получение оплаты?\n" +
                            "Под этим подразумевается то, что заказчик оплатил заявку и вы не имеете претензий.\n" +
                            "Заявка пропадет из системы, как закончившая жизненный цикл."))
                        $.ajax({
                            url: `/orderLifecycle/confirmPayment/${id}`,
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            success: function (response) {
                                if(response=="Success"){
                                    dt.draw("page");
                                } else {
                                    alert(response);
                                }
                            }
                        })
                    },
                    enabled: false
                },
            ],
            "paging": 10,
            "columnDefs": [ {"name": "id", "data": "id", "targets": 0, visible: false},
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
            ]
        }
    );

    ordersInWorkDataTable.on('select', function (e, dt, type, indexes) {
        let currentStatus = ordersInWorkDataTable.row(indexes[0]).data().status;
        if (changeableStatuses.includes(currentStatus)) {
            dt.button(0).enable();
            dt.button(1).enable();
        } else {
            dt.button(0).disable();
            dt.button(1).disable();
        }
        if(currentStatus==="Оплачено"){
            dt.button(2).enable()
        } else {
            dt.button(2).disable()
        }

        if(deliveredStatuses.includes(currentStatus)){
            dt.button(3).enable()
        } else {
            dt.button(3).disable()
        }

    });

    ordersInWorkDataTable.on('deselect', function () {
        ordersInWorkDataTable.buttons().disable();
    })
});
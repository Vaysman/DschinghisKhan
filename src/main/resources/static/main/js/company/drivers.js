// <th>id</th>
// <th>ФИО</th>
// <th>Телефон</th>
// <th>Паспорт</th>
// <th>Водительское удостверение</th>
// <th>Рейтинг</th>
// <th>Мобильное приложение</th>
// <th>Отслеживание по телефону</th>
// <th>В штате</th>
// <th>Оплата</th>

$(document).ready(function () {

    let driverEditor = new $.fn.dataTable.Editor({
        table: '#driverDataTable',
        idSrc: 'id',
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/drivers',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.originator = currentCompanyId;
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    driverDataTable.draw();
                    driverEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/drivers/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    driverDataTable.draw();
                    driverEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/drivers/_id_',
                data: function (d) {
                    return '';
                }
            }
        },
        fields: [
            {
                label: 'ФИО',
                name: 'name',
                type: 'text'
            },
            {
                label: 'Телефон',
                name: 'phone',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'Номер паспорта',
                name: 'passportNumber',
                type: 'mask',
                mask: '00 00 000000'
            },
            {
                label: 'Номер водительского удостверения',
                name: 'licenseNumber',
                type: 'mask',
                mask: '00 00 000000'
            },
            {
                label: 'Рейтинг',
                name: 'rating',
                type: 'mask',
                mask: '######',
                placeholder: ""
            },
            {
                label: 'Мобильное приложение', name: 'hasMobileApp', type: "radio", options: [{ label:"Есть", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Телефон для приложения',
                name: 'mobileAppNumber',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'Отслеживание по телефону', name: 'isTracked', type: "radio", options: [{ label:"Есть", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Телефон для отслеживания',
                name: 'trackingNumber',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'В штате', name: 'isHired', type: "radio", options: [{ label:"Да", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Способ оплаты',
                name: 'paymentType',
                type: 'selectize',
                options: paymentTypeOptions,
                opts: {
                    searchField: "label", create: false
                }
            }
        ]

    });


    var driverDataTable = $("#driverDataTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/driversForUser", // json datasource
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
                    editor: driverEditor
                },
                {
                    extend: "edit",
                    editor: driverEditor
                },
                {
                    extend: "remove",
                    editor: driverEditor
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "name", "data": "name", "targets": 1},
                {"name": "phone", "data": "phone", searchable: false, orderable: false, "targets": 2},
                {"name": "passportNumber", "data": "passportNumber", searchable: false, orderable: false, "targets": 3},
                {"name": "licenseNumber", "data": "licenseNumber", searchable: false, orderable: false, "targets": 4},
                {"name": "rating", "data": "rating", searchable: false, "targets": 5},
                {
                    "name": "hasMobileApp",
                    "data": "hasMobileApp",
                    "targets": 6,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Есть" : "Нет"
                    }
                },
                {"name": "mobileAppNumber", "data": "mobileAppNumber", searchable: false, orderable: false, "targets": 7},
                {
                    "name": "isTracked",
                    "data": "isTracked",
                    "targets": 8,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Да" : "Нет"
                    }
                },
                {"name": "trackingNumber", "data": "trackingNumber", searchable: false, orderable: false, "targets": 9},
                {
                    "name": "isHired",
                    "data": "isHired",
                    "targets": 10,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Да" : "Нет"
                    }
                },
                {
                    "name": "paymentType",
                    "data": "paymentType",
                    "targets": 11,
                    searchable: false,
                    orderable: false
                }
            ]
        }
    );
});
$(document).ready(function () {
    let companyInfoEditor = new $.fn.dataTable.Editor({
        ajax: {
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/companies/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    console.log(newdata);
                    return newdata;
                },
                success: function (response) {
                    companiesTable.draw();
                    companyEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            }
        },
        fields: [
            {label: 'ИНН', name: 'inn', type: 'text'},
            {label: 'Кол-во наемного  транспорта', name: 'numberOfTransports', type: 'mask', mask: "###"},
            {label: 'Код ati.ru', name: 'atiCode', type: 'text'},
            {label: 'ФИО главного бухгалтера', name: 'accountantName', type: 'text'},
            {label: 'ОКВЕД', name: 'ocved', type: 'text'},
            {label: 'ОКПО', name: 'ocpo', type: 'text'},
            {label: 'ОГРН', name: 'ogrn', type: 'text'},
            {label: 'Налогообложение', name: 'taxationType', type: 'selectize',options: [{label:"С НДС",value:"С НДС"},{label:"Без НДС",value:"Без НДС"}]}
        ],
        table: '#transportCompaniesTable',
        idSrc: 'id',
    });

    let companyEditor = new $.fn.dataTable.Editor({
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/companies',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.originator = currentCompanyId;
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    companiesTable.draw();
                    companyEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            }
        },
        table: '#transportCompaniesTable',
        idSrc: 'id',

        fields: [

            {label: 'Название', name: 'name', type: 'text'},
            {label: 'Краткое название', name: 'shortName', type: 'text', fieldInfo: "Будет использовано как логин"},
            {label: "E-mail", name: "email", type: 'text', fieldInfo: "На данный адрес будет отправлен логин и пароль"}
        ]
    });

    var companiesTable = $("#transportCompaniesTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/companies", // json datasource
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
                    editor: companyEditor
                },
                {
                    extend: "edit",
                    editor: companyInfoEditor,
                    text: "Редактировать информацию"
                }
            ],
            "paging": 10,
            "columnDefs": [

                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "name", "data": "name", "targets": 1, render: function (data,type,full) {
                        return `<span ${full.originator==currentCompanyId ? 'class="somewhat-green"' : ""}>${data}</span>`
                    }},
                {"name": "shortName", "data": "shortName", "targets": 2},
                {"name": "inn", "data": "inn", "targets": 3, defaultContent:""},
                {"name": "numberOfTransports", "data": "numberOfTransports", "targets": 4, defaultContent:"", orderable: false, searchable: false},
                {"name": "accountantName", "data": "accountantName", "targets": 5, defaultContent:"", orderable: false, searchable: false},
                {"name": "ocved", "data": "ocved", "targets": 6, defaultContent:"", orderable: false, searchable: false},
                {"name": "ocpo", "data": "ocpo", "targets": 7, defaultContent:"", orderable: false, searchable: false},
                {"name": "ogrn", "data": "ogrn", "targets": 8, defaultContent:"", orderable: false, searchable: false},
                {"name": "type", "data": "type", "targets": 9, defaultContent:"", orderable: false, searchable: false},
                {"name": "email", "data": "email", "targets": 10, defaultContent:"", orderable: false, searchable: false},
                {"name": "taxationType", "data": "taxationType", "targets": 11, defaultContent:"", orderable: false, searchable: false},
            ]
        }
    );
});
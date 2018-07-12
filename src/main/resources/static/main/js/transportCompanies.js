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
            {label: 'ОГРН', name: 'ogrn', type: 'text'}
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
                        value.originator = currentUser.id;
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
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/companies/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        if (value.point==""){
                            delete value["point"];
                        }
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
            ,
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/companies/_id_',
                data: function (d) {
                    return '';
                }
            }
        },
        table: '#transportCompaniesTable',
        idSrc: 'id',

        fields: [

            {label: 'Название', name: 'name', type: 'text'},
            {label: 'Краткое название', name: 'shortName', type: 'text', fieldInfo: "Будет использовано как логин"},
            {label: 'Пункт', name: 'point', type: 'selectize', options: [], opts:{
                    searchField: "label",
                    loadThrottle: 400,
                    load: function(query, callback){
                        $.get( `api/points/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentUser.id}`,
                            function (data) {
                                console.log(data);
                                let pointOptions = [];
                                data._embedded.points.forEach(function (entry) {
                                    pointOptions.push({"label": entry.name, "value": entry._links.self.href});
                                });
                                callback(pointOptions);
                            }
                        )
                    },
                    create: false,
                    placeholder:"Нажмите, чтобы изменить"
                }},
            {label: "E-mail", name: "email", type: 'text', fieldInfo: "На данный адрес будет отправлен логин и пароль"},
            {label: 'Тип компании', name: 'type', type: 'selectize', options: companyTypeOptions}
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
                url: "dataTables/companiesForUser", // json datasource
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
                    editor: companyEditor
                },
                {
                    extend: "edit",
                    editor: companyInfoEditor,
                    text: "Редактировать информацию"
                },
                {
                    extend: "remove",
                    editor: companyEditor
                }
            ],
            "paging": 10,
            "columnDefs": [

                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "name", "data": "name", "targets": 1},
                {"name": "shortName", "data": "shortName", "targets": 2},
                {"name": "point.name", "data": "point.name", "targets": 3,defaultContent:"", orderable: false, searchable: false},
                {"name": "user.username", "data": "user.username", "targets": 4, defaultContent:"", orderable: false, searchable: false},
                {"name": "inn", "data": "inn", "targets": 5, defaultContent:""},
                {"name": "numberOfTransports", "data": "numberOfTransports", "targets": 6, defaultContent:"", orderable: false, searchable: false},
                {"name": "accountantName", "data": "accountantName", "targets": 7, defaultContent:"", orderable: false, searchable: false},
                {"name": "ocved", "data": "ocved", "targets": 8, defaultContent:"", orderable: false, searchable: false},
                {"name": "ocpo", "data": "ocpo", "targets": 9, defaultContent:"", orderable: false, searchable: false},
                {"name": "ogrn", "data": "ogrn", "targets": 10, defaultContent:"", orderable: false, searchable: false},
                {"name": "type", "data": "type", "targets": 11, defaultContent:"", orderable: false, searchable: false},
                {"name": "email", "data": "email", "targets": 12, defaultContent:"", orderable: false, searchable: false},
            ]
        }
    );
});
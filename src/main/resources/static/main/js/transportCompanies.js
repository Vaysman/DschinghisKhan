$(document).ready(function () {

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
            {label: 'Краткое название', name: 'shortName', type: 'text'},
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
                }}
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
                    extend: "remove",
                    editor: companyEditor
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "name", "data": "name", "targets": 1},
                {"name": "shortName", "data": "shortName", "targets": 2},
                {"name": "point.name", "data": "point.name", "targets": 3,defaultContent:""},
                {"name": "user.username", "data": "user.username", "targets": 4, defaultContent:""},
            ]
        }
    );
});
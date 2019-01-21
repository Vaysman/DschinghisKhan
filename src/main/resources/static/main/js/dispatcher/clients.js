$(document).ready(function () {
    let currentlySelectedClientId = 0;

    $('#tab-contacts-link a').one('shown.bs.tab', function () {
        let clientsEditor = null;
        clientsEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/clients',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            if (value['primaryContact'] == "") {
                                delete value['primaryContact'];
                            }
                            value.originator = currentCompanyId;
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {
                        clientsDataTable.draw();
                        clientsEditor.close();
                        // alert(response.responseText);
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                },
                edit: {
                    contentType: 'application/json',
                    type: 'PATCH',
                    url: 'api/clients/_id_',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            newdata = JSON.stringify(value);
                            if (value['primaryContact'] == "") {
                                delete value['primaryContact'];
                            }
                        });
                        return newdata;
                    },
                    success: function (response) {
                        clientsDataTable.draw();
                        clientsEditor.close();
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                }
                ,
                remove: {
                    type: 'DELETE',
                    contentType: 'application/json',
                    url: 'api/clients/_id_',
                    data: function (d) {
                        return '';
                    }
                }
            },
            table: '#clientsTable',
            idSrc: 'id',

            fields: [
                {label: "ИНН", name: 'inn', type: 'text', attr: {maxlength: 60}},
                {
                    label: 'Краткое наименование<sup class="red"></sup>',
                    name: 'name',
                    type: 'text',
                    attr: {maxlength: 120}
                },
                {
                    label: "Полное наименование",
                    name: 'fullName',
                    type: 'text',
                    attr: {maxlength: 500}
                },
                {
                    label: 'Телефон',
                    name: 'phone',
                    type: 'mask',
                    mask: '+7 (000)-000-00-00'
                },
                {
                    label: 'Контактное лицо',
                    name: 'contact',
                    type: 'text',
                    attr: {maxlength: 256}
                },
                {
                    label: "E-mail",
                    name: 'email',
                    type: 'text',
                    attr: {maxlength: 120}
                },
                {
                    label: "Почтовый адрес",
                    name: 'mail_address',
                    type: 'text',
                    attr: {maxlength: 500}
                },
                {
                    label: "Фактический адрес",
                    name: 'address',
                    type: 'text',
                    attr: {maxlength: 500}
                },
                {
                    label: 'Основной контакт', name: 'primaryContact', type: 'selectize', options: [],  opts: {
                        searchField: "label",
                        create: false,
                        load: function (query, callback) {
                            $.get(`api/contacts/search/findTop10ByClientIdAndNameContaining/?name=${query}&clientId=${currentlySelectedClientId}`,

                                function (data) {
                                    let pointOptions = [];
                                    data._embedded.contacts.forEach(function (entry) {

                                        pointOptions.push({
                                            "label": entry.name,
                                            "value": entry._links.self.href
                                        });
                                    });
                                    callback(pointOptions);
                                }
                            )
                        },
                        preload: true,
                        delimiter: null,
                        loadThrottle: 500
                    }
                },
            ]
        });

        clientsEditor.on('initCreate', function () {
            clientsEditor.field('primaryContact').hide();
        });

        clientsEditor.on('initEdit', function () {
            clientsEditor.field('primaryContact').show();
            // clientsEditor.field("primaryContact").inst().clear();

        });

        clientsEditor.on('open', function () {
            clientsEditor.field("primaryContact").inst().clear();
            clientsEditor.field("primaryContact").inst().clearOptions();
            clientsEditor.field("primaryContact").inst().onSearchChange("");
        });


        var clientsDataTable = $("#clientsTable").DataTable({
                processing: true,
                serverSide: true,
                searchDelay: 800,
                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/clientsForUser", // json datasource
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
                        editor: clientsEditor
                    },
                    {
                        extend: "edit",
                        editor: clientsEditor
                    },
                    {
                        extend: "remove",
                        editor: clientsEditor
                    }
                ],
                "paging": 10,
                "columns": [
                    {"name": "id", "data": "id", "title": "id", visible: false},
                    {name: "inn", data: "inn", title:"ИНН"},
                    {"name": "name", "data": "name", "title": "Наименование"},
                    {name: "fullName",data:"fullName",title:"Полное наименование"},
                    {"name": "phone", "data": "phone", "title": "Телефон"},
                    {name: "email", data:"email",title:"E-mail"},
                    {"name": "contact", "data": "contact", "title": "Контактное лицо"},
                    {name: "mailAddress",data:"mailAddress", title:"Почтовый адрес"},
                    {name: "address", data:"address",title: "Фактический адрес"},
                    {name: "comment", data:"comment", title: "Комментарий"},
                    {name: "primaryContact.name", data:"primaryContact.name", title:"Основной контакт", defaultContent:""}
                ]
            }
        );


        clientsDataTable.on('select', function (e, dt, type, indexes) {
            if (type === 'row') {
                currentlySelectedClientId = clientsDataTable.rows(indexes).data('id')[0].id;
            }
        });


    });
});
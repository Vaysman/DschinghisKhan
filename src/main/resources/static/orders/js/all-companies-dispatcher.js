let suggestions = {};
$(document).ready(function () {
    $('#tab-companies-link a').one('shown.bs.tab', function () {

        $("#uploadContract").click(function (event) {

            //stop submit the form, we will post it manually.
            event.preventDefault();


            // Get form
            var form = $('#contractUploadForm')[0];

            // Create an FormData object
            var data = new FormData(form);

            // If you want to add an extra field for the FormData
            // data.append("CustomField", "This is some extra data, testing");

            let id = data.get("id");

            // disabled the submit button
            $("#uploadContract").prop("disabled", true);

            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: `/upload/contract/${id}`,
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                timeout: 600000,
                success: function (data) {

                    $("#uploadContract").prop("disabled", false);
                    $("#contractUploadModal").modal('hide');
                    document.getElementById("contractUploadForm").reset();
                    $("#contractUploadError").text("")

                },
                error: function (e) {
                    console.log("ERROR : ", e);
                    $("#uploadContract").prop("disabled", false);
                    if (e.responseJSON.message.includes("Maximum upload size exceeded")) {
                        $("#contractUploadError").text("Ошибка: файл слишком большой")
                    } else {
                        $("#contractUploadError").text(e.responseJSON.message);
                    }
                }
            });

        })

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
                {label: 'Кол-во наемного  транспорта', name: 'numberOfTransports', type: 'mask', mask: "###"},
                {label: 'Код ati.su', name: 'atiCode', type: 'text'},
                {label: 'ФИО главного бухгалтера', name: 'accountantName', type: 'text'},
                {label: 'ОКВЕД', name: 'ocved', type: 'text'},
                {label: 'ОКПО', name: 'ocpo', type: 'text'},
                {label: 'ОГРН', name: 'ogrn', type: 'text'},
                {
                    label: 'Налогообложение',
                    name: 'taxationType',
                    type: 'selectize',
                    options: [{label: "С НДС", value: "С НДС"}, {label: "Без НДС", value: "Без НДС"}]
                },
                {label: 'Город', name: 'city', type: 'text'},
            ],
            table: '#transportCompaniesTable',
            idSrc: 'id',
        });


        let companyEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'data/registerTransportCompany',
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
                    error: function (jqXHR) {
                        console.log(jqXHR);
                        var cause = jqXHR.responseJSON.message;
                        if (cause.includes("Duplicate entry")) {
                            companyEditor.field("inn").error("Перевозчик с указанным ИНН уже существует.\nЕсли перевозчик не имеет доступа к системе - можете выслать сообщение о регистрации повторно.");
                        } else {
                            companyEditor.field("inn").error(cause);
                        }

                    }
                }
            },
            table: '#transportCompaniesTable',
            idSrc: 'id',

            fields: [

                {label: 'Название', name: 'name', type: 'hidden'},
                {
                    label: 'ИНН', name: 'inn', type: 'selectize',
                    opts: {
                        placeholder: "Введите ИНН",
                        labelField: "label",
                        valueField: "value",
                        searchField: "inn",
                        delimiter: null,
                        loadThrottle: 400,
                        preload: false,
                        maxItems: 1,
                        maxOptions: 20,
                        create: false,
                        load: function (query, callback) {
                            $.ajax({
                                url: `https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/party`,
                                type: "POST",
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                data: JSON.stringify({
                                    query: query
                                }),
                                beforeSend: function (xhr) {
                                    xhr.setRequestHeader("Authorization", "Token 2aa6402023ed529e79ba4ddb92872b0709f9b859");
                                    xhr.setRequestHeader("Accept", "application/json");
                                },
                                success: function (response) {
                                    var companyOptions = [];
                                    // suggestions = response.suggestions;
                                    response.suggestions.forEach(function (entry) {
                                        companyOptions.push({
                                            "label": `${entry.data.inn}\n${entry.value}`,
                                            "inn": entry.data.inn,
                                            "value": entry.data.hid
                                        });
                                        suggestions[entry.data.hid] = entry;
                                    });
                                    callback(companyOptions);
                                }
                            });
                        },
                        onChange: (value) => {
                            if(suggestions.hasOwnProperty(value)) {
                                let hid = value;
                                // $("#companyName").val(suggestions[value].unrestricted_value);
                                companyEditor.field('name').set(suggestions[hid].unrestricted_value);
                                console.log(hid);
                                console.log(value);
                                let fetchedData = suggestions[hid].data;
                                console.log(fetchedData);
                                if (fetchedData.inn != null) {
                                    console.log("fgfds");
                                    companyEditor.field('inn').inst().addOption({
                                        value: fetchedData.inn,
                                        label: `${fetchedData.inn} ${suggestions[hid].unrestricted_value}`
                                    });
                                    companyEditor.field('inn').inst().setValue(fetchedData.inn);
                                    console.log("fh");

                                }
                                if(fetchedData.okved!=null){
                                    companyEditor.field("ocved").set(fetchedData.okved)
                                }
                                if(fetchedData.kpp!=null){
                                    companyEditor.field("kpp").set(fetchedData.kpp)
                                }
                                if(fetchedData.ogrn!=null){
                                    companyEditor.field("ogrn").set(fetchedData.ogrn);
                                }
                                if(fetchedData.okpo!=null){
                                    companyEditor.field("ocpo").set(fetchedData.okpo)
                                }
                                if(fetchedData.management!=null && fetchedData.management.name!=null){
                                    companyEditor.field("directorFullname").set(fetchedData.management.name);
                                }
                            }

                        }
                    }
                },
                {
                    label: "E-mail",
                    name: "email",
                    type: 'text',
                    fieldInfo: "На данный адрес будет отправлен логин и пароль"
                },
                {
                    type: 'hidden',
                    name: 'ocved',
                },
                {
                    type: 'hidden',
                    name: 'ocpo',
                },
                {
                    type: 'hidden',
                    name: 'kpp',
                },
                {
                    type: 'hidden',
                    name: 'ogrn',
                },
                {
                    type: 'hidden',
                    name: 'directorFullname',
                },
            ]
        });

        companyEditor.on('preSubmit', function (e, o, action) {
            if (action !== 'remove') {

                if (action == 'create') {
                    if (this.field('inn').val() == '') {
                        this.field('inn').error("ИНН должен быть указан");
                    }
                    if(this.field('email').val()==''){
                        this.field('email').error('E-Mail должен быть указан')
                    }
                }

                // If any error was reported, cancel the submission so it can be corrected
                if (this.inError()) {
                    console.log("inError");
                    return false;
                }
            }
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
                    url: "dataTables/transportCompanies", // json datasource
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
                        editor: companyEditor
                    }, {
                        extend: 'selectedSingle',
                        text: '<i class="fa fa-file"></i> Прикрепить договор',
                        action: function (e, dt, node, config) {
                            $("#contractUploadError").text("");
                            document.getElementById("contractUploadForm").reset();
                            $("#companyIdInput").val(dt.rows({selected: true}).data()[0].id);
                            $("#contractUploadModal").modal();
                            console.log(dt.rows({selected: true}).data()[0].id);
                        }
                    },
                    {
                        text: "Только мои перевозчики",
                        action: function () {
                            console.log(companiesTable.ajax.remoteUrl());
                            if (companiesTable.ajax.remoteUrl() === "dataTables/transportCompanies") {
                                companiesTable.ajax.remoteUrl("dataTables/transportCompaniesForUser");
                                companiesTable.ajax.reload();
                                this.text('Все перевозчики');
                            } else {
                                companiesTable.ajax.remoteUrl("dataTables/transportCompanies");
                                companiesTable.ajax.reload();
                                this.text('Только мои перевозчики');
                            }

                        },
                        attr: {
                            'data-toggle': 'button'
                        }
                    }
                ],
                "paging": 10,
                "columnDefs": [

                    {"name": "id", "data": "id", "targets": 0, visible: false},
                    {
                        "name": "name", "data": "name", "targets": 1,
                        render: function (data, type, full) {
                            return `<a target="_blank" href='info/company/${full.id}'>${data}</a>`;
                        }
                    },
                    {"name": "shortName", "data": "shortName", "targets": 2},
                    {"name": "inn", "data": "inn", "targets": 3, defaultContent: ""},
                    {
                        "name": "numberOfTransports",
                        "data": "numberOfTransports",
                        "targets": 4,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "accountantName",
                        "data": "accountantName",
                        "targets": 5,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "ocved",
                        "data": "ocved",
                        "targets": 6,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {"name": "ocpo", "data": "ocpo", "targets": 7, defaultContent: "", orderable: false, searchable: false},
                    {"name": "ogrn", "data": "ogrn", "targets": 8, defaultContent: "", orderable: false, searchable: false},
                    {
                        "name": "email",
                        "data": "email",
                        "targets": 9,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "taxationType",
                        "data": "taxationType",
                        "targets": 10,
                        defaultContent: "",
                        orderable: false,
                        searchable: false
                    },
                    {
                        "name": "city",
                        "data": "city",
                        "targets": 11,
                        defaultContent: "",
                        orderable: false,
                        searchable: true
                    },
                ],
                createdRow: function (row, data, dataIndex) {
                    if (sentContracts.some(contract => contract.companyId == data.id)) {
                        $(row).addClass("table-warning");
                    }
                    if (data.originator == currentCompanyId) {
                        $(row).addClass("table-success");
                    }
                },
            },
        );

        companiesTable.on('select', function (e, dt, type, indexes) {
            if (type === 'row') {
                var data = companiesTable.rows(indexes).data('id');

                if (currentCompanyId == data[0].originator) {
                    $('#companyContactsTableWrapper').show();
                    reInitCompanyContactsTable(data[0].id);
                } else {
                    $('#companyContactsTableWrapper').hide();
                }

            }
        });

        function reInitCompanyContactsTable(id) {
            $.get(`api/companies/${id}`).success(function (companyData) {
                let companyHref = companyData._links.self.href;

                var companyContactsEditor = new $.fn.dataTable.Editor({
                    ajax: {
                        create: {
                            type: 'POST',
                            contentType: 'application/json',
                            url: 'api/contacts',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    value.originator = currentCompanyId;
                                    value.company = companyHref;
                                    value.type = "SECONDARY";
                                    newdata = JSON.stringify(value);
                                });
                                return newdata;
                            },
                            success: function (response) {
                                companyContactsTable.draw();
                                companyContactsEditor.close();
                                // alert(response.responseText);
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        },
                        edit: {
                            contentType: 'application/json',
                            type: 'PATCH',
                            url: 'api/contacts/_id_',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    newdata = JSON.stringify(value);
                                });
                                console.log(newdata);
                                return newdata;
                            },
                            success: function (response) {
                                companyContactsTable.draw();
                                companyContactsEditor.close();
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        }
                        ,
                        remove: {
                            type: 'DELETE',
                            contentType: 'application/json',
                            url: 'api/contacts/_id_',
                            data: function (d) {
                                return '';
                            }
                        }
                    },
                    table: '#companyContactsTable',
                    idSrc: 'id',

                    fields: [
                        {label: 'ФИО контакта', name: 'name', type: 'text', attr: {maxlength: 128}},
                        {
                            label: 'Телефон',
                            name: 'phone',
                            type: "text",
                            attr: {maxlength: 20, placeholder: "+7 (000) 000 00-00"}
                        },
                        {label: 'E-mail', name: 'email', type: "text", attr: {maxlength: 64}},
                        {label: 'Должность', name: 'position', type: "text", attr: {maxlength: 64}},
                    ]
                });

                var companyContactsTable = $("#companyContactsTable").DataTable({
                        processing: true,
                        serverSide: true,
                        destroy: true,
                        searchDelay: 800,
                        ajax: {
                            contentType: 'application/json',
                            processing: true,
                            data: function (d) {
                                return JSON.stringify(d);
                            },
                            url: `dataTables/contactsForCompany/${id}`, // json datasource
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
                                editor: companyContactsEditor
                            },
                            {
                                extend: "edit",
                                editor: companyContactsEditor
                            },
                            {
                                extend: "remove",
                                editor: companyContactsEditor
                            }
                        ],
                        "paging": 10,
                        "columnDefs": [
                            {"name": "id", "data": "id", "targets": 0, visible: false},
                            {"name": "name", "data": "name", "targets": 1},
                            {"name": "phone", "data": "phone", "targets": 2},
                            {"name": "email", "data": "email", "targets": 3},
                            {"name": "position", "data": "position", "targets": 4},
                        ]
                    }
                );

            });


        }

    });

});
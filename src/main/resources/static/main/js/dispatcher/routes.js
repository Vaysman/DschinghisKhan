$(document).ready(function () {

    $('#tab-routes-link a').one('shown.bs.tab', function () {
        let routeEditor = null;
        routeEditor = new $.fn.dataTable.Editor({
            ajax: {
                create: {
                    type: 'POST',
                    contentType: 'application/json',
                    url: 'api/routes',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            value.originator = currentCompanyId;
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {
                        routeDataTable.draw();
                        routeEditor.close();
                        // alert(response.responseText);
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                },
                edit: {
                    contentType: 'application/json',
                    type: 'PATCH',
                    url: 'api/routes/_id_',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            if (value['company'] == "") {
                                delete value['company'];
                            }
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {
                        routeDataTable.draw();
                        routeEditor.close();
                    },
                    error: function (jqXHR, exception) {
                        alert(response.responseText);
                    }
                }
                ,
                remove: {
                    type: 'DELETE',
                    contentType: 'application/json',
                    url: 'api/routes/_id_',
                    data: function (d) {
                        return '';
                    }
                }
            },
            table: '#routesTable',
            template: '#routesForm',
            idSrc: 'id',

            fields: [
                {
                    label: 'Название<sup class="red">*</sup>',
                    name: 'name',
                    type: 'text',
                    fieldInfo: "Краткая информация о маршруте, например: Москва-Рязань, 20т 15 000 рублей без НДС, тент"
                },
                {
                    label: 'ТК<sup class="red">*</sup>',
                    name: 'company',
                    type: 'selectize',
                    options: [],
                    opts: {
                        searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
                        load: function (query, callback) {
                            $.get(`api/companies/search/findTop10ByNameContainingAndType/?name=${query}&type=TRANSPORT`,
                                function (data) {
                                    var companyOptions = [];
                                    data._embedded.companies.forEach(function (entry) {
                                        companyOptions.push({"label": entry.name, "value": entry._links.self.href});
                                    });
                                    callback(companyOptions);
                                }
                            );
                        },
                        loadThrottle: 400,
                        preload: true,
                    }
                },
                {
                    label: 'Стоимость за маршрут<span class="clipped"><sup class="red l-half">*</sup></span>', name: 'totalCost', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за маршрут + НДС<span class="clipped"><sup class="red r-half">*</sup></span>', name: 'totalCostNds', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за километр', name: 'costPerKilometer', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за километр + НДС', name: 'costPerKilometerNds', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за пункт', name: 'costPerPoint', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за пункт + НДС', name: 'costPerPointNds', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за час', name: 'costPerHour', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за час + НДС', name: 'costPerHourNds', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за паллету', name: 'costPerPallet', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за паллету + НДС', name: 'costPerPalletNds', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за коробку', name: 'costPerBox', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Стоимость за коробку + НДС', name: 'costPerBoxNds', type: "mask", mask: "#",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Температура (От)<sup class="red">*</sup>', name: 'tempFrom', type: "text",
                },
                {
                    label: 'Темература (До)<sup class="red">*</sup>', name: 'tempTo', type: "text",
                },
                {
                    label: 'Объем м<sup>3</sup>', name: 'volume<sup class="red">*</sup>', type: "mask", mask: "000",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {
                    label: 'Тоннаж<sup>Т.</sup>', name: 'tonnage<sup class="red">*</sup>', type: "mask", mask: "000",
                    maskOptions: {
                        reverse: true,
                        placeholder: ""
                    }
                },
                {label: 'Тип погрузки<sup class="red">*</sup>', name: 'loadingType', type: "selectize", options: loadingTypeOptions,
                },
                {label: 'Тип транспорта<sup class="red">*</sup>', name: 'vehicleType', type: "selectize", options: vehicleBodyTypeOptions,
                    opts:{
                    onChange:function (value) {
                        if(value==="Рефрижератор"){
                            routeEditor.field('tempTo').show();
                            routeEditor.field('tempFrom').show();
                        } else {
                            routeEditor.field('tempTo').hide();
                            routeEditor.field('tempFrom').hide();
                        }
                    }}},
                {
                    label: 'Догруз', name: 'afterLoad', type: "radio", options: [{ label:"Отдельная машина", value:false}, {label:"Возможен догруз",value:true}]
                },
                {label: 'Комментарий', name: 'comment', type: "textarea"},

            ]
        });


        var routeDataTable = $("#routesTable").DataTable({
                processing: true,
                serverSide: true,
                searchDelay: 800,

                ajax: {
                    contentType: 'application/json',
                    processing: true,
                    data: function (d) {
                        return JSON.stringify(d);
                    },
                    url: "dataTables/routesForUser", // json datasource
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
                        editor: routeEditor
                    },
                    {
                        extend: "edit",
                        editor: routeEditor
                    },
                    {
                        extend: "remove",
                        editor: routeEditor
                    }
                ],
                "paging": 10,
                "columnDefs": [
                    {"name": "id", "data": "id", "targets": 0},
                    {"name": "name", "data": "name", "targets": 1},
                    {
                        "name": "company.name",
                        "data": "company.name",
                        orderable: false,
                        "targets": 2,
                        defaultContent: ""
                    },
                    {
                        "name": "totalCost",
                        "data": null,
                        "targets": 3,
                        searchable: false,
                        orderable: false,
                        render: function (data, type, full) {
                            return (data.totalCost !== null && data.totalCostNds !== null) ? `${data.totalCost}/${data.totalCostNds}` : "";
                        }
                    },
                    {
                        "name": "costPerKilometer",
                        "data": null,
                        "targets": 4,
                        searchable: false,
                        orderable: false,
                        render: function (data, type, full) {
                            return (data.costPerKilometer !== null && data.costPerKilometerNds !== null) ? `${data.costPerKilometer}/${data.costPerKilometerNds}` : "";
                        }
                    },
                    {
                        "name": "costPerBox",
                        "data": null,
                        "targets": 5,
                        searchable: false,
                        orderable: false,
                        render: function (data, type, full) {
                            return (data.costPerBox !== null && data.costPerBoxNds !== null) ? `${data.costPerBox}/${data.costPerBoxNds}` : "";
                        }
                    },
                    {
                        "name": "costPerPoint",
                        "data": null,
                        searchable: false,
                        orderable: false,
                        "targets": 6,
                        render: function (data, type, full) {
                            return (data.costPerPoint !== null && data.costPerPointNds !== null) ? `${data.costPerPoint}/${data.costPerPointNds}` : "";
                        }
                    },
                    {
                        "name": "costPerHour",
                        "data": null,
                        searchable: false,
                        orderable: false,
                        "targets": 7,
                        render: function (data, type, full) {
                            return (data.costPerHour !== null && data.costPerHourNds !== null) ? `${data.costPerHour}/${data.costPerHourNds}` : "";
                        }
                    },
                    {
                        "name": "costPerPallet",
                        "data": null,
                        searchable: false,
                        orderable: false,
                        "targets": 8,
                        render: function (data, type, full) {
                            return (data.costPerPallet !== null && data.costPerPalletNds !== null) ? `${data.costPerPallet}/${data.costPerPalletNds}` : "";
                        }
                    },
                    {
                        "name": "tempTo",
                        "data": null,
                        searchable: false,
                        orderable: false,
                        "targets": 9,
                        render: function (data, type, full) {
                            return (data.tempTo !== null && data.tempFrom !== null) ? `${data.tempFrom}º-${data.tempTo}º` : "";
                        }
                    },
                    {
                        "name": "vehicleType",
                        "data": "vehicleType",
                        searchable: false,
                        orderable: false,
                        "targets": 10,
                        defaultContent: ""
                    },
                    {
                        "name": "volume",
                        "data": "volume",
                        searchable: false,
                        orderable: false,
                        "targets": 11,
                        defaultContent: "",
                        render: function (data, type, row, meta) {
                            return (data !== null) ? `${data}м<sup>3</sup>` : "";
                        }
                    },
                    {
                        "name": "tonnage",
                        "data": "tonnage",
                        searchable: false,
                        orderable: false,
                        "targets": 12,
                        defaultContent: "",
                        render: function (data, type, row, meta) {
                            return (data !== null) ? `${data}т` : "";
                        }
                    },
                    {
                        "name": "loadingType",
                        "data": "loadingType",
                        searchable: false,
                        orderable: false,
                        "targets": 13,
                        defaultContent: ""
                    },
                    {
                        "name": "afterLoad",
                        "data": "afterLoad",
                        searchable: false,
                        orderable: false,
                        "targets": 14,
                        render: function (data) {
                            return (data) ? "Возможен догруз" : "Отдельная машина"
                        },
                        defaultContent: ""
                    },
                    {
                        "name": "comment",
                        "data": "comment",
                        searchable: false,
                        orderable: false,
                        "targets": 15,
                        defaultContent: ""
                    },
                ]
            }
        );


        routeDataTable.on('select', function (e, dt, type, indexes) {
            if (type === 'row') {
                var data = routeDataTable.rows(indexes).data('id');
                reInitRoutePointTable(data[0].id);
            }
        });


        function reInitRoutePointTable(routeId) {
            $.get(`api/routes/${routeId}`).success(function (routeData) {
                routeHref = routeData._links.self.href;

                let routePointEditor = new $.fn.dataTable.Editor({
                    ajax: {
                        create: {
                            type: 'POST',
                            contentType: 'application/json',
                            url: 'api/routePoints',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    value["route"] = routeHref;
                                    newdata = JSON.stringify(value);
                                });
                                return newdata;
                            },
                            success: function (response) {
                                routePointDataTable.draw();
                                routePointEditor.close();
                                // alert(response.responseText);
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        },
                        edit: {
                            contentType: 'application/json',
                            type: 'PATCH',
                            url: 'api/routePoints/_id_',
                            data: function (d) {
                                let newdata;
                                $.each(d.data, function (key, value) {
                                    if (value['point'] == "") {
                                        delete value['point'];
                                    }
                                    if (value['contact'] == "") {
                                        delete value['contact'];
                                    }
                                    newdata = JSON.stringify(value);
                                });
                                return newdata;
                            },
                            success: function (response) {
                                routePointDataTable.draw();
                                routePointEditor.close();
                            },
                            error: function (jqXHR, exception) {
                                alert(response.responseText);
                            }
                        }
                        ,
                        remove: {
                            type: 'DELETE',
                            contentType: 'application/json',
                            url: 'api/routePoints/_id_',
                            data: function (d) {
                                return '';
                            }
                        }
                    },
                    table: '#routePointsTable',
                    idSrc: 'id',

                    fields: [
                        {
                            label: 'Расстояние (км)', name: 'distance', type: 'mask', mask: "#",
                            maskOptions: {
                                reverse: true,
                                placeholder: ""
                            }
                        },
                        {
                            label: 'Контакт', name: 'contact', type: 'selectize', options: [], opts: {
                                searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
                                load: function (query, callback) {
                                    $.get(`api/contacts/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
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
                                loadThrottle: 500
                            }
                        },
                        {
                            label: 'Стоимость ПРР (₽)', name: 'prrCost', type: 'mask', mask: "#",
                            maskOptions: {
                                reverse: true,
                                placeholder: ""
                            }
                        },
                        {
                            label: 'Время ПРР (м)', name: 'loadingTime', type: 'mask', mask: "#",
                            maskOptions: {
                                reverse: true,
                                placeholder: ""
                            }
                        },
                        {
                            label: 'Порядковый номер (№)', name: 'queueNumber', type: 'mask', mask: "#",
                            maskOptions: {
                                reverse: true,
                                placeholder: ""
                            },
                            fieldInfo: "Первый пункт разгрузки - 1, второй - 2 и т.д."
                        },
                        {
                            label: 'Пункт', name: 'point', type: 'selectize', options: [], opts: {
                                searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
                                load: function (query, callback) {
                                    $.get(`api/points/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
                                        function (data) {
                                            let pointOptions = [];
                                            data._embedded.points.forEach(function (entry) {
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
                                loadThrottle: 500
                            }
                        }
                    ]
                });


                // routePointEditor.field('point').input().on('keyup', function (e, d) {
                //     var namePart = $(this).val();
                // });

                // routePointEditor.on('initEdit',function () {
                //     console.log(routePointDataTable.rows());
                //         console.log(routePointEditor.field('point').inst().$control_input.prop('placeholder',routePointDataTable.rows()[routePointEditor.modifier()].data().point.name));
                //     });

                let routePointDataTable = $("#routePointsTable").DataTable({
                    processing: true,
                    serverSide: true,
                    aaSortingFixed: [[5, 'asc']],
                    destroy: true,
                    searchDelay: 800,
                    ajax: {
                        contentType: 'application/json',
                        processing: true,
                        data: function (d) {
                            return JSON.stringify(d);
                        },
                        url: `/dataTables/routePoints/routePointsForRoute/${routeId}`, // json datasource
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
                            editor: routePointEditor
                        },
                        {
                            extend: "edit",
                            editor: routePointEditor
                        },
                        {
                            extend: "remove",
                            editor: routePointEditor
                        }
                    ],
                    "paging": 10,
                    "columnDefs": [
                        {"name": "id", "data": "id", searchable: false, orderable: false, "targets": 0, visible: false},
                        {"name": "point.name", "data": "point.name", "targets": 1, defaultContent: ""},
                        {"name": "point.address", "data": "point.address", "targets": 2, defaultContent: ""},
                        {"name": "contact.name", "data": "contact.name", targets: 3, defaultContent: ""},
                        {
                            "name": "distance",
                            "data": "distance",
                            "targets": 4,
                            searchable: false,
                            orderable: false,
                            render: function (data) {
                                return (data !== null) ? `${data}км` : "";
                            }
                        },

                        // {
                        //     "name": "cost",
                        //     "data": "cost",
                        //     "targets": 4,
                        //     searchable: false,
                        //     orderable: false,
                        //     render: function (data) {
                        //         return (data !== null) ? `${data}₽` : "";
                        //     }
                        // },
                        {
                            "name": "prrCost",
                            "data": "prrCost",
                            "targets": 5,
                            searchable: false,
                            orderable: false,
                            render: function (data) {
                                return (data !== null) ? `${data}₽` : "";
                            }
                        },
                        {
                            "name": "loadingTime",
                            data: "loadingTime",
                            "targets": 6,
                            searchable: false,
                            orderable: false,
                            render: function (data) {
                                return (data !== null) ? `${data}м` : "";
                            }
                        },
                        {
                            "name": "queueNumber", "data": "queueNumber", "targets": 7, render: function (data) {
                                if (data != null) {
                                    return (data != "0") ? `№${data}` : "Склад отправки";
                                } else return "";
                            }
                        }
                    ]
                });
            });
        }

        routeEditor.on('open', function () {
            $('#routesForm').closest(".DTE_Action_Edit").parent().parent().css('max-width', '775px');
            $('#routesForm').closest(".DTE_Action_Create").parent().parent().css('max-width', '775px');
        });


        routeEditor.on('close', function () {
            $('#routesForm').closest(".DTE_Action_Edit").parent().parent().css('max-width', '500px');
            $('#routesForm').closest(".DTE_Action_Create").parent().parent().css('max-width', '500px');
        });

        //Validation
        routeEditor.on('preSubmit', function (e, o, action) {
            if (action !== 'remove') {
                checkedFields = [
                    this.field('name'),
                    this.field('tonnage'),
                    this.field('volume'),
                    this.field('loadingType'),
                    this.field('vehicleType')
                ];
                checkedPriceFields = [
                    {
                        nonds: this.field('totalCost'),
                        nds: this.field('totalCostNds')
                    },
                    // {
                    //     nonds: this.field('costPerKilometer'),
                    //     nds: this.field('costPerKilometerNds')
                    // }
                    // ,
                    // {
                    //     nonds: this.field('costPerBox'),
                    //     nds: this.field('costPerBoxNds')
                    // }
                    // ,
                    // {
                    //     nonds: this.field('costPerPallet'),
                    //     nds: this.field('costPerPalletNds')
                    // }
                    // ,
                    // {
                    //     nonds: this.field('costPerHour'),
                    //     nds: this.field('costPerHourNds')
                    // }
                    // ,
                    // {
                    //     nonds: this.field('costPerPoint'),
                    //     nds: this.field('costPerPointNds')
                    // }
                ];

                let companyField = this.field('company');

                if (action == 'create') {
                    if (companyField.val() == '') {
                        companyField.error("Компания должна быть указана при создании");
                        console.log("error in: " + "company")
                    }
                }

                let tempFields = {
                    from: this.field('tempFrom'),
                    to: this.field('tempTo')
                };

                if (this.field('vehicleType').val() == 'Рефрижератор') {
                    if (tempFields.from.val() == '') {
                        tempFields.from.error("Поле должно быть указано");
                        console.log("error in: " + "tempfrom")
                    }
                    if (tempFields.to.val() == '') {
                        tempFields.to.error("Поле должно быть указано");
                        console.log("error in: " + "temp")
                    }
                }

                for (let priceField of checkedPriceFields) {
                    if (priceField.nds.val() == '' && priceField.nonds.val() == '') {
                        priceField.nds.error("Одна из цен должна быть указана");
                        priceField.nonds.error("Одна из цен должна быть указана");
                        console.log("error in: " + "price field");
                    }
                }

                for (let field of checkedFields) {
                    if (field.val() == '') {
                        field.error('Поле должно быть указано');
                        console.log("error in: " + "checkedField")
                    }
                }

                // Only validate user input values - different values indicate that
                // the end user has not entered a value

                // ... additional validation rules

                // If any error was reported, cancel the submission so it can be corrected
                if (this.inError()) {
                    console.log("inError");
                    return false;
                }
            }
        });
    })

});
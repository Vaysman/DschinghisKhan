$(document).ready(function () {
    $('#tab-dispatched-offers-link a').one('shown.bs.tab', function () {
        let offerEditor = new $.fn.dataTable.Editor({
            table: '#dispatchedOffersTable',
            idSrc: 'id',
            ajax: {
                edit: {
                    contentType: 'application/json',
                    type: 'PATCH',
                    url: 'api/orderOffers/_id_',
                    data: function (d) {
                        let newdata;
                        $.each(d.data, function (key, value) {
                            newdata = JSON.stringify(value);
                        });
                        return newdata;
                    },
                    success: function (response) {

                        dispatchedOfferTable.draw();
                        offerEditor.close();

                    },
                    error: function (jqXHR, exception) {
                        alert(exception.responseText);
                    }
                }
            },
            fields: [
                {
                    label: 'Водитель', name: 'driver', type: 'selectize',
                    options: [],
                    opts: {
                        searchField: "label",
                        create: false,
                        placeholder: "Нажмите, чтобы изменить",
                        maxItems: 1,
                        loadThrottle: 400,
                        preload: true,
                        delimiter: null,
                        load: function (query, callback) {
                            $.get(`api/drivers/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
                                function (data) {
                                    console.log(data);
                                    var driverOptions = [];
                                    data._embedded.drivers.forEach(function (entry) {
                                        driverOptions.push({"label": entry.name, "value": entry._links.self.href});
                                    });
                                    callback(driverOptions);
                                }
                            );
                        },
                    }
                },
                {
                    label: "Транспорт", name: 'transport', type: 'selectize',
                    options: [],
                    opts: {
                        placeholder: "Нажмите, чтобы измнить",
                        labelField: "label",
                        loadThrottle: 400,
                        preload: true,
                        maxItems: 1,
                        create: false,
                        delimiter: null,
                        load: function (query, callback) {
                            $.get(`api/transports/search/findTop10ByNumberContainingAndOriginator/?number=${query}&originator=${currentCompanyId}`,
                                function (data) {
                                    console.log(data);
                                    var transportOptions = [];
                                    data._embedded.transports.forEach(function (entry) {
                                        transportOptions.push({"label": entry.number, "value": entry._links.self.href});
                                    });
                                    callback(transportOptions);
                                }
                            );
                        },
                    }
                }
            ]
        });

        let dispatchedOfferTable = $("table#dispatchedOffersTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/dispatchedOffers", // json datasource
                type: "POST"  // method  , by default get
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
                    extend: "edit",
                    editor: offerEditor,
                    text: "Назначить водителя/транспорт"
                },
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {
                    "name": "orderNumber", "data": "orderNumber", "targets": 1, render: function (data, type, full) {
                        return `<a target="_blank" href='info/orders/${full.id}'>${data}</a>`;
                    }
                },
                {
                    "name": "proposedPrice",
                    "data": "proposedPrice",
                    "targets": 2,
                    defaultContent: ""
                },
                {
                    "name": "dispatcherPrice",
                    "data": "dispatcherPrice",
                    "targets": 3,
                    defaultContent: ""
                },
                {
                    "name": "driver.name",
                    "data": "driver.name",
                    "targets": 4,
                    defaultContent: ""
                },
                {
                    "name": "transport.number",
                    "data": "transport.number",
                    "targets": 5,
                    defaultContent: ""
                },
                {
                    "name": "proposedPriceComment",
                    "data": "proposedPriceComment",
                    "targets": 6,
                    searchable: false,
                    orderable: false,
                    defaultContent: ""
                }]
        })
    });
});
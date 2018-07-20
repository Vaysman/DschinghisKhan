$(document).ready(function () {
    $("#orderHistoryTable").DataTable({
            processing: true,
            serverSide: true,
            aaSortingFixed: [[0, 'desc']],
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: `/dataTables/orderHistory/${currentOrderId}`, // json datasource
                type: "post"  // method  , by default get
            },
            dom: 'Brtip',
            language: {
                url: '/localization/dataTablesRus.json'
            },
            buttons:[],
            "paging": 20,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "orderStatus", "data": "orderStatus", "targets": 1, searchable: false, orderable: false},
                {"name": "actionCompany", "data": "actionCompany", "targets": 2,searchable:false, orderable:false},
                {"name": "actionUser", "data": "actionUser", "targets": 3,searchable:false, orderable:false},
                {"name": "action", "data": "action", "targets": 4,searchable:false, orderable:false},
                {"name": "dispatcherPrice", "data": "dispatcherPrice", "targets": 5,searchable:false, orderable:false},
                {"name": "companyPrice", "data": "companyPrice", "targets": 6,searchable:false, orderable:false},
                {"name": "date", "data": "date", "targets": 7,searchable:false, orderable:false, defaultContent:""}
            ]
        }
    );

});
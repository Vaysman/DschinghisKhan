function initReference(reference) {

};


Vue.component('pending-order-info', {
    props: ['order'],
    template:
    '<div v-if="order" class="col-7" id="pendingOrderInfo">' +
    '<h5 class="red-first-letter">{{order.number}}</h5>' +
    '    <div class="row">' +
    '      <div class="col-4">' +
    '           Маршрут:<br>' +
    '           {{order.route.name}}<br>' +
    '       <br>' +
    '         Дата оплаты:<br>' +
    '         {{order.paymentDate}}<br><br>' +
    '         ' +
    '         Требования:' +
    '         <ul>' +
    '           <li v-for="requirement in order.requirements">{{requirement}}</li>' +
    '         </ul>' +
    '<br>' +
    '         </div>' +
    '       <div class="col-4">' +
    '           Обязательность:<br>{{order.orderObligation}}<br><br>' +
    '           Дата возврата документов:<br>' +
    '           {{order.documentReturnDate}} <br><br>' +
    '         Груз:' +
    '       <ul>' +
    '           <li v-for="cargoUnit in order.cargo">{{cargoUnit}}</li><br>' +
    '                            ' +
    '       </ul>' +
    '       </div>' +
    '       <div class="col-4">' +

    '           Пункты доставки:' +
    '           <ol>' +
    '           <li v-for="routePoint in order.route.routePoints">{{routePoint.point.name}} </li>' +
    '           </ol>' +
    '              ' +
    '                        </div>' +
    '                    </div>' +
    '                </div>',
    mounted: function () {
        console.log(this.order);
    },
});

var orderList = new Vue({
    el: '#pendingOrders',
    data: {
        pendingOrders: pendingOrders,
        numbers: "fgsfds",
        order: null,
        orderId: null,
        driverId: null,
        transportId: null,
        proposedPrice: null
    },
    methods: {
        loadOrderInfo: function (orderId) {
            let that = this;
            $.get(`/data/orders/${orderId}`).then((orderInfo) => {
                that.orderId = orderId;
                that.order = orderInfo;
                that.proposedPrice = orderInfo.dispatcherPrice;
                console.log(orderInfo);
            })
        },
        acceptOrder: function () {
            let that = this;
            $.ajax({
                url: `/orderLifecycle/accept/${this.orderId}`,
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data:
                    JSON.stringify({
                        companyId: currentCompanyId,
                        driverId: this.driverId,
                        transportId: this.transportId,
                        proposedPrice: this.proposedPrice
                    }),
                success: function (response) {

                    that.pendingOrders = that.pendingOrders.filter(pOrder => pOrder.id !== that.orderId);
                    that.order = null;
                    alert(response)
                }
            })
        },
        rejectOrder: function () {
            let that = this;
            $.ajax({
                url: `/orderLifecycle/reject/${that.orderId}`,
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: currentCompanyId,
                success: function (response) {
                    that.pendingOrders = that.pendingOrders.filter(pOrder => pOrder.id !== that.orderId);
                    that.order = null;
                    alert(response)
                }
            })
        }
    },
    mounted: function () {
        // let that = this;
        let driverSelectize = $("#orderDriverSelect").selectize({
            placeholder: "Выберите водителя",
            labelField: "label",
            valueField: "value",
            loadThrottle: 400,
            maxItems: 1,
            create: false,
            load: function (query, callback) {
                $.get(`api/drivers/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
                    function (data) {
                        console.log(data);
                        var driverOptions = [];
                        data._embedded.drivers.forEach(function (entry) {
                            driverOptions.push({"label": entry.name, "value": entry.id});
                        });
                        callback(driverOptions);
                    }
                );
            },
            onChange: (value) => {
                this.driverId = value;
            }
        });

        let transportSelectize = $("#orderTransportSelect").selectize({
            placeholder: "Выберите транспорт",
            labelField: "label",
            valueField: "value",
            loadThrottle: 400,
            maxItems: 1,
            create: false,
            load: function (query, callback) {
                $.get(`api/transports/search/findTop10ByNumberContainingAndOriginator/?number=${query}&originator=${currentCompanyId}`,
                    function (data) {
                        console.log(data);
                        var transportOptions = [];
                        data._embedded.transports.forEach(function (entry) {
                            transportOptions.push({"label": entry.number, "value": entry.id});
                        });
                        callback(transportOptions);
                    }
                );
            },
            onChange: (value) => {
                this.transportId = value;
            }
        });
    }
});






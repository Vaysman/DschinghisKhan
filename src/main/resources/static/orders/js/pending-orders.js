function initReference(reference) {

};



var orderList = new Vue({
    el: '#pendingOrders',
    data: {
        pendingOrders: pendingOrders,
        numbers: "fgsfds",
        order: null,
        orderId: null,
        driverId: null,
        transportId: null,
        proposedPrice: null,
        proposedPriceComment: ""
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
            if(this.driverId==null||this.transportId==null) {
                if (confirm("Вы не указали транспорт/водителя\n Вы можете указать их в течении 2-х часов на вкладке с предложениями по заявкам. \nЕсли данные не будут указаны - предложение будет отклонено автоматическии.")){

                }
            } else {
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
                            proposedPrice: this.proposedPrice,
                            proposedPriceComment: this.proposedPriceComment
                        }),
                    success: function (response) {

                        that.pendingOrders = that.pendingOrders.filter(pOrder => pOrder.id !== that.orderId);
                        that.order = null;
                        alert(response)
                    }
                })
            }

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
        },
        getOrderColor(isMandatory) {
            console.log(isMandatory);
            if (isMandatory == "true") {
                return "list-group-item-danger"
            } else {
                return ""
            }
        }
    },
    mounted: function () {
        // let that = this;
        let driverSelectize = $("#orderDriverSelect").selectize({
            placeholder: "Выберите водителя",
            labelField: "label",
            valueField: "value",
            loadThrottle: 400,
            preload: true,
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
            preload: true,
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






$(document).ready(function () {
    function initReference(reference) {
        $.get(reference).success(function (data) {
            // console.log(data);
            return data;
        }).fail(function () {
            return null;
        });
    };

    Vue.component('pendingOrderInfo',{
        el: '#pendingOrderInfo',
        data: {
            order: {
                number: 'asdfasd',
                orderObligation: '',
                paymentDate: 'fasdf',
                rating: '',
                status: '',
                documentReturnDate: 'asdfasd',
                cargo: [],
                requirements: [],
                _links: []
            },
            route: {name:''},
            pickupPoint: {name:''},
            dropPoint: {name:''}
        }
    });


    var orderList = new Vue({
        el: '#pendingOrders',
        data: {
            pendingOrders: pendingOrders,
            numbers: "fgsfds"
        },
        methods: {
            loadOrderInfo: function (orderId) {
                $.get(`/api/orders/${orderId}`).then((orderInfo) => {

                    // let pickupPoint;
                    // let dropPoint;
                    // let route;
                    // $.get(orderInfo.pickupPoint).then((ppoint) => pickupPoint=ppoint);
                    // $.get(orderInfo.dropPoint).then((dpoint) => dropPoint=dpoint);
                    // $.get(orderInfo.route).then((route) => route=route);
                    // pickupPoint = $.get(orderInfo.pickupPoint);
                    orderInfoVue.orderInfo = orderInfo;
                    orderInfoVue.pickupPoint = initReference(orderInfo._links.pickupPoint.href);
                    orderInfoVue.dropPoint = initReference(orderInfo._links.dropPoint.href);
                    orderInfoVue.route = initReference(orderInfo._links.route.href);
                });
            }
        }
    });


});

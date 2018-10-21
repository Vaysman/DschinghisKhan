var ordersInWorkOnMap = new Vue({
    el: '#ordersInWorkMap',
    data: {
        currentCompanyId: currentCompanyId,
        orders: null,
        order: null,
        contractId: null,
        totalPages: null,
        pageNumber: null,
        orderMap: null
    },
    methods: {
        loadOrderInfo: function (orderId) {
            let that = this;
            $.get(`/data/orders/${orderId}`).then((orderInfo) => {
                that.order = orderInfo;
                console.log(JSON.stringify(orderInfo));
                that.refreshRoute(orderInfo.order.route.routePoints);
            })
        },
        refreshList: function (ordersPage) {
            this.orders = ordersPage._embedded.orders;
            this.totalPages = ordersPage.page.totalPages;
            this.pageNumber = ordersPage.page.number;
        },
        refreshRoute: function (routePoints) {

            let that = this;
            if(routePoints!=null){
                let points = [];
                routePoints.forEach(routePoint =>{
                    if(routePoint.point.fullAddress==null){
                        points.push(routePoint.point.address);
                    }else{
                        points.push(routePoint.point.fullAddress)
                    }

                });
                ymaps.route(points, {
                    mapStateAutoApply: true
                }).then(function (route) {
                    route.options.set("mapStateAutoApply", true);
                    that.orderMap.geoObjects.removeAll();
                    that.orderMap.geoObjects.add(route);
                    console.log(JSON.stringify(routePoints));
                })
            }
        }
    },
    mounted: function () {
        let that = this;
        $.get(`api/orders/search/findOrdersByOriginatorAndRouteNotNullAndStatusNot?originator=${this.currentCompanyId}&status=DELETED&size=12`).then(orders =>{
            this.refreshList(orders);

        });
        ymaps.ready(function () {
            if (typeof ymaps !== 'undefined') {
                that.orderMap = new ymaps.Map('orderMap', {
                    center: [40, 50],
                    zoom: 15
                }, {
                    searchControlProvider: 'yandex#search'
                });
            } else {
                console.log("ymaps is undefined (????)");
            }
        })
    }
});






var ordersInWorkOnMap = new Vue({
    el: '#ordersInWorkMap',
    data: {
        currentCompanyId: currentCompanyId,
        orders: null,
        orderInfo: null,
        contractId: null,
        totalPages: null,
        pageNumber: null,
        orderMap: null,
        pageRange: null,
        previous: null,
        next: null,
        search: "",
        pageSize: 5
    },
    methods: {
        loadOrderInfo: function (orderId) {
            let that = this;
            $.get(`/data/orders/${orderId}`).then((orderInfo) => {
                that.orderInfo = orderInfo;
                console.log(JSON.stringify(orderInfo));
                that.refreshRoute(orderInfo.order.route.routePoints);
            })
        },
        reloadList: function (){
            $.get(`api/orders/search/findOrdersByOriginatorAndRouteNotNullAndStatusNot?originator=${this.currentCompanyId}&status=DELETED&number=${this.search}&size=${this.pageSize}&sort=id,desc`)
                .then(orders => {
                    this.refreshList(orders);
                });
        },
        refreshList: function (ordersPage) {
            this.orders = ordersPage._embedded.orders;
            this.totalPages = ordersPage.page.totalPages;
            this.pageNumber = ordersPage.page.number;
            if(ordersPage._links.hasOwnProperty("prev")){
                this.previous = ordersPage._links.prev.href;
            } else {
                this.previous = null;
            }
            if(ordersPage._links.hasOwnProperty("next")){
                this.next = ordersPage._links.next.href;
            } else {
                this.next = null;
            }
            this.figureRange();
            console.log(JSON.stringify(this.pageRange));
        },
        refreshRoute: function (routePoints) {

            let that = this;
            if (routePoints != null) {
                let points = [];
                routePoints.forEach(routePoint => {
                    if (routePoint.point.fullAddress == null) {
                        points.push(routePoint.point.address);
                    } else {
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
        },
        loadPage: function (newPageNumber) {
            // let that = this;
            $.get(`api/orders/search/findOrdersByOriginatorAndRouteNotNullAndStatusNotAndNumberContaining?originator=${this.currentCompanyId}&status=DELETED&number=${this.search}&size=${this.pageSize}&page=${newPageNumber}&sort=id,desc`)
                .then(orders => {
                    this.refreshList(orders);
                });
        },
        nextPage: function(){
            $.get(`${this.next}`)
                .then(orders => {
                    this.refreshList(orders);
                });
        },
        previousPage: function(){
            $.get(`${this.previous}`)
                .then(orders => {
                    this.refreshList(orders);
                });
        },
        filterData: function(number){
            this.search = number;
            $.throttle(400, this.reloadList())
        },
        figureRange: function () {
            let range = [];
            for(let i = -2; i<=2; i++){
                if((this.pageNumber+i)>=0&&(this.pageNumber+i)<this.totalPages){
                    range.push(this.pageNumber+i);
                }

                //TODO: figure out how to get a range of
                // i____________|-2__-1__0___1___2
                // 0	        |i	 1	 2	 3	 4
                // 1	        |0	 i	 2	 3	 4
                // >=2 & <=tpg-2|i-2 i-1 i	 i+1 i+2
                // max-1	    |i-3 i-2 i-1 i	 i+1
                // max	        |i-4 i-3 i-2 i-1 i
                //
                // if(i>=2 && i<=(this.totalPages-2)){
                //     range.push(this.pageNumber+i);
                // } else if (i<2){
                //     range.push(i+2);
                // } else if (i>(this.totalPages-2)){
                //     range.push(i-2)
                // }
            }
            this.pageRange = range;
        }
    },
    mounted: function () {
        let that = this;
        $.get(`api/orders/search/findOrdersByOriginatorAndRouteNotNullAndStatusNot?originator=${this.currentCompanyId}&status=DELETED&number=${this.search}&size=${that.pageSize}&sort=id,desc`)
            .then(orders => {
                this.refreshList(orders);
            });
        ymaps.ready(function () {
            if (typeof ymaps !== 'undefined') {
                that.orderMap = new ymaps.Map('orderMap', {
                    center: [55.7558, 37.6173],
                    zoom: 6
                }, {
                    searchControlProvider: 'yandex#search'
                });
            } else {
                console.log("ymaps is undefined (????)");
            }
        })
    }
});






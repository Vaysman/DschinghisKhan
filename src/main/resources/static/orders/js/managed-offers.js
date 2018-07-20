var offerList = new Vue({
    el: '#managedOffers',
    data: {
        managedOffers: managedOffers,
        offer: null,
        offerId: null,
        orderIf: null
    },
    methods: {
        loadOfferInfo: function (offerId) {
            let that = this;
            $.get(`/data/offers/${offerId}`).then((offerInfo) => {
                that.offerId = offerInfo.id;
                that.offer = offerInfo;
                console.log(offerInfo);
            })
        },
        acceptOffer: function () {
            let that = this;
            $.ajax({
                url: `/orderLifecycle/confirm/${that.offerId}`,
                type: "POST",
                success: function (response) {
                    if (response == "Success") {
                        that.managedOffers = that.managedOffers.filter(mOffer => mOffer.orderNumber !== that.offer.orderNumber);
                        that.offer = null;
                        alert("Заявка принята в работу")
                    } else {
                        alert(response)
                    }
                },
                error: function (fgsfds, error) {
                    alert(error)
                }
            })
        },
        rejectOffer: function () {
            let that = this;
            $.ajax({
                url: `/orderLifecycle/declineOffer/${that.offerId}`,
                type: "POST",
                success: function (response) {
                    that.managedOffers = that.managedOffers.filter(mOffer => mOffer.id !== that.offerId);
                    that.offer = null;
                    alert(response)
                },
                error: function (fgsfds, error) {
                    alert(error)
                }
            })
        },
        getOfferColor(isPriceChanged) {
            console.log(isPriceChanged);
            if (isPriceChanged != "false") {
                return ""
            } else {
                return "red"
            }
        }
    },
});






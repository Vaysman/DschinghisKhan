var sentContractsList = new Vue({
    el: '#sentContracts',
    data: {
        sentContracts: sentContracts,
        contract: null,
        contractId: null
    },
    methods: {
        loadContractInfo: function (contractId) {
            let that = this;
            $.get(`/data/contracts/${contractId}`).then((contractInfo) => {
                that.contractId = contractId;
                that.contract = contractInfo;
                console.log(contractInfo);
            })
        }
    },
});






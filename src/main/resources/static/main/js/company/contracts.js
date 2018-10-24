var receivedContractsList = new Vue({
    el: '#receivedContracts',
    data: {
        receivedContracts: receivedContracts,
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






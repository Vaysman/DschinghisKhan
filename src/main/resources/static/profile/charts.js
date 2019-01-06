$(document).ready(function () {
    pickmeup('#ordersPeriod',{
        mode: 'range',
        format: 'd/m/Y',
        date:'01/06/2018 - 01/01/2019'
    });

    pickmeup('#companiesPeriod',{
        mode: 'range',
        format: 'd/m/Y',
        date:'01/06/2018 - 01/01/2019'
    });

    $('#reloadOrdersChart').click(function () {
        reloadOrdersChart();
    });
    function reloadOrdersChart(){
        let from = $('#ordersPeriod').val().split(' - ')[0];
        let to = $('#ordersPeriod').val().split(' - ')[1];
        console.log(from);
        console.log(to);

        $.get(`chart/orders?to=${to}&from=${from}`, function (ordersPeriodChartData) {
            console.log(ordersPeriodChartData);
            Highcharts.chart('ordersPeriodChart', {
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false,
                    type: 'pie'
                },
                title: {
                    text: 'Заявки за период'
                },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.y}</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true
                        },
                        showInLegend: true
                    }
                },
                series: [{
                    name: 'Заявки',
                    colorByPoint: true,
                    data: ordersPeriodChartData
                }]
            });
        })


    }

    $('#reloadCompaniesChart').click(function () {
        reloadCompaniesChart()
    });
    function reloadCompaniesChart(){
        let from = $('#companiesPeriod').val().split(' - ')[0];
        let to = $('#companiesPeriod').val().split(' - ')[1];
        console.log(from);
        console.log(to);

        $.get(`chart/ordersPerTransportCompany?to=${to}&from=${from}`, function (ordersPeriodChartData) {
            console.log(ordersPeriodChartData);
            Highcharts.chart('companiesChart', {
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false,
                    type: 'pie'
                },
                title: {
                    text: 'Заявки на перевозчика'
                },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.percentage.:1f}%</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true
                        },
                        showInLegend: true
                    }
                },
                series: [{
                    name: 'Процент заявок на перевозчика',
                    colorByPoint: true,
                    data: ordersPeriodChartData
                }]
            });
        })


    }



    $('#tab-dashboard-link a').one('shown.bs.tab', function () {
        reloadCompaniesChart();
        reloadOrdersChart();
    });

});
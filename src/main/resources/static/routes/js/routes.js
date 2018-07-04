$(document).ready(function () {
    $.get('/data/routes', function (data) {
        console.log(data);
        data.forEach(function (route) {
            let routeCardHtml =
                `<div class="card">
                <div class="card-header">${route.name}</div>
                <div class="card-body">ТК:${route.transportCompany.name} (${route.transportCompany.shortName})</div>
                </div>`;
            $("#routeContainer").append(routeCardHtml);
        })
    })
});
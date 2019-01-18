$(document).ready(function () {
   $("#selectCompaniesForReview").selectize({
       maxItems: 10,
       labelField: "label",
       searchField: "label", create: false, placeholder: "Нажмите, чтобы выбрать",
       load: function (query, callback) {
           $.get(`api/companies/search/findTop10ByNameContainingAndType/?name=${query}&type=TRANSPORT`,
               function (data) {
                   var companyOptions = [];
                   data._embedded.companies.forEach(function (entry) {
                       companyOptions.push({"label": entry.name, "value": entry.id});
                   });
                   callback(companyOptions);
               }
           );
       },
       loadThrottle: 400,
       preload: true,
       onChange: (value) => {
           if(value.trim()===""){
               $("#createNewReview").attr("disabled","disabled")
           } else if($("#selectRouteForReview").val().trim()!=="") {
               $("#createNewReview").removeAttr("disabled")
           }
           console.log(JSON.stringify(value));
       }
   });


   $("#selectRouteForReview").selectize({
       delimiter: null,
       labelField: "label",
       maxItems: 1,
       searchField: "label", create: false, placeholder: "Нажмите, чтобы изменить",
       valueField: "value",
       loadThrottle: 400,
       preload: true,
       load: function (query, callback) {
           $.get(`api/routes/search/findTop10ByNameContainingAndOriginator/?name=${query}&originator=${currentCompanyId}`,
               function (data) {
                   var routeOptions = [];
                   data._embedded.routes.forEach(function (entry) {
                       routeOptions.push({"label": entry.name, "value": entry.id});
                   });
                   callback(routeOptions);
               }
           );
       },
       onChange: (value) => {
           if(value.trim()===""){
               $("#createNewReview").attr("disabled","disabled")
           } else if($("#selectCompaniesForReview").val().trim()!=="") {
               $("#createNewReview").removeAttr("disabled")
           }
           console.log(JSON.stringify(value));
       }
   });


    $("#createNewReview").click(function () {

        $(this).attr("disabled","disabled");



        $.ajax({
            url: `reviews/create/${$("#selectRouteForReview").val()}`,
            type: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data:
                JSON.stringify($("#selectCompaniesForReview").val().split(",").map(function(item){return parseInt(item)})),
            success: function (response) {
                location.reload();
            }
        })
    })
});
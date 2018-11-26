$(document).ready(function () {

    let usersEditor = new $.fn.dataTable.Editor({
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/users',
                data: function (d) {
                    let newdata;

                    $.each(d.data, function (key, value) {
                        value.originator = currentCompanyId;
                        value.company = currentCompany._links.self.href;
                        value.userRole = "ТЭК";
                        newdata = JSON.stringify(value);
                    });
                    console.log(newdata);
                    return newdata;
                },
                success: function (response) {
                    usersDataTable.draw();
                    usersEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/users/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        if(value.passAndSalt=="" || value.passAndSalt=="dummy"){
                            delete value["passAndSalt"];
                        } else {
                            value.salt = "";
                        }
                        newdata = JSON.stringify(value);

                    });
                    return newdata;
                },
                success: function (response) {
                    usersDataTable.draw();
                    usersEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(jqXHR.errorJSON.cause.cause.message);
                }
            }
            ,
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/users/_id_',
                data: function () {
                    return '';
                }
            }
        },
        table: '#companyUsersTable',
        idSrc: 'id',


        fields: [
            {label: 'Имя', name: 'username', type: 'text',compare: function ( a, b ) {
                    return (a===b);
                }},
            {label: 'Логин', name: 'login', type: 'text',compare: function ( a, b ) {
                    return (a===b);
                }},
            {label: 'Пароль', name: 'passAndSalt', type: 'password',data: function () {
                    return "dummy";
                },compare: function ( a, b ) {
                    return (a===b);
                }},
            {label: 'E-Mail', name: 'email', type: 'text'},
        ]
    });



    let usersDataTable = $("#companyUsersTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/usersForUser", // json datasource
                type: "post"  // method  , by default get
            },
            dom: 'Bfrtp',
            language: {
                url: '/localization/dataTablesRus.json'
            },
            select: {
                style: 'single'
            },
            "buttons": [
                {
                    extend: "create",
                    editor: usersEditor
                },
                {
                    extend: "edit",
                    editor: usersEditor
                },
                {
                    extend: "remove",
                    editor: usersEditor
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "username", "data": "username", "targets": 1},
                {"name": "login", "data": "login", "targets": 2},
                {"name": "passAndSalt", "data":"passAndSalt", defaultContent:"dummy", mRender: function () {
                        return "dummy";
                    }, def: "dummy",
                    "targets":3, visible:false},
                {"name": "email", "data": "email", "targets": 4},
            ]
        }
    );
    let currentCompany;
    $.get(`/api/companies/${currentCompanyId}`).success(function (data) {
        currentCompany = data;
        console.log(currentCompany);
    });
    // console.log(JSON.stringify(userRoles));

    usersEditor.on( 'preSubmit', function ( e, o, action ) {
        if ( action !== 'remove' ) {
            let username = this.field('username');
            let login = this.field('login');
            let passAndSalt = this.field('passAndSalt');

            // Only validate user input values - different values indicate that
            // the end user has not entered a value
            if(!username.val()){
                username.error("Имя пользователя должно быть указано")
            }
            if(!login.val()){
                login.error("Логин должен быть указан")
            }
            if(action!=='edit'&&(!passAndSalt.val() || passAndSalt.val()==="dummy") ){
                passAndSalt.error("Пароль должен быть указан")
            }
            // ... additional validation rules

            // If any error was reported, cancel the submission so it can be corrected
            if ( this.inError() ) {
                return false;
            }
        }
    } );


});
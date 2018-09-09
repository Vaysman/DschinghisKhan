$(document).ready(function () {
    let currentCompany;
    $.get(`/api/companies/${currentCompanyId}`).success(function (data) {
        currentCompany = data;
        // console.log(currentCompany);
    });
        // console.log(JSON.stringify(userRoles));
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
                            value.role = "Диспетчер";
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
                        alert(response.responseText);
                    }
                }
                ,
                remove: {
                    type: 'DELETE',
                    contentType: 'application/json',
                    url: 'api/users/_id_',
                    data: function (d) {
                        return '';
                    }
                }
            },
            table: '#usersTable',
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

        usersEditor.on( 'preSubmit', function ( e, o, action ) {
            if ( action !== 'remove' ) {
                var username = this.field( 'username' );
                var login = this.field( 'login' );
                var passAndSalt = this.field( 'passAndSalt' );
                var userRole = this.field( 'userRole' );

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
                if(!userRole.val()){
                    userRole.error("Роль должна быть указана")
                }
                // ... additional validation rules

                // If any error was reported, cancel the submission so it can be corrected
                if ( this.inError() ) {
                    return false;
                }
            }
        } );


        var usersDataTable = $("#usersTable").DataTable({
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
                dom: 'Bfrtip',
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

});
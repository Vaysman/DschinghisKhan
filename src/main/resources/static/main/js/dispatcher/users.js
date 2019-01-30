$(document).ready(function () {
    let currentCompany;
    $.get(`/api/companies/${currentCompanyId}`).success(function (data) {
        currentCompany = data;
        // console.log(currentCompany);
    });
    // console.log(JSON.stringify(userRoles));

    let resendEditor = new $.fn.dataTable.Editor({
        table: '#usersTable',
        idSrc: 'id',
        ajax: {
            edit: {
                type: 'POST',
                contentType: 'application/json',
                url: 'resendPassword/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify({
                            email: value.email
                        })
                    });
                    return newdata;
                },
                success: function (response) {
                    resendEditor.close();
                },
                error: function (jqXHR) {

                    console.log(jqXHR);
                    resendEditor.field('email').error(jqXHR.responseJSON.message);
                }
            }
        },
        fields: [
            {label: 'E-Mail', name: 'email', type: 'text'},
        ]
    });

    let usersEditor = new $.fn.dataTable.Editor({
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/users',
                data: function (d) {
                    let newdata;

                    $.each(d.data, function (key, value) {
                        delete value.repeatPassword;
                        value.originator = currentCompanyId;
                        value.company = currentCompany._links.self.href;
                        value.userRole = "Диспетчер";
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
                error: function (response) {
                    let message = response.responseJSON.cause.cause.message;
                    if(message.includes("Duplicate") || message.includes("Unique")){
                        usersEditor.field("login").error("Данный логин уже существует")
                    } else {
                        usersEditor.error(response.responseJSON.cause.cause.message);
                    }
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/users/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        delete value.repeatPassword;
                        if (value.passAndSalt == "" || value.passAndSalt == "dummy") {
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
                error: function (response) {
                    if(response.responseJSON.cause.cause.message.includes("Duplicate")){
                        usersEditor.field("login").error("Данный логин уже существует")
                    } else {
                        usersEditor.error(response.responseJSON.cause.cause.message);
                    }
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
            {
                label: 'Имя<sup class="red">*</sup>', name: 'username', type: 'text', compare: function (a, b) {
                    return (a === b);
                }
            },
            {
                label: 'Фамилия', name: 'surname', type: 'text', compare: function (a, b) {
                    return (a === b);
                }
            },
            {
                label: 'Отчество', name: 'patronym', type: 'text', compare: function (a, b) {
                    return (a === b);
                }
            },
            {
                label: 'Логин<sup class="red">*</sup>', name: 'login', type: 'text', compare: function (a, b) {
                    return (a === b);
                }
            },
            {
                label: 'Пароль', name: 'passAndSalt', type: 'password', data: function () {
                    return "dummy";
                }, compare: function (a, b) {
                    return (a === b);
                }
            },
            {
                label: 'Повторите пароль', name: 'repeatPassword', type: 'password', data: function () {
                    return "dummy";
                }
            },
            {label: 'E-Mail', name: 'email', type: 'text'},
        ]
    });

    usersEditor.on('preSubmit', function (e, o, action) {
        if (action !== 'remove') {
            var username = this.field('username');
            var login = this.field('login');
            var passAndSalt = this.field('passAndSalt');
            var repeatPassword = this.field('repeatPassword');
            // var userRole = this.field( 'userRole' );

            // Only validate user input values - different values indicate that
            // the end user has not entered a value
            if (!username.val()) {
                username.error("Имя пользователя должно быть указано")
            }
            if (!login.val()) {
                login.error("Логин должен быть указан")
            }
            if (action !== 'edit' ) {
                if((!passAndSalt.val() || passAndSalt.val() === "dummy")){
                    passAndSalt.error("Пароль должен быть указан")
                } else if(passAndSalt.val()!=repeatPassword.val()){
                    repeatPassword.error("Пароли не совпадают");
                }

            } else if(action==='edit'){
                if(!(passAndSalt.val() === "dummy") && passAndSalt.val()!=repeatPassword.val()){
                    repeatPassword.error("Пароли не совпадают");
                }
            }
            // if(!userRole.val()){
            //     userRole.error("Роль должна быть указана")
            // }
            // ... additional validation rules

            // If any error was reported, cancel the submission so it can be corrected
            if (this.inError()) {
                return false;
            }
        }
    });


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
                    text: "Удалить",
                    action: function (e, dt, node, config) {

                        usersEditor
                            .title('Удалить пользователя')
                            .buttons('Удалить')
                            .message('Вы уверены, что хотите удалить пользователя?')
                            .remove(usersDataTable.rows('.selected', {select: true})
                                // , 'Удалить', {
                                // "label": "Удалить",
                                // "fn": function () {
                                //     this.submit();
                                // }
                                // }, false
                            );
                        dt.button(2).disable();
                    },
                    enabled: false
                },
                {
                    text: "Восстановить пароль",
                    action: function (e, dt, node, config) {
                        resendEditor
                            .edit(usersDataTable.rows('.selected', {select: true}),
                                'Восстановить пароль',
                                {
                                    "label": "Восстановить",
                                    "fn": function () {
                                        this.submit();
                                    }
                                }, true
                            );
                    }
                    ,
                    enabled: false
                }
            ],
            "paging": 10,
            "columns": [
                {"name": "id", "data": "id", "title": "id", visible: false},
                {
                    "name": "username", "data": "username", "title": "ФИО", render: function (data, type, full) {
                        return `${(full.surname) ? full.surname : ''} ${data} ${(full.patronym) ? full.patronym : ""}`;
                    }
                },
                {"name": "login", "data": "login", "title": "Логин"},
                {
                    "name": "passAndSalt", "data": "passAndSalt", defaultContent: "dummy", mRender: function () {
                        return "dummy";
                    }, def: "dummy",
                    "title": "passAndSalt", visible: false
                },
                {"name": "email", "data": "email", "title": "E-Mail"},
            ]
        }
    );

    usersDataTable.on('select', function (e, dt, type, indexes) {
        let userId = usersDataTable.row(indexes[0]).data().id;
        console.log(currentUser.id == userId);
        if (currentUser.id == userId) {
            dt.button(2).disable();
        } else {
            dt.button(2).enable();
        }
        dt.button(3).enable();
    });

    usersDataTable.on('deselect', function () {
        usersDataTable.button(2).disable();
        usersDataTable.button(3).disable();
    })
});
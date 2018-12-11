

$(document).ready(function () {

    let uploadModes = {
        file: "driver",
        license: "driverLicense",
        passport: "driverPassport"
    };
    let uploadMode = uploadModes.file;

    let selectedDriverId = 0;

    $("#uploadDriverDocument").click(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();


        // Get form
        var form = $('#driverDocumentUploadForm')[0];

        // Create an FormData object
        var data = new FormData(form);

        // If you want to add an extra field for the FormData
        // data.append("CustomField", "This is some extra data, testing");



        // disabled the submit button
        $("#uploadDriverDocument").prop("disabled", true);

        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: `/upload/${uploadMode}/${selectedDriverId}`,
            data: data,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {

                $("#uploadDriverDocument").prop("disabled", false);
                $("#driverDocumentUploadModal").modal('hide');
                document.getElementById("driverDocumentUploadForm").reset();
                $("#driverDocumentUploadError").text("")
            },
            error: function (e) {
                console.log("ERROR : ", e);
                if(e.responseJSON.message.includes("Maximum upload size exceeded")){
                    $("#driverDocumentUploadError").text("Ошибка: файл слишком большой")
                } else{
                    $("#driverDocumentUploadError").text(e.responseJSON.message);
                }
                $("#uploadDriverDocument").prop("disabled", false);
            }
        });

    });

    let driverEditor = new $.fn.dataTable.Editor({
        table: '#driverDataTable',
        template: '#driversForm',
        idSrc: 'id',
        ajax: {
            create: {
                type: 'POST',
                contentType: 'application/json',
                url: 'api/drivers',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        value.originator = currentCompanyId;
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    driverDataTable.draw();
                    driverEditor.close();
                    // alert(response.responseText);
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            edit: {
                contentType: 'application/json',
                type: 'PATCH',
                url: 'api/drivers/_id_',
                data: function (d) {
                    let newdata;
                    $.each(d.data, function (key, value) {
                        newdata = JSON.stringify(value);
                    });
                    return newdata;
                },
                success: function (response) {
                    driverDataTable.draw();
                    driverEditor.close();
                },
                error: function (jqXHR, exception) {
                    alert(response.responseText);
                }
            },
            remove: {
                type: 'DELETE',
                contentType: 'application/json',
                url: 'api/drivers/_id_',
                data: function (d) {
                    return '';
                }
            }
        },
        fields: [
            {
                label: 'ФИО',
                name: 'name',
                type: 'text'
            },
            {
                label: 'Телефон',
                name: 'phone',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'Паспорт',
                name: 'passportNumber',
                type: 'text',
                attr:{maxlength:20}
            },
            {
                label: 'Водительское удостверение',
                name: 'licenseNumber',
                type: 'text',
                attr:{maxlength:20}
            },
            {
                label: 'Рейтинг',
                name: 'rating',
                type: 'mask',
                mask: '##',
                maskOptions:{
                    onKeyPress: function(cep, event, currentField, options){
                        let min=1;
                        let max=10
                        let value = currentField.val();
                        if(parseInt(value) < min || isNaN(parseInt(value))){
                            currentField.val(min);
                        } else if(parseInt(value) > max){
                            currentField.val(max);
                        } else {
                            currentField.val(value);
                        }

                    }
                }


            },
            {
                label: 'Наличие мобильного приложения', name: 'hasMobileApp', type: "radio", options: [{ label:"Есть", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Телефон для приложения',
                name: 'mobileAppNumber',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'Возможность отслеживания по телефону', name: 'isTracked', type: "radio", options: [{ label:"Есть", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Телефон для отслеживания',
                name: 'trackingNumber',
                type: 'mask',
                mask: '+7 (000)-000-00-00'
            },
            {
                label: 'В штате', name: 'isHired', type: "radio", options: [{ label:"Да", value:true}, {label:"Нет",value:false}]
            },
            {
                label: 'Способ оплаты',
                name: 'paymentType',
                type: 'selectize',
                options: paymentTypeOptions,
                opts: {
                    searchField: "label", create: false
                }
            },
            {
                label:'Комментарий',
                name: 'comment',
                type:'textarea'
            }
        ]

    });


    var driverDataTable = $("#driverDataTable").DataTable({
            processing: true,
            serverSide: true,
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: "dataTables/driversForUser", // json datasource
                type: "post"  // method  , by default get
            },
            dom: 'Btp',
            language: {
                url: '/localization/dataTablesRus.json'
            },
            select: {
                style: 'single'
            },
            "buttons": [
                {
                    extend: "create",
                    editor: driverEditor
                },
                {
                    extend: "edit",
                    editor: driverEditor
                },
                {
                    extend: "remove",
                    editor: driverEditor
                },
                {
                    extend: 'selectedSingle',
                    text: '<i class="fa fa-file"></i> Прикрепить документ',
                    action: function (e, dt, node, config) {
                        $("#driverDocumentUploadError").text("");
                        $("#driverDocumentUploadForm input[name='fileName']").first().closest(".row").show();
                        uploadMode = uploadModes.file;
                        document.getElementById("driverDocumentUploadForm").reset();
                        selectedDriverId = dt.rows( { selected: true } ).data()[0].id;
                        $("#driverDocumentUploadModal").modal();
                    }
                },
                {
                    extend: 'selectedSingle',
                    text: '<i class="fa fa-id-card"></i> Прикрепить паспорт',
                    action: function (e, dt, node, config) {
                        uploadMode = uploadModes.passport;
                        $("#driverDocumentUploadError").text("");
                        $("#driverDocumentUploadForm input[name='fileName']").first().closest(".row").hide();
                        document.getElementById("driverDocumentUploadForm").reset();
                        selectedDriverId = dt.rows( { selected: true } ).data()[0].id;
                        $("#driverDocumentUploadModal").modal();
                    }
                },
                {
                    extend: 'selectedSingle',
                    text: '<i class="fa fa-id-card-o"></i> Прикрепить удостверение',
                    action: function (e, dt, node, config) {
                        uploadMode = uploadModes.license;
                        $("#driverDocumentUploadError").text("");
                        $("#driverDocumentUploadForm input[name='fileName']").first().closest(".row").hide();
                        document.getElementById("driverDocumentUploadForm").reset();
                        selectedDriverId = dt.rows( { selected: true } ).data()[0].id;
                        $("#driverDocumentUploadModal").modal();
                    }
                },
                {
                    extend: 'selectedSingle',
                    text: '<i class="fa fa-file-photo-o"></i> Прикрепить фотографию',
                    action: function (e, dt, node, config) {
                        $modal.modal('show');
                        selectedDriverId = dt.rows( { selected: true } ).data()[0].id;
                        console.log(dt.rows( { selected: true } ).data()[0].id);
                    }
                },
                {
                    text: 'Показать все',
                    action: function () {
                        driverDataTable.page.len(-1).draw();
                    }
                },{
                    extend: "excelHtml5",
                    text: "<i class='fa fa-file-excel-o'></i> Экспорт",
                    title: `Водители ${new Date().getDate()}.${(new Date().getMonth()+1)}.${new Date().getFullYear()}`
                }
            ],
            "paging": 10,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "name", "data": "name", "targets": 1,
                    render: function (data, type, full) {
                        return `<a target="_blank" href='info/drivers/${full.id}'>${data}</a>`;
                    }},
                {"name": "phone", "data": "phone", searchable: false, orderable: false, "targets": 2},
                {"name": "passportNumber", "data": "passportNumber", searchable: false, orderable: false, "targets": 3},
                {"name": "licenseNumber", "data": "licenseNumber", searchable: false, orderable: false, "targets": 4},
                {"name": "rating", "data": "rating", searchable: false, "targets": 5},
                {
                    "name": "hasMobileApp",
                    "data": "hasMobileApp",
                    "targets": 6,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Есть" : "Нет"
                    }
                },
                {"name": "mobileAppNumber", "data": "mobileAppNumber", searchable: false, orderable: false, "targets": 7},
                {
                    "name": "isTracked",
                    "data": "isTracked",
                    "targets": 8,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Да" : "Нет"
                    }
                },
                {"name": "trackingNumber", "data": "trackingNumber", searchable: false, orderable: false, "targets": 9},
                {
                    "name": "isHired",
                    "data": "isHired",
                    "targets": 10,
                    searchable: false,
                    orderable: false,
                    render: function (data) {
                        return (data) ? "Да" : "Нет"
                    }
                },
                {
                    "name": "paymentType",
                    "data": "paymentType",
                    "targets": 11,
                    searchable: false,
                    orderable: false
                },
                {
                    name: 'comment',
                    data: "comment",
                    targets:12,
                    searchable: false,
                    orderable: false
                }
            ]
        }
    );



    var input = document.getElementById('driverPhotoInput');
    var $modal = $('#driverPhotoUpload');
    var image = document.getElementById('driverPhoto');
    var cropper;
    $('[data-toggle="tooltip"]').tooltip();
    input.addEventListener('change', function (e) {
        if (cropper!=null) destroyCropper();
        var files = e.target.files;
        var done = function (url) {
            input.value = '';
            image.src = url;
            createCropper();
        };
        var reader;
        var file;
        if (files && files.length > 0) {
            file = files[0];
            if (URL) {
                done(URL.createObjectURL(file));
            } else if (FileReader) {
                reader = new FileReader();
                reader.onload = function (e) {
                    done(reader.result);
                };
                reader.readAsDataURL(file);
            }
        }
    });
    function createCropper(){
        cropper = new Cropper(image, {
            aspectRatio: 1,
            viewMode: 3,
        });
    };

    function destroyCropper(){
        cropper.destroy();
        cropper = null;
    }
    document.getElementById('cropDriverPhoto').addEventListener('click', function () {
        var canvas;
        if (cropper) {
            canvas = cropper.getCroppedCanvas({
                width: 320,
                height: 320,
            });
            canvas.toBlob(function (blob) {
                var formData = new FormData();
                formData.append('file', blob, 'avatar.jpg');
                $.ajax(`/upload/driverPhoto/${selectedDriverId}`, {
                    method: 'POST',
                    data: formData,
                    processData: false,
                    contentType: false,
                    xhr: function () {
                        var xhr = new XMLHttpRequest();
                        return xhr;
                    },
                    success: function () {
                        $modal.modal('hide');
                        destroyCropper();
                        image.src = '';
                    },
                    error: function () {
                    },
                    complete: function () {
                    },
                });
            });
        }
    });
    driverEditor.on('open', function () {
        $('#driversForm').closest(".DTE_Action_Edit").parent().parent().css('max-width', '820px');
        $('#driversForm').closest(".DTE_Action_Create").parent().parent().css('max-width', '820px');
    });


    driverEditor.on('close', function () {
        $('#driversForm').closest(".DTE_Action_Edit").parent().parent().css('max-width', '500px');
        $('#driversForm').closest(".DTE_Action_Create").parent().parent().css('max-width', '500px');
    });
});
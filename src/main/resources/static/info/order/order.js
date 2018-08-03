$(document).ready(function () {
    $("#orderHistoryTable").DataTable({
            processing: true,
            serverSide: true,
            aaSortingFixed: [[0, 'desc']],
            searchDelay: 800,
            ajax: {
                contentType: 'application/json',
                processing: true,
                data: function (d) {
                    return JSON.stringify(d);
                },
                url: `/dataTables/orderHistory/${currentOrderId}`, // json datasource
                type: "post"  // method  , by default get
            },
            dom: 'Brtip',
            language: {
                url: '/localization/dataTablesRus.json'
            },
            buttons:[],
            "paging": 20,
            "columnDefs": [
                {"name": "id", "data": "id", "targets": 0, visible: false},
                {"name": "orderStatus", "data": "orderStatus", "targets": 1, searchable: false, orderable: false},
                {"name": "actionCompany", "data": "actionCompany", "targets": 2,searchable:false, orderable:false},
                {"name": "actionUser", "data": "actionUser", "targets": 3,searchable:false, orderable:false},
                {"name": "action", "data": "action", "targets": 4,searchable:false, orderable:false},
                {"name": "dispatcherPrice", "data": "dispatcherPrice", "targets": 5,searchable:false, orderable:false},
                {"name": "companyPrice", "data": "companyPrice", "targets": 6,searchable:false, orderable:false},
                {"name": "date", "data": "date", "targets": 7,searchable:false, orderable:false, defaultContent:""}
            ]
        }
    );

    $('#printButton').on('click',function(){


        $.get(`/data/orders/${currentOrderId}`)
            .success(function (order) {
                console.log(order);
                $.get(`/data/companies/${order.originator}`)
                    .success(function (dispatcherCompany) {
                        console.log(dispatcherCompany);
                        makePdf(order,dispatcherCompany);
                    })
            });
    })
    function makePdf(order, dispatcherCompany) {
        var dd = {
            content: [
                {text: `Поручение-заявка на предоставление ТС №${order.transport.number} от ${order.dispatchDate} к договору 323`, style: 'bold'},
                {
                    style: 'tableExample',
                    table: {
                        widths: [44,44,63,80,88,63,80],
                        body: [
                            [{text:'ИСПОЛНИТЕЛЬ', style: "bold", colSpan:4},{},{},{}, {text:'ЗАКАЗЧИК',style:'bold',colSpan:3},{},{}],
                            [{text:`${order.company.name}`, colSpan:4, style: 'bold'},{},{},{}, {text:`${dispatcherCompany.name}`,colSpan:3, style: 'bold'},{},{}],
                            [{text:'КОНТАКТ \nИСПОЛНИТЕЛЯ', style: 'bold',rowSpan: 2,colSpan:2},{},{text:`Где взять основной контакт компании?`,style:"veryFuckingSmall"},`Где взять номер компании?`,{text:'КОНТАКТ \nЗАКАЗЧИКА', style: 'bold',rowSpan: 2},{text:'Где взять основной контакт компании?',style:'veryFuckingSmall'},'Где взять номер компании?'],
                            [{},{},{colSpan:2,text:`${order.company.email}`},{},{},{colSpan:2,text:`${dispatcherCompany.email}`},{}],
                            [{text:`Адрес: ${order.company.point.address}`,colSpan:4},{},{},{},{text:`Адрес: ${dispatcherCompany.point.address}`,colSpan:3},{},{}],
                            [{text:`Почтовый адрес: ${order.company.point.address}`,colSpan:4},{},{},{},{text:``,colSpan:3},{},{}],
                            [{text:`ИНН/КПП: ${order.company.inn}/`,colSpan:4},{},{},{},{text:`ИНН/КПП: ${dispatcherCompany.inn}/`,colSpan:3},{},{}],
                            [{text:`Р/с: Где взять рассчетный счет?`,colSpan:4},{},{},{},{text:`Р/с: Где взять рассчетный счет?`,colSpan:3},{},{}],
                            [{text:`Где взять адрес банка?`,colSpan:4},{},{},{},{text:`Где взять всё это?`,colSpan:3},{},{}],
                            [{text:`К/с: где взять корреспондентский счет?`,colSpan:4},{},{},{},{text:`К/с:`,colSpan:3},{},{}],
                            [{text:`Тел.: Телефон банка? Компании? Контакта?`,colSpan:4},{},{},{},{text:`Тел.: ???`,colSpan:3},{},{}],
                            [{text:"ГРУЗ",style:"bold",colSpan:2},{},{text:`${order.cargo.join(", ")};`,colSpan:5,style: "bold"},{},{},{},{}],
                            [{text:"ТРАНСПОРТ",style:"bold",colSpan:2},{},{text:`Тип кузова: ${order.transport.bodyType}; способ загрузки/разгрузки: ${order.transport.loadingType.join(", ")}; объем: ${order.transport.volume}м`,colSpan:5, style:"bold"},{},{},{},{}],
                            [{text:"УСЛОВИЯ",style:"bold",colSpan:2},{},{text:`ОБЯЗАТЕЛЬНОЕ НАЛИЧИЕ У ВОДИТЕЛЯ: ${order.requirements.join(", ")}`,style:"bold",colSpan:5},{},{},{},{}],
                            [{text:"ШТРАФЫ",style:"bold",colSpan:2},{},{text:'Неполадка ТС - штраф в размере нанесенных убытков, или 20: от ставки. Опоздание ТС: до 2х часов - штраф в размере понес. убытков, или 10% от ставки; свыше 2х часов - штраф в размере понес. убытков или 20% от ставки.',colSpan:5},{},{},{},{}],

                        ]
                    }
                },
                {
                    style: 'tableExample',
                    table: {
                        widths: [50,75,100, 265],
                        body: [
                            [{text:"Сведения о водителе и машине", style: "header",colSpan:4},{},{},{}],
                            [{text:`${order.driver.name}`, colSpan:4,style:"center"},{},{},{}],
                            [`${order.transport.type}`,`${order.transport.number}`,'Паспортные данные', `${order.driver.passportNumber}`],
                            ['','','Телефон',`${order.driver.phone}`]
                        ]
                    }
                },
                {
                    style: 'tableExample',
                    table: {
                        widths: [120,140,63,167],
                        body: [
                            [{text:"Стоимость перевозки",style: "header",colSpan:4},{},{},{}],
                            ['Сумма',`${order.proposedPrice}₽`, {text:`Без НДС`,colSpan:2},{}],
                            ['Сроки и способ оплаты', {text:`Безналичная ${order.paymentDate} банковских дней с момента получения ${(order.paymentType=="По оригиналам")? "оригиналов":"копий"} документов`,colSpan:3},{},{}],
                        ]
                    }
                },
                "При въезде ТС на территорию склада Грузоотправителя, Водитель обязан предъявить представителю заказчика/охране оригинал СТС/ПТС на тягач и прицеп, а так же предоставить подвижной состав на осмотр, в том числе и для проверки VIN номеров автомобиля и прицепа (Указать местонахождение VIN номера на кузове ТС). Для регистрации и получения груза на складе Грузоотправителя, водитель обязан предоставить действующие и действительные паспорт и в/удостверение. Кузов ТС (прицеп, п/прицеп) должны иметь исправные запорные пломбировочные устройства, исключающие доступ к перевозимому грузу. \n\n\n\n",


                {
                    style: 'tableExample',
                    table: {
                        headerRows: 1,
                        widths: [260,260],
                        body: [
                            ['Исполнитель ______________________', 'Заказчик _______________________'],
                        ]
                    },
                    layout: 'noBorders'
                },
                {text: '', fontSize: 14, bold: true, pageBreak: 'before', margin: [0, 0, 0, 8]},
                {margin:[0,10,0,10],text:`1. Водитель-экспедитор несет ответственность за единицу груза- КОРОБ, в случае невозможности пересчета по коробам, водитель обязан выполнить следующие действия:
-во всех экземплярах ТРН, в графе "Отметки о составленных актах", сделать и заверить сотрудником склада запись: "Склад не предоставил возможность пересчета мест по коробам, принято "Х " паллет(а)", "Сотрудниками склада, в возможности пересчета мест по коробам, отказано, груз принят в количестве "Х " паллет(а)". - При наличии данной отметки и своевременном сообщении о данном происшествии менеджеру Заказчика, обязанность приемки по коробам с Водителя снимается, груз принимается по количеству паллет за целой, ненарушенной упаковкой. В случае невыполнения указанных ранее действий, Претензии о возмещении стоимости недогруженной, недостающей и т.п. продукции в полном объеме будут компенсироваться за счет Перевозчика в независимости от заключения комиссии при составлении Акта.\n\n
2. Перевозчик обязуется по окончании перевозки предоставить Заказчику в следующие сроки :
- по 2 (два) оригинала Транспортных накладных (ТРН) на каждый заказ (по межскладским переброскам - 1 (один) оригинал);
- по 1 (одному) оригиналу Товарных накладных (ТН) на каждый заказ;
- по 1 оригиналу Приложения к ТН на каждый заказ ( в случаях доставки получателям "Атак", "Ашан", "Зельгрос", "Метро", "Реал"); - 1 акт АПС (при доставке получателю "Сладкая Жизнь", "Перекресток");
- 1 подтверждение приемки (при доставке получателю "Метро");
- Акт о расхождениях (в случаях обнаружения брака/недостачи, и т.д.) и обяснительную водителя в случае составоения такого акта;
- Акт возврата продукции\протокол разногласий в случаях осуществелния такого возврата;
- данную Заявку на перевозку, подтвержденную синей печатью Перевозчика.
Все выше указанные документы обязаны быть заверены "живыми" круглыми печатями с подписью и расшифровкой грузоотправителя/грузополучателя/водителя.
\n
3. Документы предоставляются Перевозчиком с момента окончания маршрута перевозки:
- по внутригородским перевозкам - не позднее 3 календарных дней ;
- по межгородским перевозкам (до 1 500 км) - не позднее 5 календарных дней;
- по межгородским перевозкам (свыше 1 500 км) - не позднее 10 календарных дней.
\n
4. Допустимое опоздание прибытия ТС на склад не более 14 минут (от времени согласованного сторонами в Заявке).\n
5. Нормативное время ожидания погрузки ТС у грузоотправителя составляет 3 часа, с момента подачи ТС. Нормативное время ожидания разгрузки ТС у грузополучателя составляет 3 часа, с момента подачи ТС.\n
6. Отметки о простое должны содержаться в ТРН (пропечатаны, а не написаны от руки), и\или Акте о приеме-передаче товара на хранение, и должны быть заверены подписями и печатями сотрудников склада. Все исправления должны быть заверены печатью и подписью склада. Должно быть зафиксировано 3 отметки (время и дата!): прибытие на погрузку\разгрузку, начало загрузки\разгрузки, убытие с загрузки\разгрузки!!\n
7. Штраф за восстановление док-ов Заказчиком равен сумме понесенных затрат, или 1 000 (одна тысяча) рублей за комплект документ по каждому месту разгрузки.\n
`, fontSize:9},
                {text:`Все остановки в пути должны быть только на специализированных и охраняемых стоянках. Перед выездом, после каждой остановки, водитель обязан проверить: 1) наличие и целостность пломбы 2) техническое состояние автопоезда 3) соблюдение требуемого температурного режима в кузове ТС (в случае перевозки груза в температурном режиме). Только после того, как водитель убедился, что все в порядке, он может трогаться с территории охраняемой стоянки.
Любая переадресация груза Нестле во время перевозки запрещена. В случае попытки переадресации, водитель обязан связаться с СБ Грузоотправителя и действовать по указанию СБ.\n\n\n\n\n\n\n`,fontSize:9,bold:true},
                {
                    style: 'tableExample',
                    table: {
                        headerRows: 1,
                        widths: [260,260],
                        body: [
                            ['Исполнитель ______________________', 'Заказчик _______________________'],
                        ]
                    },
                    layout: 'noBorders'
                },
            ],
            styles: {
                veryFuckingSmall:{
                    fontSize:7
                },
                bold:{
                    bold: true
                },

                header: {
                    alignment: "center",
                    bold: true
                },
                center:{
                    alignment: "center"
                },
                subheader: {
                    fontSize: 16,
                    bold: true,
                    margin: [0, 10, 0, 5]
                },
                tableExample: {
                    margin: [0, 10, 0, 10]
                },
                tableHeader: {
                    bold: true,
                    fontSize: 13,
                    color: 'black'
                }
            },
            defaultStyle: {
                alignment: 'justify',
                fontSize:10
            }

        };
        pdfMake.createPdf(dd).open();
    }

});
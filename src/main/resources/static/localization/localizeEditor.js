$('document').ready(function () {


$.extend(true, $.fn.dataTable.Editor.defaults, {
    "i18n": {
        "create": {
            "button": "<i style='color: green' class='fa fa-plus'></i> Создать",
            "title": "Добавить новую запись",
            "submit": "Создать"
        },
        "edit": {
            "button": "<i class='fa fa-edit'></i> Изменить",
            "title": "Изменить запись",
            "submit": "Обновить"
        },
        "remove": {
            "button": "<i class='fa fa-minus'></i> Удалить",
            "title": "Удалить",
            "submit": "Удалить",
            "confirm": {
                "_": "Вы уверены, что хотите удалить %d записей?",
                "1": "Вы уверены, что хотите удалить запись?"
            }
        },
        "error": {
            "system": "Возникла системная ошибка"
        },
        "multi": {
            "title": "Множество значений",
            "info": "The selected items contain different values for this input. To edit and set all items for this input to the same value, click or tap here, otherwise they will retain their individual values.",
            "restore": "Обратить изменения"
        },
        "datetime": {
            "previous": "Предыдущая",
            "next": "Следующая",
            "months": ["Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"],
            "weekdays": ["Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб"],
            "amPm": ["am", "pm"],
            "unknown": "-"
        }
    }
});
});
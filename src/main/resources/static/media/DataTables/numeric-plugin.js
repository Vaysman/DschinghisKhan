(function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD
        define(['jquery', 'datatables', 'datatables-editor'], factory);
    }
    else if (typeof exports === 'object') {
        // Node / CommonJS
        module.exports = function ($, dt) {
            if (!$) { $ = require('jquery'); }
            factory($, dt || $.fn.dataTable || require('datatables'));
        };
    }
    else if (jQuery) {
        // Browser standard
        factory(jQuery, jQuery.fn.dataTable);
    }
}(function ($, DataTable) {
    'use strict';


    if (!DataTable.ext.editorFields) {
        DataTable.ext.editorFields = {};
    }

    var _fieldTypes = DataTable.Editor ?
        DataTable.Editor.fieldTypes :
        DataTable.ext.editorFields;


    _fieldTypes.numeric = {
        create: function (conf) {
            conf._input = $('<input/>').attr($.extend({
                id: DataTable.Editor.safeId(conf.id),
                type: 'text'
            }, conf.attr || {}));
            conf._input.on("focus", function () { $(this).select(); });
            conf._input.on("keydown", function (evtObj) {
                return keydownHandler(evtObj);
            });
            conf._input.on("paste drop", function (evtObj) {
                // event handler for drop and paste
                // strip the number of all characters that are not part of the actual number
                let paste = evtObj.clipboardData || window.clipboardData || evtObj.originalEvent.clipboardData || event.dataTransfer;
                let str = paste.getData("text").replace(/[^0-9\.-]+/g, "");
                var number = Number(str);
                this.value = isNaN(number) ? 0 : number;
                evtObj.preventDefault();
            })
            return conf._input[0];
        },

        get: function (conf) {
            // user is allowed to use commas to make the number easier to read so
            // so need to remove them here.
            var raw = conf._input.val();
            var val = parseFloat(raw.replace(/[^0-9\.-]+/g, ""));
            if (isNaN(val) || val === "") return 0;
            else return val;
        },

        set: function (conf, val) {
            conf._input
                .val(val);
        },
        canReturnSubmit: function (opts, node) {
            return true;
        },
        enable: function (conf) {
            conf._input.prop('disabled', false);
        },

        disable: function (conf) {
            conf._input.prop('disabled', true);
        },
        destroy: function (conf) {
            conf._input.off();
        }
    };

}));

function keydownHandler(evtObj) {

    // length greater than 1 assumed to be
    // nonprintable key
    if (evtObj.key.length > 1) {
        // non printable keys
        switch (evtObj.key) {
            // does not work with input event.
            //case "Escape":
            //default behavior is good
            //case "Tab":
            //case "Enter":


            // decimal from keypad.
            case "Decimal":
                if (evtObj.target.value.indexOf(".") > -1) {
                    evtObj.preventDefault();
                    return false;
                }
                break;

        }
    }
    else if (evtObj.ctrlKey && (evtObj.key == "v" || evtObj.key == "V" || evtObj.key == "c" || evtObj.key == "C")) {
        // allow keyboard copy and paste actions
    }
    else {

        if (evtObj.key == ".") {
            if (evtObj.target.value.indexOf(".") > -1) {
                evtObj.preventDefault();
                return false;
            }
        }
        else if ("1234567890".indexOf(evtObj.key) < 0) {
            evtObj.preventDefault();
            return false;
        }
    }
    return true;

}
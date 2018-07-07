package ru.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderRequirements {
    @JsonProperty("Медицинская карта")
    MED_CARD("Медицинская карта"),
    @JsonProperty("Светоотражающий жилет")
    REFLECTION_JACKET("Светоотражающий жилет"),
    @JsonProperty("Ботинки с защитным носком")
    PROTECTIVE_BOOTS("Ботинки с защитным носком"),
    @JsonProperty("GPS")
    GPS("GPS"),
    @JsonProperty("Мобильное приложение")
    PHONE_APP("Мобильное приложение"),
    @JsonProperty("Отслеживание по номеру телефона")
    PHONE_TRACKING("Отслеживание по номеру телефона"),
    @JsonProperty("Боковая погрузка")
    SIDE_LOADING("Боковая погрузка"),
    @JsonProperty("Задняя погрузка")
    BACK_LOADING("Задняя погрузка"),
    //wtf is koniki
    @JsonProperty("Коники")
    KONIKI("Коники"),
    @JsonProperty("Гидробот")
    GIDROBOT("Гидробот");


    private String requirementName;

    OrderRequirements(String requirementName) {
        this.requirementName = requirementName;
    }

    public String[] getRequirements(){
        return new String[]{"Медицинская карта",
                "Светоотражающий жилет",
                "Ботинки с защитным носком",
                "GPS",
                "Мобильное приложение",
                "Отслеживание по номеру телефона",
                "Тип погрузки - боковая",
                "Тип погрузки - задняя",
                "Коники",
                "Гидробот"};
    }
}

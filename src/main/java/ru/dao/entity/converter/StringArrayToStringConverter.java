package ru.dao.entity.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringArrayToStringConverter implements AttributeConverter<List<String>,String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        //Using @ instead of comma, because comma occurs sometimes
        return attribute == null ? null : StringUtils.join(attribute,"@");
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)){
            return new ArrayList<>();
        } else {
            return Arrays.asList(StringUtils.split(dbData,"@"));
        }
    }
}

package ru.dao.entity.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.*;
import java.util.stream.Collectors;

@Converter
public class StringSetToStringConverter implements AttributeConverter<Set<String>,String> {
    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        //Using @ instead of comma, because comma occurs sometimes
        return attribute == null ? null : StringUtils.join(attribute,"@");
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)){
            return new HashSet<>();
        } else {
            return Arrays.stream(StringUtils.split(dbData, "@")).collect(Collectors.toSet());
        }
    }
}

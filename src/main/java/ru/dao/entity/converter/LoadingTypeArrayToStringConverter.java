package ru.dao.entity.converter;

import ru.constant.LoadingType;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadingTypeArrayToStringConverter implements AttributeConverter<Set<LoadingType>,String> {
    @Override
    public String convertToDatabaseColumn(Set<LoadingType> attribute) {
        if(attribute==null ||attribute.isEmpty()){
            return "";
        } else {
            return attribute.stream().map(Enum::name).collect(Collectors.joining(","));
        }

    }

    @Override
    public Set<LoadingType> convertToEntityAttribute(String dbData) {
        if(dbData.isEmpty()){
            return null;
        } else {
            return Arrays.stream(dbData.split(",")).map(LoadingType::valueOf).collect(Collectors.toSet());
        }

    }

}

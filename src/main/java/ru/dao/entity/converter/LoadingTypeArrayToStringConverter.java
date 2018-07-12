package ru.dao.entity.converter;

import ru.constant.LoadingType;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadingTypeArrayToStringConverter implements AttributeConverter<Set<LoadingType>,String> {
    @Override
    public String convertToDatabaseColumn(Set<LoadingType> attribute) {
        return attribute.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<LoadingType> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(",")).map(LoadingType::valueOf).collect(Collectors.toSet());
    }

}

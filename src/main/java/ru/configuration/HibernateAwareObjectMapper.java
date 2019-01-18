package ru.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;


class HibernateAwareObjectMapper extends ObjectMapper {
    public HibernateAwareObjectMapper() {
        Hibernate5Module module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        this.enable(SerializationFeature.INDENT_OUTPUT);
        this.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        this.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        this.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        this.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.registerModule(module);
    }
}

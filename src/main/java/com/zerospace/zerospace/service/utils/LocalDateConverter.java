package com.zerospace.zerospace.service.utils;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return attribute != null ? attribute.format(FORMATTER) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return dbData != null ? LocalDate.parse(dbData, FORMATTER) : null;
    }
}
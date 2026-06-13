package com.aquacode.ctm.rules.infrastructure.persistence;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.postgresql.util.PGobject;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.sql.SQLException;
import java.util.List;

@Configuration
public class JdbcJsonbConfiguration extends AbstractJdbcConfiguration {

    @Override
    @NullMarked
    protected List<?> userConverters() {
        return List.of(
            new JsonbToStringConverter(),
            new StringToJsonbConverter()
        );
    }

    @ReadingConverter
    static class JsonbToStringConverter implements Converter<PGobject, String> {

        @Override
        public String convert(PGobject source) {
            return source.getValue();
        }
    }

    @WritingConverter
    static class StringToJsonbConverter implements Converter<String, PGobject> {

        @Override
        public PGobject convert(@NonNull String source) {
            try {
                var jsonObject = new PGobject();
                jsonObject.setType("jsonb");
                jsonObject.setValue(source.isBlank() ? "{}" : source);
                return jsonObject;
            } catch (SQLException ex) {
                throw new IllegalArgumentException("Failed to convert String to PostgreSQL JSONB", ex);
            }
        }
    }
}
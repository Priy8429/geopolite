package com.priyhotel.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class DateFormatConfig {

    private static final String dateFormat = "dd-MM-yyyy";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
            builder.simpleDateFormat(dateFormat);
            builder.serializers(new LocalDateSerializer(formatter));
            builder.deserializers(new LocalDateDeserializer(formatter));
        };
    }
}

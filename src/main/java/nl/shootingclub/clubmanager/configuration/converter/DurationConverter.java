package nl.shootingclub.clubmanager.configuration.converter;

import jakarta.persistence.*;
import java.time.Duration;
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration attribute) {
        return (attribute == null ? null : attribute.toNanos());
    }

    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        return (dbData == null ? null : Duration.ofNanos(dbData));
    }
}
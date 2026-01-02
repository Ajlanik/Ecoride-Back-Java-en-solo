package converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.util.List;
import java.util.ArrayList;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    
    private static final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) return null;
        return jsonb.toJson(attribute); // Java convertit la Liste en String JSON
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) return new ArrayList<>();
        // Java convertit la String JSON en Liste
        return jsonb.fromJson(dbData, new ArrayList<String>(){}.getClass().getGenericSuperclass());
    }
}
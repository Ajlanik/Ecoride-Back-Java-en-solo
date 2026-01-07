package converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTWriter;
import org.postgresql.util.PGobject;

@Converter(autoApply = true)
// Notez le changement ici : <Geometry, Object> au lieu de <Geometry, String>
public class GeometryConverter implements AttributeConverter<Geometry, Object> {

    // On garde le writer en WKT car PostGIS comprend très bien l'insertion en texte
    private final WKTWriter wktWriter = new WKTWriter();

    @Override
    public Object convertToDatabaseColumn(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        // On renvoie une String (WKT), qui est un Object valide.
        return wktWriter.write(geometry);
    }

    @Override
    public Geometry convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }

        String hexString = null;

        // 1. Extraction de la chaîne hexadécimale depuis le PGobject
        if (dbData instanceof PGobject) {
            hexString = ((PGobject) dbData).getValue();
        } else if (dbData instanceof String) {
            hexString = (String) dbData;
        }

        if (hexString == null || hexString.isEmpty()) {
            return null;
        }

        // 2. Parsing du format WKB (Hexadécimal vers Géométrie)
        try {
            // WKBReader convertit le binaire (ou hex) en objet Geometry
            byte[] bytes = WKBReader.hexToBytes(hexString);
            return new WKBReader().read(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la conversion WKB vers Geometry", e);
        }
    }
}
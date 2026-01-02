package converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

/**
 * Convertit automatiquement les objets JTS Geometry (Java) 
 * en format WKT (Texte) pour que PostGIS les comprenne.
 */
@Converter(autoApply = true)
public class GeometryConverter implements AttributeConverter<Geometry, String> {

    @Override
    public String convertToDatabaseColumn(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        // Transforme l'objet Java en texte : "LINESTRING(x y, x y...)"
        return new WKTWriter().write(geometry);
    }

    @Override
    public Geometry convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            // Transforme le texte de la DB en objet Java
            return new WKTReader().read(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la lecture de la géométrie WKT", e);
        }
    }
}
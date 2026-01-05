/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ajlan
 */
package be.isl.ecoridebackajlani.service; // Vérifie que le package est bon

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Path("files")
public class FileResource {

    // TON DOSSIER LOCAL
    private static final String UPLOAD_DIR = "D:/EcorideFolder/ecoride_uploads/";

    @POST
    @Path("upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM) // On reçoit le flux binaire direct
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(InputStream uploadedInputStream) {
        try {
            // 1. Créer le dossier s'il n'existe pas
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. Générer un nom unique (UUID) + extension .jpg par défaut pour simplifier
            String fileName = UUID.randomUUID().toString() + ".jpg";
            File file = new File(dir, fileName);

            // 3. Écrire le fichier sur le disque D:
            Files.copy(uploadedInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 4. Construire l'URL publique
            // Attention: Change "Ecoride-Back-Java-en-solo" si le nom de ton contexte web est différent
            String fileUrl = "http://localhost:8080/EcorideBackAjlani/images/" + fileName;

            return Response.ok("{\"url\": \"" + fileUrl + "\"}").build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}
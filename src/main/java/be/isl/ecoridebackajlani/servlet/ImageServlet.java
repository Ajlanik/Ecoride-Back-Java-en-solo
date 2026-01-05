/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author ajlan
 */
package be.isl.ecoridebackajlani.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// Cette URL intercepte tout ce qui commence par /images/
@WebServlet(name = "ImageServlet", urlPatterns = {"/images/*"})
public class ImageServlet extends HttpServlet {

    // LE MÊME DOSSIER QUE DANS FILE RESOURCE
    private static final String UPLOAD_DIR = "D:/EcorideFolder/ecoride_uploads/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Récupérer le nom du fichier (ex: /mon-image.jpg)
        String filename = request.getPathInfo();
        
        if (filename == null || filename.equals("/")) {
            response.sendError(404);
            return;
        }
        
        // Enlever le slash initial
        File file = new File(UPLOAD_DIR, filename.substring(1));

        if (!file.exists()) {
            response.sendError(404); // Fichier introuvable
            return;
        }

        // Deviner le type (image/jpeg, image/png, etc.)
        String contentType = getServletContext().getMimeType(file.getName());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");

        // Envoyer le fichier au navigateur
        Files.copy(file.toPath(), response.getOutputStream());
    }
}
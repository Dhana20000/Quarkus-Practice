package com.dhana;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/git")
public class GreetingResource {

    private static final Logger LOGGER = Logger.getLogger(GreetingResource.class.getName());

    @Inject
    EC2Service ec2Service;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOGGER.log(Level.INFO, "hello endpoint called");
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/filedownload")
    @Produces("application/octet-stream")
    public Response downloadFile(@QueryParam("folder") String folder, @QueryParam("file") String file) {
        LOGGER.log(Level.INFO, "downloadFile endpoint called with folder: {0} and file: {1}", new Object[]{folder, file});

        if (folder == null || folder.isEmpty() || file == null || file.isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid parameters: folder and file parameters must not be null or empty");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Folder and file parameters must not be null or empty")
                    .build();
        }

        try {
            String remoteFilePath = folder + "/" + file;
            LOGGER.log(Level.INFO,"Remote file path :: "+ remoteFilePath);
            String localDirectoryPath = "C:\\Users\\baddela.dhananjaneya\\Desktop\\files from ec2\\";
            String localFilePath = localDirectoryPath + file;
            LOGGER.log(Level.INFO,"Local Directory path :: "+ localFilePath);

            // Ensure the local directory exists
            File localDirectory = new File(localDirectoryPath);
            if (!localDirectory.exists()) {
                LOGGER.log(Level.INFO, "Creating local directory: {0}", localDirectoryPath);
                localDirectory.mkdirs();
            }

            LOGGER.log(Level.INFO, "Starting file download from {0} to {1}", new Object[]{remoteFilePath, localFilePath});
            ec2Service.downloadFile(remoteFilePath, localFilePath);
            LOGGER.log(Level.INFO, "File downloaded successfully");

            File localFile = new File(localFilePath);
            if (!localFile.exists()) {
                LOGGER.log(Level.WARNING, "File not found after download: {0}", localFilePath);
                return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
            }

            return Response.ok(localFile)
                    .header("Content-Disposition", "attachment; filename=\"" + file + "\"")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error downloading file", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }
}

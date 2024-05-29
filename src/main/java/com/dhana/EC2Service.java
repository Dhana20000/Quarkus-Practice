package com.dhana;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.enterprise.context.ApplicationScoped;


import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class EC2Service {

    private static final Logger LOGGER = Logger.getLogger(EC2Service.class.getName());
    private static final String EC2_HOST = "54.224.218.148"; // You can also read these from application.properties
    private static final String EC2_USER = "ec2-user";
    private static final String PEM_FILE_PATH = "C:\\Users\\baddela.dhananjaneya\\Desktop\\Keys\\KeyPairAWS.pem";

    public void downloadFile(String remoteFilePath, String localFilePath) throws Exception {
        LOGGER.log(Level.INFO, "Downloading file from EC2: {0} to local path: {1}", new Object[]{remoteFilePath, localFilePath});

        JSch jsch = new JSch();
        jsch.addIdentity(PEM_FILE_PATH);

        Session session = jsch.getSession(EC2_USER, EC2_HOST, 22);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        LOGGER.log(Level.INFO, "Connected to EC2 instance");

        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        LOGGER.log(Level.INFO, "SFTP channel opened");

        try (OutputStream outputStream = new FileOutputStream(localFilePath)) {
            channelSftp.get(remoteFilePath, outputStream);
            LOGGER.log(Level.INFO, "File transfer completed");
        } finally {
            channelSftp.disconnect();
            session.disconnect();
            LOGGER.log(Level.INFO, "Disconnected from EC2 instance");
        }
    }
}

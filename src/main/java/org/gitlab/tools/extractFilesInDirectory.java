package org.gitlab.tools;

import org.zeroturnaround.zip.ZipUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

class extractFilesInDirectory {
    private static final String PROP_FILENAME = "config.properties";
    private final String archiveDirectory;
    private final String ext;
    private final String suffix;

    private extractFilesInDirectory(Properties prop) {
        archiveDirectory = prop.getProperty("archive_directory");
        ext = prop.getProperty("ext");
        suffix = prop.getProperty("suffix");
    }

    /**
     * Правим уровень логирования в ESB процессе
     */
    public static void main(String[] args) throws IOException {
        extractFilesInDirectory extractFilesInDirectory = new extractFilesInDirectory(readProperties());
        extractFilesInDirectory.changeTracingLevelInDirectory();
    }

    private void changeTracingLevelInDirectory() {
        System.out.println(String.format("extract " + ext + " files in directory: %s", getArchiveDirectory()));
        DirectoryStream.Filter<Path> documentFilter = entry -> {
            String fileName = entry.getFileName().toString();
            return fileName != null && fileName.endsWith(ext);
        };
        try (DirectoryStream<Path> pathList = Files.newDirectoryStream(Paths.get(getArchiveDirectory()),
                documentFilter)) {
            for (Path path : pathList) {
                Path tempDirectory = Files.createTempDirectory(suffix);
                System.out.println("extract " + path.toFile() + " to tempDirectory.toString() = " +
                        tempDirectory.toString());
                ZipUtil.unpack(path.toFile(), tempDirectory.toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private Properties readProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(PROP_FILENAME);
            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    private String getArchiveDirectory() {
        return archiveDirectory;
    }
}

package net.technolords.micro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSupport.class);
    private Path pathToClassFolder;
    private Path pathToDataFolder;
    private Path pathToTargetFolder;

    /**
     * Since the artificer project is part of a multi module Maven build, the start point of executing the test
     * can differ. I.e. from 'root pom' or from 'artificer pom' which means there are different path's to the
     * test resources. This method fixes the correct location of the test resources by calculating the path.
     */

    /**
     * Depending on the execution of the test files, whether from IDE or from multi module maven project (CLI),
     * the target and data folders are relative. In order to overcome this, the folders are calculated.
     */

    @BeforeClass
    public void configureRelativeFolders() {
        // Create String which depends on file system (Unix vs Windows) by using the file separator
        StringBuilder buffer = new StringBuilder();
        buffer.append("src").append(File.separator).append("test").append(File.separator);
        buffer.append("resources").append(File.separator).append("data");

        // Set path to folder containing the data, i.e. src/test/resources/data
        Path pathToData = FileSystems.getDefault().getPath(buffer.toString());
        if(!pathToData.toAbsolutePath().toString().contains("microservice-mock")) {
            StringBuilder pathWithPrefix = new StringBuilder();
            pathWithPrefix.append("artificer").append(File.separator).append(buffer.toString());
            pathToData = FileSystems.getDefault().getPath(pathWithPrefix.toString());
        }
        this.pathToDataFolder = pathToData.toAbsolutePath();
        LOGGER.debug("Data folder set: {} and exists: {}", this.pathToDataFolder.toString(), Files.exists(this.pathToDataFolder));

        // Set path to Target folder to create Output, i.e. target
        Path pathToTarget = FileSystems.getDefault().getPath("target");
        if(!pathToTarget.toAbsolutePath().endsWith("artificer" + File.separator + "target")) {
            pathToTarget = FileSystems.getDefault().getPath("artificer" + File.separator + "target");
        }
        this.pathToTargetFolder = pathToTarget.toAbsolutePath();
        LOGGER.debug("Target folder set: {} and exists: {}", this.pathToTargetFolder.toString(), Files.exists(this.pathToTargetFolder));

        // Set path to folder containing the data, i.e. src/test/resources/data/class
        this.pathToClassFolder = FileSystems.getDefault().getPath(pathToData.toAbsolutePath() + File.separator + "class");
        LOGGER.debug("Class folder set: {} and exists: {}", this.pathToClassFolder.toString(), Files.exists(this.pathToClassFolder));
    }

    /**
     * Auxiliary method to get a reference of the class folder, as sub folder of the data folder.
     *
     * @return
     *  The reference of the class folder.
     */
    public Path getPathToClassFolder() {
        return this.pathToClassFolder;
    }

    public Path getPathToDataFolder() {
        return this.pathToDataFolder;
    }

    public Path getPathToTargetFolder() {
        return  this.pathToTargetFolder;
    }
}

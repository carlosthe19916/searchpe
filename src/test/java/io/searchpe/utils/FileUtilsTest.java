package io.searchpe.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtilsTest {

    public static final Path FOLDER_TEST = Paths.get("fileUtilsTestFolder");
    public static final Path DEFAULT_FILE_PATH = FOLDER_TEST.resolve("file.txt");

    private void createFileWithRandomContent(Path path) throws Exception {
        org.apache.commons.io.FileUtils.writeByteArrayToFile(path.toFile(), new byte[]{1, 2, 3});
    }

    @Before
    public void before() throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(FOLDER_TEST.toFile());
    }

    @After
    public void after() throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(FOLDER_TEST.toFile());
    }

    /**
     * Should delete files if exists
     * {@link io.searchpe.utils.FileUtils#deleteFilesIfExists(String[])}
     */
    @Test
    public void test_shouldDeleteFilesIfExists() throws Exception {
        createFileWithRandomContent(DEFAULT_FILE_PATH);
        Assert.assertTrue(Files.exists(DEFAULT_FILE_PATH));

        FileUtils.deleteFilesIfExists(new String[]{DEFAULT_FILE_PATH.toAbsolutePath().toString()});
        Assert.assertFalse(Files.exists(DEFAULT_FILE_PATH));
    }

    /**
     * Should not thrown an exception if file does not exists
     * {@link io.searchpe.utils.FileUtils#deleteFilesIfExists(String[])}
     */
    @Test
    public void test_shouldDoNotThrownExceptionIfFileDoesNotExists() throws Exception {
        Assert.assertFalse(Files.exists(Paths.get(DEFAULT_FILE_PATH.toAbsolutePath().toString())));
        FileUtils.deleteFilesIfExists(new String[]{DEFAULT_FILE_PATH.toAbsolutePath().toString()});
    }

    /**
     * Should delete directory recursively
     * {@link io.searchpe.utils.FileUtils#deleteFilesIfExists(String[])}
     */
    @Test
    public void test_shouldDeleteDirectoryRecursively() throws Exception {
        Files.createDirectories(FOLDER_TEST.resolve("subFolder1").resolve("subFolder2"));
        createFileWithRandomContent(FOLDER_TEST.resolve(DEFAULT_FILE_PATH));
        Assert.assertTrue(Files.exists(FOLDER_TEST.resolve(DEFAULT_FILE_PATH)));

        FileUtils.deleteFilesIfExists(new String[]{FOLDER_TEST.toAbsolutePath().toString()});
        Assert.assertFalse(Files.exists(FOLDER_TEST));
    }


    /**
     * Should unzip file
     * {@link io.searchpe.utils.FileUtils#unzipFile(File, Path)}
     */
    @Test
    public void test_unzipFile() throws Exception {
        Path unzipPath = FOLDER_TEST.resolve("unzipFolder");

        FileUtils.unzipFile(new File("padron_reducido_ruc.zip"), unzipPath);
        Assert.assertTrue(Files.exists(unzipPath));
        Assert.assertTrue(Files.isDirectory(unzipPath));
        Assert.assertTrue(Files.exists(unzipPath.resolve("padron_reducido_ruc.txt")));
    }

}
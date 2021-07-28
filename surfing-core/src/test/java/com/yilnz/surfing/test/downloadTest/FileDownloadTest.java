package com.yilnz.surfing.test.downloadTest;

import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.downloader.filedownload.DownloadFile;
import com.yilnz.surfing.core.downloader.filedownload.FileDownloadProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileDownloadTest {

    @Test
    public void test() throws IOException {
        File tempFile = File.createTempFile("someprefix", "idownkon");
        File file = SurfSpider.download(tempFile.getAbsolutePath(), "http://www.baidu.com");
        Assert.assertTrue(file.exists());
        Assert.assertNotNull(Files.readAllBytes(file.toPath()));
        File file2 = SurfSpider.download(tempFile.getAbsolutePath(), "http://www.baidu.com");
        Assert.assertTrue(file2.exists());
        String fileString = new String(Files.readAllBytes(file2.toPath()), StandardCharsets.UTF_8);
        System.out.println("test downloadFile:" + file2.getAbsolutePath() + ":" + fileString);
        file2.delete();
    }

    @Test
    public void testIfExist(){
        String tmpDir = System.getProperty("java.io.tmpdir");
        File file = SurfSpider.downloadIfNotExist(tmpDir + "/abc", "http://www.baidu.com");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testBatch() throws IOException, ExecutionException, InterruptedException {
        File tempFile = File.createTempFile("someprefix", "idownkon");
        File parentFile = tempFile.getParentFile();
        tempFile.delete();
        List<Future<DownloadFile>> futures = SurfSpider.downloadBatch(parentFile.getAbsolutePath(), 5, new FileDownloadProcessor() {
            @Override
            public void downloadFinished(DownloadFile downloadFile) {
                System.out.println(downloadFile.getUrl() + ":" + downloadFile.getFilepath());
            }

            @Override
            public String getFileName(String url, Map<String, List<String>> headerFields) {
                System.out.println(headerFields);
                return "test123";
            }
        }, "http://www.baidu.com");

        for (Future<DownloadFile> file : futures) {
            DownloadFile downloadFile = file.get();
            System.out.println("download finishied: " + downloadFile.getUrl() + ":" + downloadFile.getFilepath());
            Assert.assertEquals(Paths.get(downloadFile.getFilepath()).getFileName().toString(), "test123");
            new File(downloadFile.getFilepath()).delete();
        }
    }
}

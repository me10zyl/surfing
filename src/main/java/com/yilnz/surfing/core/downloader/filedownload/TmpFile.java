package com.yilnz.surfing.core.downloader.filedownload;

import java.io.File;

public class TmpFile {

    private File file;

    public TmpFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean delete(){
        return file.delete();
    }

    @Override
    public String toString() {
        return file.toString();
    }
}

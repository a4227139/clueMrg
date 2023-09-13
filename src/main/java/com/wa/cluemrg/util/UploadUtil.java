package com.wa.cluemrg.util;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadUtil {

    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public String getSavePath() {
        // 这里需要注意的是ApplicationHome是属于SpringBoot的类
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());

        return applicationHome.getDir().getParentFile()
                .getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\excel\\"+sdf1.format(new Date());
    }

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空！";
        }
        String originalFilename = file.getOriginalFilename();
        int pointIndex = originalFilename.lastIndexOf(".");
        // 给文件重命名
        String fileName = originalFilename.substring(0,pointIndex)+"-"+sdf2.format(new Date())+originalFilename.substring(pointIndex,originalFilename.length());
        String fullFileName="";
        try {
            // 获取保存路径
            String path = getSavePath();
            File files = new File(path, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.transferTo(files);
            fullFileName=files.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullFileName; // 返回重命名后的文件名
    }
}

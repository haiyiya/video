package com.lyml.video;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Common {
    public static final String destPath = "http://127.0.0.1/upload/";

    public static List<String> transform(String path) {
        List<String> errFiles = new ArrayList<>();

        // 操作文件夹是否存在
        File uploadDir = new File(path);
        if (!uploadDir.exists() || !uploadDir.isDirectory()) {
            return errFiles;
        }

        // 获取全部文件
        File[] files = uploadDir.listFiles();
        if (files == null) {
            return errFiles;
        }

        for (File f : files) {
            if (f.isDirectory() | !f.getName().endsWith(".m3u8")) {
                continue;
            }

            // 读取文件流
            InputStream in;
            try {
                in = new FileInputStream(f);
            } catch (Exception ex) {
                errFiles.add(f.getName() + "：文件流获取错误");
                continue;
            }

            // 获得包含文件路径的行
            String line;
            String originDirName;
            String fileContent;
            try {
                String[] arr = getContent(in, StandardCharsets.UTF_8);
                line = arr[0];
                originDirName = arr[1];
                fileContent = arr[2];
            } catch (Exception ex) {
                errFiles.add(f.getName() + "：源文件读取错误|已转换");
                continue;
            }

            // m3u8文件名
            String dirName = f.getName().substring(0, f.getName().lastIndexOf("."));
            // m3u8文件夹url
            String dirPath = destPath + dirName;
            // 原m3u8路径
            String originPath = line.substring(0, line.lastIndexOf("/"));

            // 替换路径
            String content = fileContent.replace(originPath, dirPath);

            try {
                // 生成新m3u8文件
                File destFile = new File(f.getPath().substring(0, f.getPath().lastIndexOf(".")) + "_t.m3u8");
                writeContent(destFile, content, StandardCharsets.UTF_8);
            } catch (Exception ex) {
                errFiles.add(f.getName() + "：生成目标文件出错");
                continue;
            }

            // 重命名对应文件夹
            File originDirFile = new File(path + originDirName);
            File dirFile = new File(path + dirName);
            if (!(originDirFile.exists() && originDirFile.isDirectory() && !dirFile.exists() && originDirFile.renameTo(dirFile))) {
                errFiles.add(f.getName() + "：文件夹不存在|被占用|目标文件夹名被占用|重命名出错");
                continue;
            }

            // 关闭文件流，删除原m3u8
            try {
                in.close();
                if (!f.delete()) {
                    errFiles.add(f.getName() + "：删除原文件出错");
                }
            } catch (Exception ex) {
                errFiles.add(f.getName() + "：删除原文件出错");
            }
        }

        return errFiles;
    }

    public static String[] getContent(InputStream is, Charset encoding) throws Exception {
        String[] arr = null;
        StringBuffer sb = new StringBuffer();

        InputStreamReader isr = new InputStreamReader(is, encoding);
        BufferedReader bf = new BufferedReader(isr);

        String line;
        while ((line = bf.readLine()) != null) {
            sb.append("\n").append(line);

            if (arr == null && line.startsWith("file:///")) {
                String[] dirArr = line.split("/");
                if (dirArr[dirArr.length - 2].length() == 0 || dirArr[dirArr.length - 1].length() == 0) {
                    throw new Exception();
                }

                arr = new String[]{line, dirArr[dirArr.length - 2], ""};
            }
        }

        if (arr != null) {
            String content = sb.toString();
            arr[2] = content.substring(1, content.length() - 1);
            return arr;
        } else {
            throw new Exception();
        }
    }

    public static void writeContent(File file, String content, Charset encoding) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
        Writer writer = new BufferedWriter(osw);
        writer.write(content);
        writer.flush();
        writer.close();
    }
}

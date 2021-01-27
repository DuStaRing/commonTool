package com.zhouzifei.tool.media.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * 文件操作工具类
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @website https://www.zhouzifei.com
 * @date 2019年7月16日
 * @since 1.0
 */
@Slf4j
public class FileUtil{
    private static final String[] PICTURE_SUFFIXS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".svg"};

    /**
     * 删除目录，返回删除的文件数
     *
     * @param rootPath 待删除的目录
     * @param fileNum  已删除的文件个数
     * @return 已删除的文件个数
     */
    public static int deleteFiles(String rootPath, int fileNum) {
        File file = new File(rootPath);
        if (!file.exists()) {
            return -1;
        }
        if (file.isDirectory()) {
            File[] sonFiles = file.listFiles();
            if (sonFiles != null && sonFiles.length > 0) {
                for (File sonFile : sonFiles) {
                    if (sonFile.isDirectory()) {
                        fileNum = deleteFiles(sonFile.getAbsolutePath(), fileNum);
                    } else {
                        sonFile.delete();
                        fileNum++;
                    }
                }
            }
        } else {
            file.delete();
        }
        return fileNum;
    }


    public static String getPrefix(File file) {
        return getPrefix(file.getName());
    }

    public static String getPrefix(String fileName) {
        int idx = fileName.lastIndexOf(".");
        int xie = fileName.lastIndexOf("/");
        idx = idx == -1 ? fileName.length() : idx;
        xie = xie == -1 ? 0 : xie + 1;
        return fileName.substring(xie, idx);
    }

    public static String getSuffix(File file) {
        return getSuffix(file.getName());
    }

    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        index = -1 == index ? fileName.length() : index;
        return fileName.substring(index);
    }

    public static String getSuffixByUrl(String imgUrl) {
        String defaultSuffix = ".png";
        if (StringUtils.isEmpty(imgUrl)) {
            return defaultSuffix;
        }
        String fileName = imgUrl;
        if(imgUrl.contains("/")) {
            fileName = imgUrl.substring(imgUrl.lastIndexOf("/"));
        }
        String fileSuffix = getSuffix(fileName);
        return StringUtils.isEmpty(fileSuffix) ? defaultSuffix : fileSuffix;
    }

    public static String generateTempFileName(String imgUrl) {
        return "temp" + getSuffixByUrl(imgUrl);
    }

    public static boolean isPicture(String suffix) {
        return !StringUtils.isEmpty(suffix) && Arrays.asList(PICTURE_SUFFIXS).contains(suffix.toLowerCase());
    }

    public static void mkdirs(String filePath) {
        File file = new File(filePath);
        mkdirs(file);
    }

    public static void mkdirs(File file) {
        if (!file.exists()) {
            if (file.isDirectory()) {
                file.mkdirs();
            } else {
                file.getParentFile().mkdirs();
            }
        }
    }

    public static void checkFilePath(String realFilePath) {
        if (StringUtils.isEmpty(realFilePath)) {
            return;
        }
        File parentDir = new File(realFilePath).getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
    //下载视频
    public static void down(String src, String path, String name) {
        try {
            URL url = new URL(src);
            // 2.获取链接
            URLConnection conn = url.openConnection();
            long length = conn.getContentLengthLong();
            // 3.输入流
            InputStream inStream = conn.getInputStream();
            // 3.写入文件
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String suffix = getSuffixByUrl(src);
            String saveFile = path + name + suffix;
            FileOutputStream fs = new FileOutputStream(saveFile);
            byte[] buffer = new byte[1024];
            int i = 0, j = 0;
            int byteRead;
            while ((byteRead = inStream.read(buffer)) != -1) {
                i++;
                fs.write(buffer, 0, byteRead);
                if (i % 500 == 0) {
                    j++;
                    File file2 = new File(saveFile);
                    //控制输出小数点后的位数
                    DecimalFormat df = new DecimalFormat("#.##");
                    float f = (file2.length() / (float) length) * 100;
                    System.out.print("已下载：" + df.format(f) + "%\t\t");
                    if (j % 5 == 0) {
                        log.info("下载完成");
                    }
                }
            }
            log.info("\n已下载：100.00%");
            inStream.close();
            fs.close();
        } catch (IOException e) {
            e.toString();
        } catch (Exception e) {
            e.toString();
        }
    }
    public static String getName(String filePath) {
        if (null == filePath) {
            return filePath;
        } else {
            int len = filePath.length();
            if (0 == len) {
                return filePath;
            } else {
                if (isFileSeparator(filePath.charAt(len - 1))) {
                    --len;
                }
                int begin = 0;
                for(int i = len - 1; i > -1; --i) {
                    char c = filePath.charAt(i);
                    if (isFileSeparator(c)) {
                        begin = i + 1;
                        break;
                    }
                }
                return filePath.substring(begin, len);
            }
        }
    }
    public static InputStream getInputStreamByUrl(String url, String referer) {
        HttpGet httpGet = new HttpGet(checkUrl(url));
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        if (org.apache.commons.lang.StringUtils.isNotEmpty(referer)) {
            httpGet.setHeader("referer", referer);
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        InputStream in = null;
        try {
            response = httpclient.execute(httpGet);
            in = response.getEntity().getContent();
            if (response.getStatusLine().getStatusCode() == 200) {
                return in;
            } else {
                log.error("Error. %s", parseInputStream(in));
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return in;
    }
    private static String parseInputStream(InputStream in) throws IOException {
        String result = "";
        StringBuffer content = null;
        if (null != in) {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            content = new StringBuffer();
            String line = "";
            while ((line = r.readLine()) != null) {
                content.append(line);
            }
            result = content.toString();
        }
        return result;
    }
    /**
     * 校验Url，并返回完整的url
     *
     * @param url 待校验的url
     */
    private static String checkUrl(String url) {
        if (!org.apache.commons.lang.StringUtils.isEmpty(url)) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return url;
            }
            return url.startsWith("//") ? "https:" + url : "http://" + url;
        }
        return null;
    }
    public static boolean isFileSeparator(char c) {
        return '/' == c || '\\' == c;
    }
}
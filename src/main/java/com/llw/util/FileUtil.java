package com.llw.util;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @description: 文件工具类
 * @author: llw
 * @date: 2018-10-31
 */
public class FileUtil {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取本地本目录绝对路径
     * @return 本地根目录绝对路径
     * @throws Exception
     */
    public static String getLocalRootAbsolutePath() throws Exception {

        return System.getProperty("user.dir");
    }

    /**
     * 获取web根目录绝对路径
     * @return web根目录绝对路径
     * @throws Exception
     */
    public static String getWebRootAbsolutePath() throws Exception {

        return new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
    }

    /**
     * 客户端下载文件
     * @param downloadFileName 下载的文件名称
     * @param filePath 文件路径(相对路径，以/开头)
     * @param response 响应
     * @throws Exception
     */
    public static void downloadFile(String downloadFileName, String filePath, HttpServletResponse response) throws Exception {
        String fileAbsPath = getWebRootAbsolutePath() + filePath;
        logger.info("开始下载文件: " + fileAbsPath);
        File file = new File(fileAbsPath);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + downloadFileName);// 设置文件名

            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                logger.info("下载成功: " + fileAbsPath);
                logger.info("下载后的文件名: " + downloadFileName);
            } catch (Exception e) {
                logger.info("下载失败: " + fileAbsPath);
                LoggerUtil.printStackTrace(logger, e);
                throw e;
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    LoggerUtil.printStackTrace(logger, e);
                }
            }
        } else {
            logger.info("没有找到该文件");
            throw new Exception("没有找到该文件");
        }
    }

    /**
     * 上传文件
     * @param file 上传的文件
     * @param fileName 文件名
     * @param dirPath 文件目录路径(相对路径，以/开头)
     * @return 文件相对路径
     * @throws Exception
     */
    public static String uploadFile(MultipartFile file, String fileName, String dirPath) throws Exception {
        String absDirPath = getWebRootAbsolutePath() + dirPath;
        if (file.isEmpty()) {
            logger.info("上传失败，文件不能为空！");
            return null;
        }
        // 文件名
        logger.info("上传的文件名为: " + fileName);
        // 获取文件的后缀名
        String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        logger.info("上传的后缀名为: " + suffixName);
        // 文件上传后的路径
        String fullPath = absDirPath + "/" + fileName + suffixName;
        logger.info("文件上传后的路径: " + fullPath);
        // 解决中文问题，linux下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(fullPath);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            logger.info("新建目录: " + dest.getParentFile().getName());
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
        } catch (Exception e) {
            logger.info("上传文件失败: " + fullPath);
            LoggerUtil.printStackTrace(logger, e);
            throw e;
        }

        logger.info("上传文件成功: " + fullPath);

        return dirPath + "/" + fileName + suffixName;
    }

    /**
     * 删除文件
     * @param filePath 文件路径(相对路径，以/开头)
     * @throws Exception
     */
    public static void deleteFile(String filePath) throws Exception {
        String absFilePath = getWebRootAbsolutePath() + filePath;
        logger.info("准备删除文件的绝对路径为: " + absFilePath);
        File file = new File(absFilePath);
        if (file.isFile() && file.exists()) {
            if (file.delete()) {
                logger.info("删除文件成功: " + filePath);
            } else {
                logger.info("删除文件失败: " + filePath);
            }
        } else {
            logger.info("不存在该文件: " + filePath);
        }
    }

    /**
     * 创建word文档
     * @param templateName 模版名称(不用带扩展名)
     * @param downloadName 下载的文件名称
     * @param params word文档上的变量参数
     * @param response 响应
     * @param freeMarkerConfigurer freeMarker
     * @throws Exception
     */
    public static void createWordDoc(String templateName,
                                     String downloadName,
                                     Map<String, Object> params,
                                     HttpServletResponse response,
                                     FreeMarkerConfigurer freeMarkerConfigurer) throws Exception {
        logger.info("创建word文档, 模版名称: " + templateName + ", 参数: " + params.toString() + ", 客户端下载名称: " + downloadName + ".doc");
        response.setHeader("content-Type", "application/msword");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadName + ".doc");

        try {
            freeMarkerConfigurer.getConfiguration().setClassForTemplateLoading(FileUtil.class, "/templates/freemarker");
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName + ".ftl");

            template.process(params, new OutputStreamWriter(response.getOutputStream()));
        } catch (Exception e) {
            logger.info("创建word文档失败, 模版名称: " + templateName + ", 参数: " + params.toString() + ", 客户端下载名称: " + downloadName + ".doc");
            LoggerUtil.printStackTrace(logger, e);
            throw e;
        }
    }

}

package com.llw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;

/**
 * @description: 邮件工具类
 * @author: llw
 * @date: 2018-12-30
 */
public class MailUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

    /**
     * 简单内容邮件
     * @param mailSender JavaMailSender
     * @param senderName 发送方名称
     * @param fromAddr 发送方地址
     * @param toAddr 接收方地址
     * @param subject 主题
     * @param content 内容
     */
    public static void sendSimpleMail(JavaMailSender mailSender, String senderName, String fromAddr, String toAddr, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            if (StringUtil.isEmpty(senderName))
                message.setFrom(fromAddr);
            else
                message.setFrom(MimeUtility.encodeText(senderName) + "<" + fromAddr + ">");
            message.setTo(toAddr);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            logger.info("发送邮件成功, 发送方为: " + fromAddr + ", 接收方为: " + toAddr + ", 主题为: " + subject);
        } catch (Exception e) {
            logger.error("发送邮件失败", e);
        }
    }

    /**
     * 发送有附件的邮件
     * @param mailSender JavaMailSender
     * @param senderName 发送方名称
     * @param fromAddr 发送方地址
     * @param toAddr 接收方地址
     * @param subject 主题
     * @param content 内容
     * @param fileName 附件名称
     * @param filePath 附件路径
     */
    public static void sendAttachmentsMail(JavaMailSender mailSender, String senderName, String fromAddr, String toAddr, String subject, String content, String fileName, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mmh = new MimeMessageHelper(message, true);
            if (StringUtil.isEmpty(senderName))
                mmh.setFrom(fromAddr);
            else
                mmh.setFrom(MimeUtility.encodeText(senderName) + "<" + fromAddr + ">");
            mmh.setTo(toAddr);
            mmh.setSubject(subject);
            mmh.setText(content, true);

            UrlResource resource = new UrlResource(filePath);
            mmh.addAttachment(fileName, resource);

            mailSender.send(message);
            logger.info("发送邮件成功, 发送方为: " + fromAddr + ", 接收方为: " + toAddr + ", 主题为: " + subject);
        } catch (Exception e) {
            logger.error("发送邮件失败", e);
        }
    }

    /**
     * 发送批量文件
     * @param mailSender
     * @param senderName 发送方名称
     * @param fromAddr 发送方地址
     * @param toAddr 接受方地址
     * @param subject 主题
     * @param content 内容
     * @param files 文件(Map<fileName, path>)
     */
    public static void sendAttachmentsMailBatch(JavaMailSender mailSender, String senderName, String fromAddr, String toAddr, String subject, String content, Map<String, String> files) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mmh = new MimeMessageHelper(message, true);
            if (StringUtil.isEmpty(senderName))
                mmh.setFrom(fromAddr);
            else
                mmh.setFrom(MimeUtility.encodeText(senderName) + "<" + fromAddr + ">");
            mmh.setTo(toAddr);
            mmh.setSubject(subject);
            mmh.setText(content, true);
            if (files != null && files.size() > 0) {
                for (String fileName : files.keySet()) {
                    UrlResource resource = new UrlResource(files.get(fileName));
                    mmh.addAttachment(fileName, resource);
                }
            }
            mailSender.send(message);
            logger.info("发送邮件成功, 发送方为: " + fromAddr + ", 接收方为: " + toAddr + ", 主题为: " + subject);
        } catch (Exception var10) {
            logger.error("发送邮件失败", var10);
        }
    }

    /**
     * 发送有内嵌资源的邮件
     * @param mailSender JavaMailSender
     * @param senderName 发送方名称
     * @param fromAddr 发送方地址
     * @param toAddr 接收方地址
     * @param subject 主题
     * @param content 内容
     * @param filePath 文件路径
     */
    public static void sendInlineResourceMail(JavaMailSender mailSender, String senderName, String fromAddr, String toAddr, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mmh = new MimeMessageHelper(message, true);
            if (StringUtil.isEmpty(senderName))
                mmh.setFrom(fromAddr);
            else
                mmh.setFrom(MimeUtility.encodeText(senderName) + "<" + fromAddr + ">");
            mmh.setTo(toAddr);
            mmh.setSubject(subject);
            mmh.setText(content, true);

            UrlResource resource = new UrlResource(filePath);
            mmh.addInline(KeyGenerateUtil.getRandomKey(5), resource);

            mailSender.send(message);
            logger.info("发送邮件成功, 发送方为: " + fromAddr + ", 接收方为: " + toAddr + ", 主题为: " + subject);
        } catch (Exception e) {
            logger.error("发送邮件失败", e);
        }
    }

}

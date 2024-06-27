package com.easypan.service.impl;


import com.easypan.utils.RedisComponent;
import com.easypan.config.AppConfig;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SysSettingsDto;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.UserInfoQuery;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.UserInfoMapper;
import com.easypan.service.EmailCodeService;
import com.easypan.utils.RedisUtils;
import com.easypan.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;


/**
 * 邮箱验证码 业务接口实现
 */
@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private AppConfig appConfig;
    @Resource
    private RedisComponent redisComponent;

    @Resource
    private RedisUtils redisUtils;
    /**
     * 真正发送邮件验证码
     * @param toEmail 发送到德邮箱
     * @param code 需要发送的验证码
     */
    private void sendEmailCode(String toEmail, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //邮件发件人
            helper.setFrom(appConfig.getSendUserName());
            //邮件收件人 1或多个
            helper.setTo(toEmail);

            SysSettingsDto sysSettingsDto = redisComponent.getSysSettingsDto();

            //邮件主题
            helper.setSubject(sysSettingsDto.getRegisterEmailTitle());
            //邮件内容
            helper.setText(String.format(sysSettingsDto.getRegisterEmailContent(), code));
            //邮件发送时间
            helper.setSentDate(new Date());
            //发送
            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error("邮件发送失败", e);
            throw new BusinessException("邮件发送失败");
        }
    }

    /**
     * 发送邮箱验证码的前置和后置工作
     * @param toEmail 发送的邮箱地址
     * @param type  0:注册  1:找回密码
     */
    @Override
    @Transactional
    public void sendEmailCode(String toEmail, Integer type) {
        //如果是注册，校验邮箱是否已存在
        if (type == Constants.REGISTER_ZERO) {
            UserInfo userInfo = userInfoMapper.selectByEmail(toEmail);
            if (null != userInfo) {
                throw new BusinessException("邮箱已经存在");
            }
        }

        String code = StringTools.getRandomNumber(Constants.LENGTH_5);
        sendEmailCode(toEmail, code);

        redisUtils.delete(toEmail);
        logger.info("删除");

        redisUtils.setex(Constants.REDIS_KEY_EMAIL_CODE+toEmail,code,Constants.REDIS_KEY_EXPIRES_FIVE_MIN * 3);
        logger.info((String) redisUtils.get(toEmail));
    }

    @Override
    public void checkCode(String email, String code) {
        String emailCode = (String) redisUtils.get(Constants.REDIS_KEY_EMAIL_CODE+email);
        logger.info((String) redisUtils.get(Constants.REDIS_KEY_EMAIL_CODE+email));
        // 如果没查到数据
        if(emailCode == null){
            throw new BusinessException("邮箱验证已失效");
        }
        if (!emailCode.equals(code)) {
            throw new BusinessException("邮箱验证码不正确");
        }
        redisUtils.delete(Constants.REDIS_KEY_EMAIL_CODE+email);
    }

}
package com.tests.campuslostandfoundsystem.utils;

import com.tests.campuslostandfoundsystem.entity.enums.exception.UtilsResultCodes;
import com.tests.campuslostandfoundsystem.entity.utils.GraphCaptcha;
import com.tests.campuslostandfoundsystem.exception.UtilsException;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaUtils {
    private  String prefix ="utils:captcha:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "生成图形验证码")
    public GraphCaptcha generateCaptcha() {
        try {
            SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
            // 让验证码只包含数字 + 大写字母（你也可以改成 Captcha.TYPE_ONLY_NUMBER 全数字）
            specCaptcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);

            String code = specCaptcha.text();                    // 例如 "A9KX"
            String key = prefix + UUID.randomUUID().toString();  // 保持你原来的 key 前缀
            redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            specCaptcha.out(outputStream);
            String base64Image = "data:image/jpg;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return new GraphCaptcha(key, "", base64Image); // 不回传真实 code
        } catch (Exception e) {
            throw new UtilsException(UtilsResultCodes.GENERATE_CAPTCHA_FAILED, "生成图形验证码失败:" + e.getMessage(), e);
        }
    }

    @Operation(summary = "验证图形验证码")
    public boolean validateGraphCaptcha(String key, String code) {
        try {
            if (StringUtils.isBlank(code)) {
                throw new UtilsException(UtilsResultCodes.VALIDATE_CAPTCHA_FAILED, "验证码不能为空");
            }
            if (StringUtils.isBlank(key)) {
                throw new UtilsException(UtilsResultCodes.VALIDATE_CAPTCHA_FAILED, "key不能为空");
            }

            String real = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.isBlank(real)) {
                return false; // 过期或不存在
            }

            boolean ok = StringUtils.equalsIgnoreCase(
                    code.trim(),           // 去掉输入里的首尾空格
                    real.trim()
            );

            if (ok) {
                // 一次性：验证通过后删除，防止被重放
                redisTemplate.delete(key);
            }
            return ok;
        } catch (UtilsException e) {
            return false;
        }
    }

}

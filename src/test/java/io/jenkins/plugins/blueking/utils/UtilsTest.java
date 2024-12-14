package io.jenkins.plugins.blueking.utils;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    void testIpReg() {
        Assertions.assertTrue(ReUtil.isMatch(RegexPool.IPV4, "172.31.21.10"));
        Assertions.assertTrue(ReUtil.isMatch(RegexPool.IPV4, "192.168.21.10"));
        Assertions.assertTrue(ReUtil.isMatch(RegexPool.IPV4, "49.31.21.10"));
    }
}

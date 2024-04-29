package cn.itscloudy.propray;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PropRayUtilTest {

    @ParameterizedTest
    @CsvSource(value = {
            "A\\u4e2d\\u6587B, A中文B",
            "A\\u4e2dB\\u6587, A中B文",
            "Jack, Jack",
            "\\u4f60\\u597d, 你好",
    })
    void shouldToNormal(String iso, String expected) {
        String actual = PropRayUtil.toNormal(iso);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "A中文B, A\\u4e2d\\u6587B",
            "A中B文, A\\u4e2dB\\u6587",
            "Jack, Jack",
            "你好, \\u4f60\\u597d",
    })
    void shouldToIso(String normal, String expected) {
        String actual = PropRayUtil.toIso(normal);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "中文, true",
            "ABC, false",
            "Òaßb, false",
            "abc你好, true",
    })
    void shouldTestContainsNonIsoChars(String str, boolean expected) {
        boolean actual = PropRayUtil.containsNonIsoChars(str);

        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "abc\\u4e2d\\u6587, true",
            "abc\\u4e2\\u658, false",
            "abc, false",
            "Òaßb, false",
    })
    void shouldTestIsIsoCharsContainsUes(String str, boolean expected) {
        boolean actual = PropRayUtil.isIsoCharsContainsUes(str);

        Assertions.assertEquals(expected, actual);
    }
}

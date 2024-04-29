package cn.itscloudy.propray;

public class PropRayUtil {
    private PropRayUtil() {
    }


    public static String toNormal(String text) {
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder();

        int state = 0;
        char[] codeUnit = new char[4];
        for (char c : chars) {
            if (state == 0) {
                if (c == '\\') {
                    state = 1;
                } else {
                    sb.append(c);
                }
            } else if (state == 1) {
                if (c == 'u') {
                    state = 2;
                } else {
                    sb.append("\\");
                    sb.append(c);
                    state = 0;
                }
            } else if (state >= 2 && state <= 5) {
                if (Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                    codeUnit[state - 2] = c;
                    state++;
                    if (state == 6) {
                        sb.append(Character.toChars(Integer.parseInt(new String(codeUnit), 16)));
                        state = 0;
                    }
                } else {
                    sb.append("\\u");
                    sb.append(codeUnit, 0, state - 1);
                    sb.append(c);
                    state = 0;
                }
            }
        }
        return sb.toString();
    }

    public static String toIso(String text) {
        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c < 256) {
                sb.append(c);
            } else {
                String hex = Integer.toHexString(c);
                sb.append("\\u").append(hex);
            }
        }
        return sb.toString();
    }

    public static boolean containsNonIsoChars(String text) {
        if (text == null) {
            return false;
        }
        return text.chars().anyMatch(c -> c > 255);
    }

    public static boolean isIsoCharsContainsUes(String text) {
        if (text == null || text.length() < 6) {
            return false;
        }

        char[] chars = text.toCharArray();
        int state = 0;
        for (char c : chars) {
            if (state == 0) {
                if (c == '\\') {
                    state = 1;
                }
            } else if (state == 1) {
                if (c == 'u') {
                    state = 2;
                } else {
                    state = 0;
                }
            } else if (state >= 2 && state <= 5) {
                if (Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) {
                    state++;
                    if (state == 6) {
                        return true;
                    }
                } else {
                    state = 0;
                }
            }
        }
        return false;
    }
}

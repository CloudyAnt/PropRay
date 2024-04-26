package cn.itscloudy.propray;

public class PropRayUtil {
    private PropRayUtil() {
    }


    public static String irradiate(String text) {
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
                } else {
                    sb.append("\\u");
                    sb.append(codeUnit, 0, state - 1);
                    sb.append(c);
                    state = 0;
                }
            } else if (state == 6) {
                sb.append(Character.toChars(Integer.parseInt(new String(codeUnit), 16)));
                if (c == '\\') {
                    state = 1;
                } else {
                    sb.append(c);
                    state = 0;
                }
            }
        }
        return sb.toString();
    }
}

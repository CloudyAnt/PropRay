package cn.itscloudy.propray;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface PrConst {

    String ID = "PropRay";
    String NAME = ID;

    Map<String, String> LOCALES = new HashMap<>();
    Pattern VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    static String get(String key) {
        if (LOCALES.containsKey(key)) {
            return LOCALES.get(key);
        }

        String value = IcResourceBundle.instance.get(key);
        Matcher matcher = VAR_PATTERN.matcher(value);
        PriorityQueue<Replacement> replacements = new PriorityQueue<>();
        matcher.results().forEach(r -> {
            String group = r.group(1);
            String replacement = get(group);
            replacements.add(new Replacement(r.start(), r.end(), replacement));
        });

        if (!replacements.isEmpty()) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (Replacement replacement : replacements) {
                sb.append(value, i, replacement.start);
                sb.append(replacement.replacedValue);
                i = replacement.end;
            }
            sb.append(value, i, value.length());

            value = sb.toString();
        }

        LOCALES.put(key, value);
        return value;
    }

    class IcResourceBundle {
        private static final IcResourceBundle instance = new IcResourceBundle();

        private final ResourceBundle bundle = ResourceBundle.getBundle("locales.pr");

        public String get(String key) {
            return bundle.getString(key);
        }
    }

    class Replacement implements Comparable<Replacement> {
        private final int start;
        private final int end;
        private final String replacedValue;

        public Replacement(int start, int end, String replacedValue) {
            this.start = start;
            this.end = end;
            this.replacedValue = replacedValue;
        }

        @Override
        public int compareTo(@NotNull PrConst.Replacement replacement) {
            if (this.start < replacement.start) {
                return -1;
            } else if (this.start > replacement.start) {
                return 1;
            }
            return 0;
        }
    }
}

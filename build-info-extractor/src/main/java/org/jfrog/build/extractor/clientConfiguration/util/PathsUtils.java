package org.jfrog.build.extractor.clientConfiguration.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tamirh on 04/05/2016.
 */
public class PathsUtils {

    private static final String REGEXP_CHARS = "*+?[]$^.{}|()";
    private static final char ESCAPE_CHARACTER = "\\".charAt(0);

    public static String reformatRegexp(String sourceString, String destString, Pattern regexPattern) {
        String target = destString;
        Matcher matcher = regexPattern.matcher(sourceString.replace("\\", "/"));
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String currentGroup = matcher.group(i);
                if (currentGroup == null) {
                    continue;
                }
                target = target.replace("{" + i + "}", currentGroup);
            }
        }
        return target;
    }

    /**
     * Returns a substring from the beginning of the string till the first regex chars: "*?[]$^.{}|()"
     * @param baseDir the String
     * @return a substring from the beginning of the string till the first regex chars: "*?[]$^.{}|()"
     */
    public static String substringBeforeFirstRegex(String baseDir) {
        if (StringUtils.isEmpty(baseDir)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int length = baseDir.length();
        for (int i = 0; i < length; i++) {
            char nextChar = baseDir.charAt(i);
            if (StringUtils.contains(REGEXP_CHARS, nextChar)) {
                break;
            }
            if (nextChar == ESCAPE_CHARACTER) {
                if (baseDir.length() < i + 2) {
                    throw new IllegalStateException("Base directory: " + baseDir + " ends with an escape character.");
                }
                result.append(baseDir.charAt(i + 1));
                i = i + 1;
            } else {
                result.append(baseDir.charAt(i));
            }
        }
        return result.toString();
    }

    /**
     * Adds escape character before each one of the following chars: "*?[]$^.{}|()"
     * @param pattern the provided String
     * @return the provided by the user String with prepended escape chars before each regex char
     */
    public static String escapeRegexChars(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int length = pattern.length();
        for (int i = 0 ; i < length; i++) {
            String nextChar = pattern.substring(i ,i + 1);
            if (StringUtils.contains(REGEXP_CHARS, nextChar)) {
                result.append(ESCAPE_CHARACTER).append(nextChar);
            } else {
                result.append(nextChar);
            }
        }
        return result.toString();
    }

    public static String pathToRegExp(String path) {
        String wildcard = ".*";
        StringBuilder sb = new StringBuilder(path.length());
        int length = path.length();
        for (int i = 0, is = length; i < is; i++) {
            char c = path.charAt(i);
            switch(c) {
                case '*':
                    sb.append(wildcard);
                    break;
                case '?':
                    sb.append(".");
                    break;
                // Escape special regexp-characters
                case '[': case ']': case '$': case '^':
                case '.': case '{': case '}': case '|':
                case '\\': case '+':
                    sb.append("\\");
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }

        String newPath = sb.toString();
        if (newPath.endsWith("/")) {
            newPath += wildcard;
        }
        return  "^" + newPath + "$";
    }

    /**
     * @param targetPath the path which the file name will be taken targetDir/targetPath/targetFileName
     * @param srcPath    the path which the file name will be replace srcDir/srcPath/srcFileName
     * @return map with the new targetPath and srcPath as values.
     */
    public static Map<String, String> replaceFilesName(String targetPath, String srcPath) {
        String targetDirPath = StringUtils.substringBeforeLast(targetPath, "/");
        String targetFileName = StringUtils.substringAfterLast(targetPath, "/");
        Map<String, String> result = new HashMap<String, String>();
        result.put("targetPath", targetDirPath);
        result.put("srcPath", srcPath.contains("/") ?
                StringUtils.substringBeforeLast(srcPath, "/") + "/" + targetFileName :
                targetFileName);
        return result;
    }

    /**
     * The method removes all unescaped instances of the provided Char from the provided String.
     * An unescaped char is a char with no leading backslash before it.
     * For example removeUnescapedChar(A$A\$A, "$".charAt(0)) will return "AA\$A"
     * @param stringToRemoveFrom the string to apply the method on
     * @param characterToRemove the character to remove from the string
     * @return stringToRemoveFrom without unescaped characterToRemove
     */
    public static String removeUnescapedChar(String stringToRemoveFrom, Character characterToRemove) {
        String separator = String.valueOf(characterToRemove);
        if (REGEXP_CHARS.contains(separator)) {
            separator = "\\" + characterToRemove;
        }
        String[] strings = stringToRemoveFrom.split(separator);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            if (string.endsWith("\\")) {
                stringBuilder.append(string).append(characterToRemove);
            } else {
                stringBuilder.append(string);
            }
        }
        return stringBuilder.toString();
    }

    public static String escapeSpecialChars(String str) {
        String specialChars = "\\" + REGEXP_CHARS;
        for (char c : specialChars.toCharArray()){
            str = str.replace(String.valueOf(c), "\\" + c);
        }
        return str;
    }
}

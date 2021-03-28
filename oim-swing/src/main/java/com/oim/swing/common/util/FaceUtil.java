package com.oim.swing.common.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oim.common.util.EmojiUtil;

/**
 * @author: XiaHui
 * @date: 2017年6月1日 下午5:28:55
 */
public class FaceUtil {

    public static String getFacePath(String categoryId, String value) {
        String fullPath = "";
        if ("classical".equals(categoryId)) {
            String path = "Resources/Images/Face/" + value + ".gif";
            File file = new File(path);
            if (file.exists()) {
                fullPath = file.getAbsolutePath();
            }
        } else if ("emoji".equals(categoryId)) {
            String path = "Resources/Images/Face/emoji/23x23/" + value + ".png";
            File file = new File(path);
            if (file.exists()) {
                fullPath = file.getAbsolutePath();
            }
        }
        return fullPath;
    }

    public static String toEmojiImage(String source, String path, String suffix) {
        if (source != null) {

            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            while (emojiMatcher.find()) {
                String e = emojiMatcher.group();
                String code = EmojiUtil.emojiCode(e);
                String image = code + suffix;
                String imagePath = path + "" + image;
                File file = new File(imagePath);
                if (file.exists()) {
                    String imageTag = getImageTag("", "face", "emoji," + code, file.getAbsolutePath());
                    source = source.replace(e, imageTag);
                }
                // source = emojiMatcher.replaceAll("*");
            }
        }
        return source;
    }

    private static String getImageTag(String id, String name, String value, String path) {
        StringBuilder image = new StringBuilder();
        image.append("<img ");
        if (null != id && !"".equals(id)) {
            image.append(" id=\"");
            image.append(id);
            image.append("\"");
        }
        if (null != name && !"".equals(name)) {
            image.append(" name=\"");
            image.append(name);
            image.append("\"");
        }
        if (null != value && !"".equals(value)) {
            image.append(" value=\"");
            image.append(value);
            image.append("\"");
        }
        if (null != path && !"".equals(path)) {
            String p = path.replace("\\", "/");
            image.append(" src=\"file:");
            if (p.startsWith("/")) {

            } else {
                image.append("/");
            }
            image.append(p);
            image.append("\" ");
        }
        image.append(" />");
        return image.toString();
    }
}

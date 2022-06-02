package com.hirohiro716.javafx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hirohiro716.StringConverter;

import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;

/**
 * NodeのローカルCSSの扱いを補助するクラス。
 *
 * @author hiro
 */
public class CSSHelper {
    
    /**
     * 定義されたスタイルから値を取得する。
     *
     * @param originalStyle スタイル全体の文字列
     * @param propertyName プロパティ
     * @return プロパティ設定値
     */
    public static String findStyleValue(String originalStyle, String propertyName) {
        String convStyle = StringConverter.nullReplace(originalStyle, "");
        Pattern p = Pattern.compile(propertyName + ":.{1,}?;");
        Matcher matcher = p.matcher(convStyle);
        if (matcher.find()) {
            String fontString = matcher.group();
            return fontString.replace(propertyName, "").replace(":", "").replace(";", "");
        }
        return "";
    }

    /**
     * 定義されたスタイルの値を再設定する。
     *
     * @param originalStyle スタイル全体の文字列
     * @param propertyName プロパティ
     * @param value プロパティ変更値
     * @return 変更後のスタイル
     */
    public static String updateStyleValue(String originalStyle, String propertyName, String value) {
        String newStyle = StringConverter.nullReplace(originalStyle, "");
        Pattern p = Pattern.compile(propertyName + ":.{1,}?;");
        Matcher matcher = p.matcher(newStyle);
        if (matcher.find()) {
            newStyle = matcher.replaceAll(StringConverter.join(propertyName + ": ", value, ";"));
        } else {
            newStyle = newStyle + StringConverter.join(propertyName + ": ", value, ";");
        }
        return newStyle;
    }

    /**
     * 定義されたスタイルの一部を削除する。
     *
     * @param originalStyle スタイル全体の文字列
     * @param propertyName プロパティ
     * @return 変更後のスタイル
     */
    public static String removeStyle(String originalStyle, String propertyName) {
        String newStyle = StringConverter.nullReplace(originalStyle, "");
        Pattern p = Pattern.compile(propertyName + ":.{1,}?;");
        Matcher matcher = p.matcher(newStyle);
        if (matcher.find()) {
            newStyle = matcher.replaceAll("");
        }
        return newStyle;
    }

    /**
     * ColorからCSSで使用できる色文字列に変換する。
     *
     * @param color
     * @return CSSで使用できる色文字列
     */
    public static String colorToRGBA(Color color) {
        if (color == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder("rgba(");
        builder.append(Math.round(255 * color.getRed()));
        builder.append(",");
        builder.append(Math.round(255 * color.getGreen()));
        builder.append(",");
        builder.append(Math.round(255 * color.getBlue()));
        builder.append(",");
        builder.append(color.getOpacity());
        builder.append(")");
        return builder.toString();
    }

    /**
     * BorderStrokeStyleから-fx-border-styleで使用できる値に変換する。
     *
     * @param borderStrokeStyle
     * @return -fx-border-styleで使用できる値
     */
    public static String borderStrokeStyleToString(BorderStrokeStyle borderStrokeStyle) {
        if (borderStrokeStyle == null) {
            return null;
        }
        String[] splited = borderStrokeStyle.toString().split("\\.");
        return splited[1].toLowerCase();
    }

    /**
     * BorderWidthsから-fx-border-widthで使用できる値に変換する。
     *
     * @param borderWidths
     * @return -fx-border-widthで使用できる値
     */
    public static String borderWidthsToString(BorderWidths borderWidths) {
        if (borderWidths == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(borderWidths.getTop());
        builder.append(" ");
        builder.append(borderWidths.getRight());
        builder.append(" ");
        builder.append(borderWidths.getBottom());
        builder.append(" ");
        builder.append(borderWidths.getLeft());
        builder.append("");
        return builder.toString();
    }
}

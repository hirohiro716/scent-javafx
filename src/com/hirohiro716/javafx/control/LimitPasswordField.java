package com.hirohiro716.javafx.control;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.control.PasswordField;

import com.hirohiro716.StringConverter;

/**
 * 入力制限機能を付けたPasswordField.
 * @author hiro
 */
public class LimitPasswordField extends PasswordField {

    private final ArrayList<Pattern> permitRegexs = new ArrayList<>();
    private final ArrayList<Boolean> permitRegexReverses = new ArrayList<>();
    private StringConverter converter = null;

    /**
     * コンストラクタ.
     */
    public LimitPasswordField() {
        this.focusedProperty().addListener(this.convertTextListener);
    }

    /**
     * 最大文字数プロパティ
     */
    public final IntegerProperty maxLength = new SimpleIntegerProperty(-1);

    /**
     * 最大文字数プロパティを取得する.
     * @return IntegerProperty
     */
    public IntegerProperty maxLengthProperty() {
        return this.maxLength;
    }

    /**
     * 最大文字数を取得する.
     * @return 最大文字数
     */
    public int getMaxLength() {
        return this.maxLength.getValue();
    }

    /**
     * 最大文字数をセットする.
     * @param maxLength 最大文字数
     */
    public void setMaxLength(int maxLength) {
        this.maxLength.setValue(maxLength);
    }

    /**
     * 正規表現のパターン配列を取得する.
     * @return Patternの配列
     */
    public Pattern[] getPermitRegexs() {
        return (Pattern[]) this.permitRegexs.toArray();
    }

    /**
     * 正規表現に一致した文字列を許可するように設定する. 複数の追加が可能.
     * @param permitRegex 正規表現Pattern
     * @param reverse 条件を逆転するか
     */
    public void addPermitRegex(Pattern permitRegex, boolean reverse) {
        this.permitRegexs.add(permitRegex);
        this.permitRegexReverses.add(reverse);
    }

    /**
     * 正規表現を逆転するかの配列を取得します.
     * @return 条件を逆転するかをbooleanで表した配列
     */
    public Boolean[] getPermitRegexReverses() {
        return (Boolean[]) this.permitRegexReverses.toArray();
    }

    /**
     * フォーカス喪失時に値を変換するStringConverterインスタンスをセットする.
     * @param converter コンバーターインスタンス
     */
    public void setStringConverter(StringConverter converter) {
        this.converter = converter;
    }

    /**
     * フォーカス喪失時に値を変換するStringConverterインスタンスを取得する.
     * @return converter コンバーターインスタンス
     */
    public StringConverter getStringConverter() {
        return this.converter;
    }

    /**
     * 文字列を入力する前にチェックして制御します.
     * @param start
     * @param end
     * @param text
     */
    @Override
    public void replaceText(int start, int end, String text) {
        try {
            if (text.equals("")) {
                super.replaceText(start, end, text);
            } else {
                int selectionLen = end - start;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    String checkChar = text.substring(i, i + 1);
                    boolean regexCheck = true;
                    for (int regexIndex = 0; regexIndex < this.permitRegexs.size(); regexIndex++) {
                        Pattern pattern = this.permitRegexs.get(regexIndex);
                        boolean reverse = this.permitRegexReverses.get(regexIndex);
                        if (pattern.matcher(checkChar).find() == reverse) {
                            regexCheck = false;
                        }
                    }
                    if (regexCheck
                            && this.getLength() - selectionLen + stringBuilder.length() < this.getMaxLength()
                            || regexCheck && this.getMaxLength() == -1) {
                        stringBuilder.append(checkChar);
                    }
                }
                super.replaceText(start, end, stringBuilder.toString());
            }
        } catch (Exception exception) {
        }
    }

    /**
     * 文字列を入力する前にチェックして制御します.
     * @param text
     */
    @Override
    public void replaceSelection(String text) {
        try {
            if (text.equals("")) {
                super.replaceSelection(text);
            } else {
                IndexRange range = getSelection();
                int end = range.getEnd();
                int start = range.getStart();
                int selectionLen = end - start;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    String checkChar = "";
                    try {
                    checkChar = text.substring(i, i + 1);
                    } catch (StringIndexOutOfBoundsException exception) {
                        // nop
                    }
                    boolean regexCheck = true;
                    for (int regexIndex = 0; regexIndex < this.permitRegexs.size(); regexIndex++) {
                        Pattern pattern = this.permitRegexs.get(regexIndex);
                        boolean reverse = this.permitRegexReverses.get(regexIndex);
                        if (pattern.matcher(checkChar).find() == reverse) {
                            regexCheck = false;
                        }
                    }
                    if (regexCheck
                            && this.getLength() - selectionLen + stringBuilder.length() < this.getMaxLength()
                            || regexCheck && this.getMaxLength() == -1) {
                        stringBuilder.append(checkChar);
                    }
                }
                super.replaceSelection(stringBuilder.toString());
            }
        } catch (Exception exception) {
        }
    }

    /*
     * フォーカス喪失時に数値として有効な文字列にフォーマットする
     */
    private ChangeListener<Boolean> convertTextListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (oldValue && newValue == false
                    && LimitPasswordField.this.converter != null) {
                String org = getText();
                String formatVal = LimitPasswordField.this.converter.execute(org);
                setText(formatVal);
            }
        }
    };

}
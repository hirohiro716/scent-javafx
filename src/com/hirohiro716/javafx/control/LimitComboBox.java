package com.hirohiro716.javafx.control;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.hirohiro716.StringConverter;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

/**
 * 入力制限機能を付けたComboBox
 * @author hiro
 * @param <T>
 */
public class LimitComboBox<T> extends ComboBox<T> {

    private final ArrayList<Pattern> permitRegexs = new ArrayList<>();
    private final ArrayList<Boolean> permitRegexReverses = new ArrayList<>();
    private StringConverter converter = null;

    /**
     * コンストラクタ.
     */
    public LimitComboBox() {
        this(FXCollections.observableArrayList());
    }

    /**
     * コンストラクタ.
     * @param items
     */
    public LimitComboBox(ObservableList<T> items) {
        super(items);
        this.getEditor().textProperty().addListener(this.textChangeListener);
        this.getEditor().focusedProperty().addListener(this.convertTextListener);
        this.getEditor().focusedProperty().addListener(this.textSelectAllListener);
        this.addEventFilter(KeyEvent.KEY_RELEASED, this.keyReleasedEventHanler);
    }

    /**
     * 値をBSやDELキーで消す事ができるかどうかを示すプロパティ.
     */
    public final BooleanProperty clearable = new SimpleBooleanProperty(false);

    /**
     * 値をBSやDELキーで消す事ができるようになる.
     * @param isClearable
     */
    public void setClearable(boolean isClearable) {
        this.clearable.set(isClearable);
    }
    /**
     * 値をBSやDELキーで消すことができるかどうか.
     * @return isClearable
     */
    public boolean getClearable() {
        return this.clearable.get();
    }

    /**
     * 値をBSやDELキーで消すことができるかどうか.
     * @return isClearable
     */
    public boolean isClearable() {
        return this.clearable.get();
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
     * テキストを精査して入力する.
     */
    private ChangeListener<String> textChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            try {
                LimitComboBox<?> combo = LimitComboBox.this;
                if (newValue.equals("")) {
                    return;
                }
                if (newValue.length() > combo.getMaxLength() && oldValue.length() == combo.getMaxLength() && combo.getMaxLength() != -1) {
                    combo.getEditor().setText(oldValue);
                    return;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < newValue.length(); i++) {
                    String checkChar = newValue.substring(i, i + 1);
                    boolean regexCheck = true;
                    for (int regexIndex = 0; regexIndex < combo.permitRegexs.size(); regexIndex++) {
                        Pattern pattern = combo.permitRegexs.get(regexIndex);
                        boolean reverse = combo.permitRegexReverses.get(regexIndex);
                        if (pattern.matcher(checkChar).find() == reverse) {
                            regexCheck = false;
                        }
                    }
                    if (regexCheck) {
                        stringBuilder.append(checkChar);
                    }
                }
                String regexNewValue = stringBuilder.toString();
                if (regexNewValue.length() > combo.getMaxLength() && combo.getMaxLength() != -1) {
                    combo.getEditor().setText(regexNewValue.substring(0, combo.getMaxLength()));
                } else {
                    combo.getEditor().setText(regexNewValue);
                }
            } catch (Exception exception) {
            }

        }
    };

    /**
     * フォーカス喪失時に有効な文字列にフォーマットする.
     */
    private ChangeListener<Boolean> convertTextListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (oldValue && newValue == false
                    && LimitComboBox.this.converter != null) {
                String org = LimitComboBox.this.getEditor().getText();
                String formatVal = LimitComboBox.this.converter.execute(org);
                LimitComboBox.this.getEditor().setText(formatVal);
            }
        }
    };

    /**
     * フォーカス取得時に文字列をすべて選択する.
     */
    private ChangeListener<Boolean> textSelectAllListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (oldValue == false && newValue && LimitComboBox.this.isEditable()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        LimitComboBox.this.getEditor().selectAll();
                    }
                });
            }
        }
    };

    /**
     * BSやDELキーで値にnullを入力する.
     */
    private EventHandler<KeyEvent> keyReleasedEventHanler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            LimitComboBox<T> comboBox = LimitComboBox.this;
            if (comboBox.clearable.get() && comboBox.isEditable() == false) {
                switch (event.getCode()) {
                case BACK_SPACE:
                case DELETE:
                    comboBox.setValue(null);
                    break;
                default:
                    break;
                }
            }
        }
    };

}

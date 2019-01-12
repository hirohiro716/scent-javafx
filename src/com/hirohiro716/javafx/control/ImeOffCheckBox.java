package com.hirohiro716.javafx.control;

import com.hirohiro716.InterfaceKeyInputRobotJapanese.ImeMode;
import com.hirohiro716.javafx.ImeHelper;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;

/**
 * フォーカスした際にIMEをOFFにするCheckBox.
 * @author hiro
 */
public class ImeOffCheckBox extends CheckBox {

    /**
     * コンストラクタ.
     */
    public ImeOffCheckBox() {
        this("");
    }

    /**
     * コンストラクタ.
     * @param text ラベルテキスト
     */
    public ImeOffCheckBox(String text) {
        super(text);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ImeHelper.apply(ImeOffCheckBox.this, ImeMode.OFF);
            }
        });
    }

}

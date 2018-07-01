package com.hirohiro716.javafx;

import java.awt.AWTException;

import com.hirohiro716.awt.RobotJapanese;
import com.hirohiro716.awt.RobotJapanese.ImeMode;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

/**
 * キーボードのショートカットキーをエミュレートしてIMEを制御しようとするクラス.
 * @author hiro
 */
public class IMEHelper {

    /**
     * TextInputControlを継承したコントロールにIME制御用のListenerを付与する.
     * @param <T> javafx.scene.control.Controlを継承したクラスオブジェクト
     * @param control TextInputControlを継承したコントロール
     * @param imeMode IMEモード
     */
    public static <T extends Control> void apply(T control, ImeMode imeMode) {
        createInstance();
        if (ROBOT == null) {
            return;
        }
        control.focusedProperty().addListener(new ImeFocusChangeListener(imeMode));
        control.addEventHandler(MouseEvent.MOUSE_CLICKED, new ImeClickEventHandler(imeMode));
    }

    private static RobotJapanese ROBOT;

    /**
     * RobotJapaneseインスタンスを生成する.
     */
    private static void createInstance() {
        if (ROBOT == null) {
            try {
                ROBOT = new RobotJapanese();
            } catch (AWTException exception) {
            }
        }
    }
    
    /**
     * 内部のRobotJapaneseインスタンスを取得する.
     * @return RobotJapanese
     */
    public static RobotJapanese getRobotInstance() {
        createInstance();
        return ROBOT;
    }
    
    /**
     * IMEをOFFにする.
     */
    public static void changeImeOff() {
        ROBOT.changeImeOff();
    }

    /**
     * IMEをひらがなにする.
     */
    public static void changeImeHiragana() {
        ROBOT.changeImeHiragana();
    }

    /**
     * IMEを全角カタカナにする.
     */
    public static void changeImeKatakanaWide() {
        ROBOT.changeImeKatakanaWide();
    }

    /**
     * IMEを半角ｶﾀｶﾅにする.
     */
    public static void changeImeKatakanaNarrow() {
        ROBOT.changeImeKatakanaNarrow();
    }

    /**
     * コントロールがフォーカスを得た際にIMEモードの変更を試みるクラス.
     * @author hiro
     */
    private static class ImeFocusChangeListener implements ChangeListener<Boolean> {

        private ImeMode mode;

        /**
         * コンストラクタ
         * @param mode
         */
        public ImeFocusChangeListener(ImeMode mode) {
            this.mode = mode;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (ImeFocusChangeListener.this.mode == null) {
                            return;
                        }
                        switch (ImeFocusChangeListener.this.mode) {
                        case OFF:
                            ROBOT.changeImeOff();
                            break;
                        case HIRAGANA:
                            ROBOT.changeImeHiragana();
                            break;
                        case KATAKANA_WIDE:
                            ROBOT.changeImeKatakanaWide();
                            break;
                        case KATAKANA_NARROW:
                            ROBOT.changeImeKatakanaNarrow();
                            break;
                        }
                    }
                });
            }
        }

    }

    /**
     * コントロールをクリックした際にIMEモードの変更を試みるクラス.
     * @author hiro
     */
    public static class ImeClickEventHandler implements EventHandler<MouseEvent> {

        private ImeMode mode;

        /**
         * コンストラクタ
         * @param mode
         */
        public ImeClickEventHandler(ImeMode mode) {
            this.mode = mode;
        }

        @Override
        public void handle(MouseEvent event) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (ImeClickEventHandler.this.mode == null) {
                        return;
                    }
                    switch (ImeClickEventHandler.this.mode) {
                    case OFF:
                        ROBOT.changeImeOff();
                        break;
                    case HIRAGANA:
                        ROBOT.changeImeHiragana();
                        break;
                    case KATAKANA_WIDE:
                        ROBOT.changeImeKatakanaWide();
                        break;
                    case KATAKANA_NARROW:
                        ROBOT.changeImeKatakanaNarrow();
                        break;
                    }
                }
            });
        }

    }

}

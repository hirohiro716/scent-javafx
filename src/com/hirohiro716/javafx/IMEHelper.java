package com.hirohiro716.javafx;

import java.awt.AWTException;

import com.hirohiro716.awt.RobotJapanese;
import com.hirohiro716.awt.RobotJapanese.ImeMode;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
        ImeChangeRunnable runnable = new ImeChangeRunnable(imeMode);
        control.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue && isShiftKeyDown == false) {
                    Platform.runLater(runnable);
                }
            }
        });
        control.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(runnable);
            }
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                control.getParent().setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode() == KeyCode.SHIFT) {
                            isShiftKeyDown = true;
                        }
                    }
                });
                control.getParent().setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (isShiftKeyDown && event.getCode() == KeyCode.SHIFT) {
                            isShiftKeyDown = false;
                        }
                    }
                });
            }
        });
    }
    
    private static RobotJapanese ROBOT;
    
    private static boolean isShiftKeyDown = false;
    
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
     * IMEの変更を試みる実行Runnableクラス.
     * @author hiro
     */
    private static class ImeChangeRunnable implements Runnable {

        private ImeMode imeMode;
        
        /**
         * コンストラクタ.
         * @param imeMode
         */
        public ImeChangeRunnable(ImeMode imeMode) {
            this.imeMode = imeMode;
        }
        
        @Override
        public void run() {
            if (this.imeMode == null) {
                return;
            }
            switch (this.imeMode) {
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
        
    }
    
}

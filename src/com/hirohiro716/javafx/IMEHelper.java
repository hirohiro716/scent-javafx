package com.hirohiro716.javafx;

import com.hirohiro716.javafx.robot.RobotJapanese;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;

import java.lang.Thread.State;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

/**
 * キーボードのショートカットキーをシミュレートしてIMEを制御しようとするクラス.
 * @author hiro
 */
public class IMEHelper {
    
    
    /**
     * コントロールにIME制御用のListenerを付与する.
     * @param <T> javafx.scene.control.Controlを継承したクラスオブジェクト
     * @param control コントロール
     * @param imeMode IMEモード
     */
    public static <T extends Control> void apply(T control, IMEMode imeMode) {
        ImeChangeRunnable imeChangeRunnable = new ImeChangeRunnable(imeMode);
        control.focusedProperty().addListener(new ImeChangeFocusedChangeListener(imeChangeRunnable));
        control.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                imeChangeRunnable.run();
            }
        });
    }
    
    private static RobotJapanese ROBOT;
    
    /**
     * 内部のRobotJapaneseインスタンスを取得する.
     * @return RobotJapanese
     */
    public static RobotJapanese getRobotInstance() {
        if (ROBOT == null) {
            ROBOT = new RobotJapanese();
        }
        return ROBOT;
    }
    
    private static long focusedImeChangeWaitTime = 700;
    
    /**
     * フォーカス取得時にIMEモード変更処理をするまでの時間をセットする. デフォルトは700ミリ秒.
     * @param waitTime
     */
    public static void setFocusedImeChangeWaitTime(long waitTime) {
        focusedImeChangeWaitTime = waitTime;
    }
    
    /**
     * IMEをOFFにする.
     */
    public static void changeImeOff() {
        getRobotInstance().changeImeOff();
    }

    /**
     * IMEをひらがなにする.
     */
    public static void changeImeHiragana() {
        getRobotInstance().changeImeHiragana();
    }

    /**
     * IMEを全角カタカナにする.
     */
    public static void changeImeKatakanaWide() {
        getRobotInstance().changeImeKatakanaWide();
    }

    /**
     * IMEを半角ｶﾀｶﾅにする.
     */
    public static void changeImeKatakanaNarrow() {
        getRobotInstance().changeImeKatakanaNarrow();
    }
    
    /**
     * IMEの変更を試みる実行Runnableクラス.
     * @author hiro
     */
    private static class ImeChangeRunnable implements Runnable {

        private IMEMode imeMode;
        
        /**
         * コンストラクタ.
         * @param imeMode
         */
        public ImeChangeRunnable(IMEMode imeMode) {
            this.imeMode = imeMode;
        }
        
        @Override
        public void run() {
            if (this.imeMode == null) {
                return;
            }
            switch (this.imeMode) {
            case OFF:
                getRobotInstance().changeImeOff();
                break;
            case HIRAGANA:
                getRobotInstance().changeImeHiragana();
                break;
            case KATAKANA_WIDE:
                getRobotInstance().changeImeKatakanaWide();
                break;
            case KATAKANA_NARROW:
                getRobotInstance().changeImeKatakanaNarrow();
                break;
            }
        }
        
    }

    /**
     * フォーカス取得時にIMEモードを変更するクラス.
     * @author hiro
     */
    private static class ImeChangeFocusedChangeListener implements ChangeListener<Boolean> {
        
        /**
         * コンストラクタ.
         * @param imeChangeRunnable
         */
        public ImeChangeFocusedChangeListener(ImeChangeRunnable imeChangeRunnable) {
            this.imeChangeRunnable = imeChangeRunnable;
        }
        
        private ImeChangeRunnable imeChangeRunnable;
        
        private boolean isCanceled = false;
        
        private Thread thread;
        
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
                if (this.thread == null || this.thread.getState() == State.TERMINATED) {
                    this.thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ImeChangeFocusedChangeListener listener = ImeChangeFocusedChangeListener.this;
                            try {
                                listener.isCanceled = false;
                                Thread.sleep(focusedImeChangeWaitTime);
                                if (listener.isCanceled == false) {
                                    listener.imeChangeRunnable.run();
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    });
                    this.thread.start();
                }
            } else {
                this.isCanceled = true;
            }
            
        }
        
    }
    
}

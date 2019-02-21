package com.hirohiro716.javafx.dialog;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * ダイアログのインターフェース.
 * @author hiro
 * @param <T> ダイアログのResultタイプ
 */
public interface InterfaceDialog<T> {

    /**
     * ダイアログのStackPaneを取得する.
     * @return StackPane
     */
    public abstract StackPane getStackPane();

    /**
     * ダイアログのPaneを取得する.
     * @return Pane
     */
    public abstract Pane getContentPane();

    /**
     * ダイアログの結果をセットする.
     * @param resultValue
     */
    public abstract void setResult(T resultValue);

    /**
     * ダイアログの結果を取得する.
     * @return 結果
     */
    public abstract T getResult();

    /**
     * ダイアログを拡大・縮小する.
     * @param scale 比率
     */
    public abstract void setScale(double scale);

    /**
     * ダイアログを表示する.
     */
    public abstract void show();
    
    /**
     * ダイアログのStackPaneをクリックした場合に閉じることができるかどうか.
     * @return 結果
     */
    public abstract boolean isClosableAtStackPaneClicked();

    /**
     * ダイアログを閉じる.
     */
    public abstract void close();
    
    /**
     * ダイアログを閉じた時のイベントをセットする.
     * @param closeEvent
     */
    public abstract void setCloseEvent(CloseEventHandler<T> closeEvent);

    /**
     * 閉じる際の処理インターフェース.
     * @author hiro
     * @param <T> ダイアログのResultタイプ
     */
    public interface CloseEventHandler<T> {

        /**
         * ダイアログを閉じる際の処理
         * @param resultValue
         */
        public void handle(T resultValue);

    }
    
}

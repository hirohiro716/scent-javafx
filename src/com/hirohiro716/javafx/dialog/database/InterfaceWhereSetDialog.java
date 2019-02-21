package com.hirohiro716.javafx.dialog.database;

import com.hirohiro716.database.WhereSet;
import com.hirohiro716.javafx.dialog.InterfaceDialog;

import javafx.scene.layout.VBox;

/**
 * WhereSet[]を作成するダイアログのインターフェース.
 * @author hiro
 */
public interface InterfaceWhereSetDialog extends InterfaceDialog<WhereSet[]> {

    /**
     * カラムのデータ型.
     * @author hiro
     */
    public enum ColumnType {
        /**
         * 文字列
         */
        STRING,
        /**
         * 文字列（数字だけ）
         */
        NUMBER_STRING,
        /**
         * 数値
         */
        NUMBER,
        /**
         * 日付
         */
        DATE,
        /**
         * 日付と時刻
         */
        DATETIME,
        /**
         * 真偽
         */
        BOOLEAN,
        ;
    }

    /**
     * WhereSetDialog.fxmlにおける左側のVBoxを取得する.
     * @return VBox
     */
    public abstract VBox getVBoxWhereSet();
    
    /**
     * WhereSetDialog.fxmlにおける右側のVBoxを取得する.
     * @return VBox
     */
    public abstract VBox getVBoxWhereSetGroup();
    
}

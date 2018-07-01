package com.hirohiro716.javafx.dialog;

import java.util.ArrayList;

import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * ダイアログを表示するための抽象クラス.
 * @author hiro
 * @param <T> ダイアログのResultタイプ
 */
public abstract class AbstractPaneDialog<T> {

    private Pane parentPane;

    /**
     * コンストラクタで指定したダイアログ表示対象Paneを取得する.
     * @return Pane
     */
    public Pane getParentPane() {
        return this.parentPane;
    }

    private StackPane dialogPane;

    /**
     * ダイアログPaneを取得する.
     * @return StackPane
     */
    public StackPane getDialogPane() {
        return this.dialogPane;
    }

    private ArrayList<Node> disableChangeNodes = new ArrayList<Node>();

    /**
     * コンストラクタでダイアログ表示対象を指定する.
     * @param parentPane ダイアログ表示対象Pane
     */
    public AbstractPaneDialog(Pane parentPane) {
        this.parentPane = parentPane;
        for (Node node: this.parentPane.getChildren()) {
            if (node.isDisabled() == false) {
                this.disableChangeNodes.add(node);
            }
        }
    }

    /**
     * ダイアログを表示する. ダイアログを表示すると親Paneは操作不可になる.
     * @param dialogContentPane ダイアログ内容
     */
    protected void show(Pane dialogContentPane) {
        // 親Pane内の子をすべて使用不可にする
        for (Node node: this.disableChangeNodes) {
            node.setDisable(true);
        }
        // StackPaneを固定
        this.dialogPane = new StackPane();
        this.dialogPane.setPrefSize(this.parentPane.getWidth(), this.parentPane.getHeight());
        this.dialogPane.setStyle("-fx-background-color: rgba(180,180,180,0.5);");
        // ダイアログの拡大縮小
        dialogContentPane.setScaleX(this.scale);
        dialogContentPane.setScaleY(this.scale);
        // ダイアログのサイズを設定する
        if (this.width == -1) {
            dialogContentPane.setMinWidth(dialogContentPane.getPrefWidth());
            dialogContentPane.setMaxWidth(dialogContentPane.getPrefWidth());
        } else {
            dialogContentPane.setMinWidth(this.width);
            dialogContentPane.setMaxWidth(this.width);
        }
        if (this.height == -1) {
            dialogContentPane.setMinHeight(dialogContentPane.getPrefHeight());
            dialogContentPane.setMaxHeight(dialogContentPane.getPrefHeight());
        } else {
            dialogContentPane.setMinHeight(this.height);
            dialogContentPane.setMaxHeight(this.height);
        }
        this.dialogPane.getChildren().add(dialogContentPane);
        // 親Paneリサイズ時にダイアログも同期
        this.parentPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AbstractPaneDialog.this.dialogPane.setPrefWidth(newValue.doubleValue());
            }
        });
        this.parentPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                AbstractPaneDialog.this.dialogPane.setPrefHeight(newValue.doubleValue());
            }
        });
        this.parentPane.getChildren().add(this.dialogPane);
    }

    /**
     * ダイアログを表示する.
     */
    public abstract void show();

    /**
     * ダイアログを閉じる.
     */
    public void close() {
        this.parentPane.getChildren().remove(this.dialogPane);
        for (Node node: this.disableChangeNodes) {
            node.setDisable(false);
        }
        if (this.closeEvent != null) {
            this.closeEvent.handle(this.getResult());
        }
    }

    private CloseEventHandler<T> closeEvent;

    /**
     * ダイアログを閉じた時のイベントをセットする.
     * @param closeEvent
     */
    public void setCloseEvent(CloseEventHandler<T> closeEvent) {
        this.closeEvent = closeEvent;
    }

    private T result;

    /**
     * ダイアログの結果をセットする.
     * @param resultValue
     */
    public void setResult(T resultValue) {
        this.result = resultValue;
    }

    /**
     * ダイアログの結果を取得する.
     * @return 結果
     */
    public T getResult() {
        return this.result;
    }

    private double scale = 1;

    /**
     * ダイアログを拡大・縮小する.
     * @param scale 比率
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    private double width = -1;

    /**
     * ダイアログの幅を変更する.
     * @param width
     */
    public void setWidth(double width) {
        this.width = width;
    }

    private double height = -1;

    /**
     * ダイアログの高さを変更する.
     * @param height
     */
    public void setHeight(double height) {
        this.height = height;
    }

}

package com.hirohiro716.javafx.dialog;

import java.util.ArrayList;

import com.hirohiro716.javafx.CSSHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Paneに対してダイアログを表示するための抽象クラス.
 * @author hiro
 * @param <T> ダイアログのResultタイプ
 */
public abstract class AbstractPaneDialog<T> implements InterfaceDialog<T> {

    private Pane parentPane;

    /**
     * コンストラクタで指定したダイアログ表示対象Paneを取得する.
     * @return Pane
     */
    public Pane getParentPane() {
        return this.parentPane;
    }

    private StackPane dialogPane = new StackPane();

    @Override
    public StackPane getStackPane() {
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
        AbstractPaneDialog<T> dialog = this;
        // 親Pane内の子をすべて使用不可にする
        for (Node node: this.disableChangeNodes) {
            node.setDisable(true);
        }
        // StackPaneを設定
        this.dialogPane.setPrefSize(this.parentPane.getWidth(), this.parentPane.getHeight());
        this.dialogPane.setStyle(CSSHelper.updateStyleValue(this.dialogPane.getStyle(), "-fx-background-color", "rgba(180,180,180,0.5)"));
        // StackPaneをクリックしたら閉じる処理
        if (this.isClosableAtStackPaneClicked()) {
            this.dialogPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    dialog.close();
                }
            });
            dialogContentPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    event.consume();
                }
            });
        }
        // ダイアログの拡大縮小
        dialogContentPane.setScaleX(this.scale);
        dialogContentPane.setScaleY(this.scale);
        // ダイアログのサイズを設定する
        dialogContentPane.setMinWidth(dialogContentPane.getPrefWidth());
        dialogContentPane.setMaxWidth(dialogContentPane.getPrefWidth());
        dialogContentPane.setMinHeight(dialogContentPane.getPrefHeight());
        dialogContentPane.setMaxHeight(dialogContentPane.getPrefHeight());
        this.dialogPane.getChildren().add(dialogContentPane);
        // 親Paneリサイズ時にダイアログも同期
        this.parentPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dialog.dialogPane.setPrefWidth(newValue.doubleValue());
            }
        });
        this.parentPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dialog.dialogPane.setPrefHeight(newValue.doubleValue());
            }
        });
        this.parentPane.getChildren().add(this.dialogPane);
    }

    @Override
    public abstract void show();

    @Override
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

    @Override
    public void setCloseEvent(CloseEventHandler<T> closeEvent) {
        this.closeEvent = closeEvent;
    }

    private T result;

    @Override
    public void setResult(T resultValue) {
        this.result = resultValue;
    }

    @Override
    public T getResult() {
        return this.result;
    }

    private double scale = 1;

    @Override
    public void setScale(double scale) {
        this.scale = scale;
    }

}

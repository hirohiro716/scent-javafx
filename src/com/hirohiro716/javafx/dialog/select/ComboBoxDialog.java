package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * コンボボックス入力ダイアログを表示するクラス.
 * @author hiro
 * @param <T> コンボボックスの値型
 */
public class ComboBoxDialog<T> extends AbstractDialog<T> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ComboBox<T> comboBox;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        ComboBoxDialog<T> dialog = this;
        // Paneの生成
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dialog.comboBox.getValue() != null) {
                    dialog.setResult(dialog.comboBox.getValue());
                    dialog.close();
                }
            }
        });
        // キーボードイベント定義
        this.getStackPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isAltDown() == false) {
                    return;
                }
                switch (event.getCode()) {
                case O:
                    dialog.buttonOk.fire();
                    break;
                case C:
                    dialog.buttonCancel.fire();
                    break;
                default:
                    break;
                }
            }
        });
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
        ComboBoxDialog<T> dialog = this;
        // コンボボックスアイテムのセット
        this.comboBox.setItems(this.items);
        // コンボボックスの初期値をセット
        if (this.defaultValue != null) {
            this.comboBox.setValue(this.defaultValue);
        }
        // キャンセル可能かどうか
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                }
            });
        } else {
            HBox hboxButton = (HBox) this.buttonCancel.getParent();
            hboxButton.getChildren().remove(this.buttonCancel);
        }
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return this.isCancelable;
    }

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.paneMessage.getChildren().clear();
        Label label = new Label(message);
        label.setWrapText(true);
        this.paneMessage.getChildren().add(label);
        LayoutHelper.setAnchor(label, 0, 0, 0, 0);
    }

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    private ObservableList<T> items;

    /**
     * コンボボックスのアイテムを指定する.
     * @param items
     */
    public void setItems(ObservableList<T> items) {
        this.items = items;
    }

    /**
     * コンボボックスのアイテムを取得する.
     * @return items
     */
    public ObservableList<T> getItems() {
        return this.items;
    }

    private T defaultValue;

    /**
     * コンボボックスの初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する. 初期値はtrue.
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

}

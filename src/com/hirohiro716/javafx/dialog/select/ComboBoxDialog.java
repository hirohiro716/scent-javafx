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
import javafx.stage.Stage;

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

    /**
     * コンストラクタ.
     */
    public ComboBoxDialog() {
        super();
    }

    /**
     * コンストラクタ.
     * @param parentStage
     */
    public ComboBoxDialog(Stage parentStage) {
        super(parentStage);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    protected void preparationCallback() {
        ComboBoxDialog<T> dialog = ComboBoxDialog.this;
        // タイトルのセット
        this.getStage().setTitle(this.title);
        this.labelTitle.setText(this.title);
        // メッセージのセット
        if (this.message != null) {
            Label label = new Label(this.message);
            label.setWrapText(true);
            this.paneMessage.getChildren().add(label);
            LayoutHelper.setAnchor(label, 0, 0, 0, 0);
        }
        // メッセージNodeのセット
        if (this.messageNode != null) {
            this.paneMessage.getChildren().add(this.messageNode);
        }
        // コンボボックスアイテムのセット
        this.comboBox.setItems(this.items);
        // コンボボックスの初期値をセット
        if (this.defaultValue != null) {
            this.comboBox.setValue(this.defaultValue);
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
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
                }
            });
        } else {
            this.buttonCancel.setVisible(false);
            LayoutHelper.setAnchor(this.buttonOk, null, 20d, 20d, null);
        }
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
    }

    @Override
    public void show() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public T showAndWait() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            return this.showAndWait(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String title;

    /**
     * タイトルをセットする.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    private String message;

    /**
     * メッセージ内容をセットする.
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    private Node messageNode;

    /**
     * メッセージに代わるNodeをセットする.
     * @param node
     */
    public void setMessageNode(Node node) {
        this.messageNode = node;
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
     * キャンセル可能かを設定する.
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * キャンセル可能かを取得する.
     * @return キャンセル可能か
     */
    public boolean isCancelable() {
        return this.isCancelable;
    }

}

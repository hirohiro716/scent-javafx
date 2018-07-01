package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;

import com.hirohiro716.StringConverter;
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
 */
public class ComboBoxDialog extends AbstractDialog<String> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ
     */
    public ComboBoxDialog() {
        super();
    }

    /**
     * コンストラクタ
     * @param parentStage
     */
    public ComboBoxDialog(Stage parentStage) {
        super(parentStage);
    }

    @Override
    protected void preparationCallback() {
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
                ComboBoxDialog.this.setResult(StringConverter.nullReplace(ComboBoxDialog.this.comboBox.getValue(), ""));
                ComboBoxDialog.this.close();
                event.consume();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ComboBoxDialog.this.setResult(null);
                    ComboBoxDialog.this.close();
                    event.consume();
                }
            });
        } else {
            this.buttonCancel.setVisible(false);
            LayoutHelper.setAnchor(this.buttonOk, null, 20d, 20d, null);
        }
        // キーボードイベント定義
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isAltDown() == false) {
                    return;
                }
                switch (event.getCode()) {
                case O:
                    ComboBoxDialog.this.buttonOk.fire();
                    event.consume();
                    break;
                case C:
                    ComboBoxDialog.this.buttonCancel.fire();
                    event.consume();
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
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("ComboBoxDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public String showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("ComboBoxDialog.fxml"), this);
            return this.showAndWait(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
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

    private ObservableList<String> items;

    /**
     * コンボボックスのアイテムを指定する.
     * @param items
     */
    public void setItems(ObservableList<String> items) {
        this.items = items;
    }

    /**
     * コンボボックスのアイテムを取得する.
     * @return items
     */
    public ObservableList<String> getItems() {
        return this.items;
    }

    private String defaultValue;

    /**
     * コンボボックスの初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
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

    /**
     * ダイアログを表示
     * @param title タイトル
     * @param message メッセージ
     * @param items コンボボックスのアイテム
     * @return 結果
     */
    public static String showAndWait(String title, String message, ObservableList<String> items) {
        ComboBoxDialog dialog = new ComboBoxDialog();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setItems(items);
        return dialog.showAndWait();
    }

    /**
     * ダイアログを表示
     * @param title タイトル
     * @param message メッセージ
     * @param items コンボボックスのアイテム
     * @param parentStage 親Stage
     * @return 結果
     */
    public static String showAndWait(String title, String message, ObservableList<String> items, Stage parentStage) {
        ComboBoxDialog dialog = new ComboBoxDialog(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setItems(items);
        return dialog.showAndWait();
    }

    @Override @Deprecated
    public void setWidth(double width) {
    }

    @Override @Deprecated
    public void setHeight(double height) {
    }

}

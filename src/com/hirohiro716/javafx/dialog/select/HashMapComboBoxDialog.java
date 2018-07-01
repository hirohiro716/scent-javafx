package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.HashMapComboBox;
import com.hirohiro716.javafx.dialog.AbstractDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * コンボボックス入力ダイアログを表示するクラス.
 * @author hiro
 * @param <K> 連想配列のキー型
 */
public class HashMapComboBoxDialog<K> extends AbstractDialog<K> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private HashMapComboBox<K, String> comboBox;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ
     */
    public HashMapComboBoxDialog() {
        super();
    }

    /**
     * コンストラクタ
     * @param parentStage
     */
    public HashMapComboBoxDialog(Stage parentStage) {
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
        this.comboBox.setHashMap(this.hashMap);
        // コンボボックスの初期値をセット
        if (this.defaultValue != null) {
            this.comboBox.setKey(this.defaultValue);
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HashMapComboBoxDialog.this.setResult(HashMapComboBoxDialog.this.comboBox.getKey());
                HashMapComboBoxDialog.this.close();
                event.consume();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    HashMapComboBoxDialog.this.setResult(null);
                    HashMapComboBoxDialog.this.close();
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
                    HashMapComboBoxDialog.this.buttonOk.fire();
                    event.consume();
                    break;
                case C:
                    HashMapComboBoxDialog.this.buttonCancel.fire();
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
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("HashMapComboBoxDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public K showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("HashMapComboBoxDialog.fxml"), this);
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

    private HashMap<K, String> hashMap;

    /**
     * コンボボックスのアイテムを指定する.
     * @param hashMap
     */
    public void setHashMap(HashMap<K, String> hashMap) {
        this.hashMap = hashMap;
    }

    /**
     * コンボボックスのアイテムを取得する.
     * @return items
     */
    public HashMap<K, String> getItems() {
        return this.hashMap;
    }

    private K defaultValue;

    /**
     * コンボボックスの初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(K defaultValue) {
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
     * @param <K> 連想配列のキー型
     * @param title タイトル
     * @param message メッセージ
     * @param hashMap コンボボックスのアイテム
     * @return 結果
     */
    public static <K> K showAndWait(String title, String message, HashMap<K, String> hashMap) {
        HashMapComboBoxDialog<K> dialog = new HashMapComboBoxDialog<>();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setHashMap(hashMap);
        return dialog.showAndWait();
    }

    /**
     * ダイアログを表示
     * @param <K> 連想配列のキー型
     * @param title タイトル
     * @param message メッセージ
     * @param hashMap コンボボックスのアイテム
     * @param parentStage 親Stage
     * @return 結果
     */
    public static <K> K showAndWait(String title, String message, HashMap<K, String> hashMap, Stage parentStage) {
        HashMapComboBoxDialog<K> dialog = new HashMapComboBoxDialog<>(parentStage);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setHashMap(hashMap);
        return dialog.showAndWait();
    }

    @Override @Deprecated
    public void setWidth(double width) {
    }

    @Override @Deprecated
    public void setHeight(double height) {
    }

}

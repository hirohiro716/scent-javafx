package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.HashMapComboBox;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * コンボボックス入力ダイアログを表示するクラス.
 * @author hiro
 * @param <K> 連想配列のキー型
 * @param <V> 連想配列の値型
 */
public class HashMapComboBoxPaneDialog<K, V> extends AbstractPaneDialog<K> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private HashMapComboBox<K, V> comboBox;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ.
     * @param parentPane
     */
    public HashMapComboBoxPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    public void show() {
        HashMapComboBoxPaneDialog<K, V> dialog = HashMapComboBoxPaneDialog.this;
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HashMapComboBoxDialog.class.getResource(HashMapComboBoxDialog.class.getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        // タイトルのセット
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
                dialog.setResult(dialog.comboBox.getKey());
                dialog.close();
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

    private HashMap<K, V> hashMap;

    /**
     * コンボボックスのアイテムを指定する.
     * @param hashMap
     */
    public void setHashMap(HashMap<K, V> hashMap) {
        this.hashMap = hashMap;
    }

    /**
     * コンボボックスのアイテムを取得する.
     * @return items
     */
    public HashMap<K, V> getItems() {
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

}

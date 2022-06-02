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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * コンボボックス入力ダイアログを表示するクラス。
 *
 * @author hiro
 * @param <K> 連想配列のキー型
 * @param <V> 連想配列の値型
 */
public class HashMapComboBoxDialog<K, V> extends AbstractDialog<K> {

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

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        HashMapComboBoxDialog<K, V> dialog = this;
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
                if (dialog.comboBox.getKey() != null) {
                    dialog.setResult(dialog.comboBox.getKey());
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
        HashMapComboBoxDialog<K, V> dialog = this;
        // コンボボックスアイテムのセット
        this.comboBox.setHashMap(this.hashMap);
        // コンボボックスの初期値をセット
        if (this.defaultValue != null) {
            this.comboBox.setKey(this.defaultValue);
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
     * メッセージ内容をセットする。
     *
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
     * メッセージに代わるNodeをセットする。
     *
     * @param node
     */
    public void setMessageNode(Node node) {
        this.paneMessage.getChildren().clear();
        this.paneMessage.getChildren().add(node);
    }

    private HashMap<K, V> hashMap;

    /**
     * コンボボックスのアイテムを指定する。
     *
     * @param hashMap
     */
    public void setHashMap(HashMap<K, V> hashMap) {
        this.hashMap = hashMap;
    }

    /**
     * コンボボックスのアイテムを取得する。
     *
     * @return items
     */
    public HashMap<K, V> getItems() {
        return this.hashMap;
    }

    private K defaultValue;

    /**
     * コンボボックスの初期値をセットする。
     *
     * @param defaultValue
     */
    public void setDefaultValue(K defaultValue) {
        this.defaultValue = defaultValue;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する. 初期値はtrue。
     *
     * @param isCancelable
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * キャンセル可能かを取得する。
     *
     * @return キャンセル可能か
     */
    public boolean isCancelable() {
        return this.isCancelable;
    }

}

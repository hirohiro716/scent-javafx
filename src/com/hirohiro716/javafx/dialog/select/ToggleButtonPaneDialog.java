package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

/**
 * HashMapの値をそれぞれToggleButtonで表示しユーザーにON/OFFを切り替えさせるダイアログを表示するクラス.
 * @author hiro
 * @param <E> 選択できるItemの型
 */
public class ToggleButtonPaneDialog<E> extends AbstractPaneDialog<LinkedHashMap<E, String>> {

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private FlowPane flowPane;
    
    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ.
     * @param selectableItems 選択できるItem
     * @param parentPane
     */
    public ToggleButtonPaneDialog(HashMap<E, String> selectableItems, Pane parentPane) {
        super(parentPane);
        this.selectableItems = selectableItems;
    }

    private HashMap<E, String> selectableItems;

    @Override
    public void show() {
        ToggleButtonPaneDialog<E> dialog = ToggleButtonPaneDialog.this;
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ToggleButtonDialog.class.getResource(ToggleButtonDialog.class.getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
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
        // 選択用のToggleButtonを作成
        HashMap<E, ToggleButton> buttonsHashMap = new HashMap<>();
        for (E key: this.selectableItems.keySet()) {
            String value = this.selectableItems.get(key);
            ToggleButton button = new ToggleButton();
            button.setText(value);
            button.setUserData(key);
            button.setOpacity(0.5);
            button.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        button.setOpacity(1);
                    } else {
                        button.setOpacity(0.5);
                    }
                }
            });
            this.flowPane.getChildren().add(button);
            buttonsHashMap.put(key, button);
        }
        // 初期値のセット
        if (this.defaultValue != null) {
            for (E key: this.defaultValue.keySet()) {
                buttonsHashMap.get(key).setSelected(true);
            }
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @SuppressWarnings("unchecked")
            @Override
            public void handle(ActionEvent event) {
                LinkedHashMap<E, String> hashMap = new LinkedHashMap<>();
                for (Node node: dialog.flowPane.getChildren()) {
                    ToggleButton button = (ToggleButton) node;
                    if (button.isSelected()) {
                        hashMap.put((E) button.getUserData(), button.getText()); 
                    }
                }
                dialog.setResult(hashMap);
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
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
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

    private HashMap<E, String> defaultValue;

    /**
     * 選択状態の初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(HashMap<E, String> defaultValue) {
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

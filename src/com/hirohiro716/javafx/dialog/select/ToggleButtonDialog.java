package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * HashMapの値をそれぞれToggleButtonで表示しユーザーにON/OFFを切り替えさせるダイアログを表示するクラス。
 *
 * @author hiro
 * @param <E> 選択できるItemの型
 */
public class ToggleButtonDialog<E> extends AbstractDialog<LinkedHashMap<E, String>> {

    @FXML
    private AnchorPane paneRoot;

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
     * コンストラクタ。
     *
     * @param selectableItems 選択できるItem
     */
    public ToggleButtonDialog(HashMap<E, String> selectableItems) {
        this.selectableItems = selectableItems;
    }
    
    private HashMap<E, String> selectableItems;
    
    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        ToggleButtonDialog<E> dialog = this;
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
        // キーボードイベント定義
        this.getStackPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
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
        return fxmlLoader.getPaneRoot();
    }

    @Override
    public void breforeShowPrepare() {
        ToggleButtonDialog<E> dialog = this;
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
            for (E key: this.defaultValue) {
                if (buttonsHashMap.containsKey(key)) {
                    buttonsHashMap.get(key).setSelected(true);
                }
            }
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

    private E[] defaultValue;

    /**
     * 選択状態の初期値をセットする。
     *
     * @param defaultValue
     */
    public void setDefaultValue(E[] defaultValue) {
        this.defaultValue = defaultValue;
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する。初期値はtrue。
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

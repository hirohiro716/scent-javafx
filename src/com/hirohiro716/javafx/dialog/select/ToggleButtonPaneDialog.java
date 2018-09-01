package com.hirohiro716.javafx.dialog.select;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
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
 * @param <E> 選択するItemの型
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
     * コンストラクタ
     * @param parentPane
     */
    public ToggleButtonPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public void show() {
        // ダイアログ表示
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource("ToggleButtonDialog.fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
            return;
        }
        ToggleButtonPaneDialog<E> dialog = ToggleButtonPaneDialog.this;
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
        Iterator<E> iterator = this.items.keySet().iterator();
        while (iterator.hasNext()) {
            E key = iterator.next();
            String value = this.items.get(key);
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
        iterator = this.defaultValue.keySet().iterator();
        while (iterator.hasNext()) {
            E key = iterator.next();
            buttonsHashMap.get(key).setSelected(true);
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
                event.consume();
            }
        });
        if (this.isCancelable) {
            this.buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    dialog.setResult(null);
                    dialog.close();
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
                switch (event.getCode()) {
                case O:
                    dialog.buttonOk.fire();
                    event.consume();
                    break;
                case C:
                    dialog.buttonCancel.fire();
                    event.consume();
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

    private HashMap<E, String> items;

    /**
     * 並び替えるアイテムを指定する.
     * @param items
     */
    public void setItems(HashMap<E, String> items) {
        this.items = items;
    }

    /**
     * 並び替えるアイテムを取得する.
     * @return items
     */
    public HashMap<E, String> getItems() {
        return this.items;
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

    /**
     * ダイアログを表示
     * @param <E> ListViewのItem型
     * @param <T> javafx.scene.layout.Paneを継承したクラスオブジェクト
     * @param title タイトル
     * @param message メッセージ
     * @param items コンボボックスのアイテム
     * @param parentPane 表示対象Pane
     * @param closeEvent 閉じる際の処理
     */
    public static <E, T extends Pane> void show(String title, String message, HashMap<E, String> items, T parentPane, CloseEventHandler<LinkedHashMap<E, String>> closeEvent) {
        ToggleButtonPaneDialog<E> dialog = new ToggleButtonPaneDialog<>(parentPane);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setItems(items);
        dialog.setCloseEvent(closeEvent);
        dialog.show();
    }

    @Override @Deprecated
    public void setWidth(double width) {
    }

    @Override @Deprecated
    public void setHeight(double height) {
    }

}
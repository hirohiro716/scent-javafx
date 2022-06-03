package com.hirohiro716.javafx.dialog.datetime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.datetime.FiscalMonth;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.javafx.dialog.alert.InstantAlert;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * 月の入力ダイアログを表示するクラス。
 *
 * @author hiro
 *
 */
public class MonthSelectDialog extends AbstractDialog<MonthResult> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private ComboBox<Integer> comboBoxYear;

    @FXML
    private ComboBox<Integer> comboBoxMonth;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ。
     */
    public MonthSelectDialog() {
        super();
    }

    /**
     * コンストラクタ。
     *
     * @param selectableYears 選択可能な年
     */
    public MonthSelectDialog(List<Integer> selectableYears) {
        super();
        this.selectableYears = selectableYears;
    }

    private List<Integer> selectableYears = null;

    @Override
    protected Label getLabelTitle() {
        return this.labelTitle;
    }

    @Override
    protected Pane createContentPane() {
        MonthSelectDialog dialog = this;
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
                try {
                    MonthResult result = new MonthResult(dialog.comboBoxYear.getValue(), dialog.comboBoxMonth.getValue());
                    dialog.setResult(result);
                    dialog.close();
                } catch (Exception exception) {
                    InstantAlert.show(dialog.getStackPane(), "正しく入力されていません。", Pos.CENTER, 3000);
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
        MonthSelectDialog dialog = this;
        // コンボボックスのアイテムをセット
        Datetime datetime = new Datetime();
        if (this.selectableYears == null) {
            this.selectableYears = new ArrayList<>();
            this.selectableYears.add(datetime.toYear());
        }
        this.comboBoxYear.setItems(FXCollections.observableArrayList(this.selectableYears));
        this.comboBoxMonth.setItems(FXCollections.observableArrayList(FiscalMonth.createLinkedHashMap().keySet()));
        // 初期値をセット
        if (this.defaultYear != null) {
            this.comboBoxYear.setValue(this.defaultYear);
        }
        if (this.defaultMonth != null) {
            this.comboBoxMonth.setValue(this.defaultMonth);
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
    
    private Integer defaultYear = null;
    
    /**
     * 初期でセットする年をセットする。
     *
     * @param year
     */
    public void setDefaultYear(int year) {
        this.defaultYear = year;
    }
    
    private Integer defaultMonth = null;
    
    /**
     * 初期でセットする月をセットする。
     *
     * @param month
     */
    public void setDefaultMonth(int month) {
        this.defaultMonth = month;
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
    }}

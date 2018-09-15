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
import javafx.stage.Stage;

/**
 * 月の入力ダイアログを表示するクラス.
 * @author hiro
 */
public class MonthSelectDialog extends AbstractDialog<MonthResult> {

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
     * コンストラクタ.
     */
    public MonthSelectDialog() {
        super();
    }

    /**
     * コンストラクタ.
     * @param selectableYears 選択可能な年
     */
    public MonthSelectDialog(List<Integer> selectableYears) {
        super();
        this.selectableYears = selectableYears;
    }

    /**
     * コンストラクタ.
     * @param parentStage
     */
    public MonthSelectDialog(Stage parentStage) {
        super(parentStage);
    }

    /**
     * コンストラクタ.
     * @param parentStage
     * @param selectableYears 選択可能な年
     */
    public MonthSelectDialog(Stage parentStage, List<Integer> selectableYears) {
        super(parentStage);
        this.selectableYears = selectableYears;
    }

    /**
     * コンストラクタ.
     * @param parentStage
     * @param selectableYears 選択可能な年
     */
    public MonthSelectDialog(Stage parentStage, int... selectableYears) {
        super(parentStage);
        this.selectableYears = new ArrayList<>();
        for (int year: selectableYears) {
            this.selectableYears.add(year);
        }
    }

    private List<Integer> selectableYears = null;
    
    @Override
    protected void preparationCallback() {
        MonthSelectDialog dialog = MonthSelectDialog.this;
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
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    MonthResult result = new MonthResult(dialog.comboBoxYear.getValue(), dialog.comboBoxMonth.getValue());
                    dialog.setResult(result);
                    dialog.close();
                } catch (Exception exception) {
                    InstantAlert.show(dialog.getDialogPane(), "正しく入力されていません。", Pos.CENTER, 3000);
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
        this.getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
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
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            this.show(fxmlHelper.getPaneRoot());
        } catch (IOException exception) {
        }
    }

    @Override
    public MonthResult showAndWait() {
        try {
            FXMLLoader fxmlHelper = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
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

    private Integer defaultYear = null;
    
    /**
     * 初期でセットする年をセットする.
     * @param year
     */
    public void setDefaultYear(int year) {
        this.defaultYear = year;
    }
    
    private Integer defaultMonth = null;
    
    /**
     * 初期でセットする月をセットする.
     * @param month
     */
    public void setDefaultMonth(int month) {
        this.defaultMonth = month;
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

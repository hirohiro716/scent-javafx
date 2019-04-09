package com.hirohiro716.javafx.dialog.datetime;

import java.io.IOException;
import java.util.Date;

import com.hirohiro716.StringConverter;
import com.hirohiro716.RegexHelper.RegexPattern;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.control.RudeDatePicker;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;
import com.hirohiro716.robot.InterfaceTypingRobotJapanese.IMEMode;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * 日時の入力ダイアログを表示するクラス.
 * @author hiro
 */
public class DatetimePickerPaneDialog extends AbstractPaneDialog<Date> {

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Label labelTitle;

    @FXML
    private AnchorPane paneMessage;

    @FXML
    private RudeDatePicker datePicker;

    @FXML
    private LimitTextField limitTextFieldHour;

    @FXML
    private LimitTextField limitTextFieldMinute;

    @FXML
    private LimitTextField limitTextFieldSecond;

    @FXML
    private EnterFireButton buttonOk;

    @FXML
    private EnterFireButton buttonCancel;

    /**
     * コンストラクタ.
     * @param parentPane
     */
    public DatetimePickerPaneDialog(Pane parentPane) {
        super(parentPane);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    public void show() {
        DatetimePickerPaneDialog dialog = this;
        // ダイアログ表示
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DatetimePickerDialog.class.getResource(DatetimePickerDialog.class.getSimpleName() + ".fxml"), this);
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
        // 日付の初期値をセット
        this.datePicker.setValue(this.defaultValue);
        this.datePicker.getEditor().setAlignment(Pos.CENTER);
        // 時刻
        if (this.isTimeInput == false) {
            this.limitTextFieldHour.setVisible(false);
            this.limitTextFieldHour.setText("0");;
            this.limitTextFieldMinute.setVisible(false);
            this.limitTextFieldMinute.setText("0");;
            this.limitTextFieldSecond.setVisible(false);
            this.limitTextFieldSecond.setText("0");;
            for (Node node: this.getStackPane().lookupAll("#colon")) {
                node.setVisible(false);
            }
        } else {
            Datetime datetime = new Datetime(this.defaultValue);
            StringConverter converter = new StringConverter();
            converter.addIntegerString();
            this.limitTextFieldHour.setMaxLength(2);
            this.limitTextFieldHour.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
            this.limitTextFieldHour.setText(String.valueOf(datetime.toHour()));
            IMEHelper.apply(this.limitTextFieldHour, IMEMode.OFF);
            this.limitTextFieldMinute.setMaxLength(2);
            this.limitTextFieldMinute.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
            this.limitTextFieldMinute.setText(String.valueOf(datetime.toMinute()));
            IMEHelper.apply(this.limitTextFieldMinute, IMEMode.OFF);
            this.limitTextFieldSecond.setMaxLength(2);
            this.limitTextFieldSecond.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
            this.limitTextFieldSecond.setText(String.valueOf(datetime.toSecond()));
            IMEHelper.apply(this.limitTextFieldSecond, IMEMode.OFF);
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dialog.datePicker.getValue() != null) {
                    Datetime helper = new Datetime(dialog.datePicker.getDate());
                    helper.modifyHour(StringConverter.stringToInteger(dialog.limitTextFieldHour.getText()));
                    helper.modifyMinute(StringConverter.stringToInteger(dialog.limitTextFieldMinute.getText()));
                    helper.modifySecond(StringConverter.stringToInteger(dialog.limitTextFieldSecond.getText()));
                    dialog.setResult(helper.getDate());
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
            HBox hboxButton = (HBox) this.buttonCancel.getParent();
            hboxButton.getChildren().remove(this.buttonCancel);
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
        // FIXME DatePickerはバグなのか開いた瞬間はフォーカスを一度外さないと選択されない
        dialog.limitTextFieldHour.requestFocus();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialog.datePicker.requestFocus();
                dialog.datePicker.getEditor().selectAll();
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

    private Date defaultValue = new Date();

    /**
     * 日時の初期値をセットする.
     * @param defaultValue
     */
    public void setDefaultValue(Date defaultValue) {
        if (defaultValue != null) {
            this.defaultValue = defaultValue;
        }
    }

    private boolean isCancelable = true;

    /**
     * キャンセル可能かを設定する. 初期値はtrue.
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

    private boolean isTimeInput = true;

    /**
     * 時刻も入力させるかどうか. 初期値はtrue.
     * @param isTimeInput
     */
    public void setTimeInput(boolean isTimeInput) {
        this.isTimeInput = isTimeInput;
    }

    /**
     * 時刻も入力させるかどうかを取得する.
     * @return isTimeInput
     */
    public boolean isTimeInput() {
        return this.isTimeInput;
    }

    @Override
    public boolean isClosableAtStackPaneClicked() {
        return this.isCancelable;
    }

}

package com.hirohiro716.javafx.dialog.datetime;

import java.io.IOException;
import java.util.Date;

import com.hirohiro716.StringConverter;
import com.hirohiro716.RegexHelper.RegexPattern;
import com.hirohiro716.InterfaceKeyInputRobotJapanese.ImeMode;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.ImeHelper;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.control.RudeDatePicker;
import com.hirohiro716.javafx.dialog.AbstractDialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 日時の入力ダイアログを表示するクラス.
 * @author hiro
 */
public class DatetimePickerDialog extends AbstractDialog<Date> {

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
     */
    public DatetimePickerDialog() {
        super();
    }

    /**
     * コンストラクタ.
     * @param parentStage
     */
    public DatetimePickerDialog(Stage parentStage) {
        super(parentStage);
    }

    @Override
    public AnchorPane getContentPane() {
        return this.paneRoot;
    }

    @Override
    protected void preparationCallback() {
        DatetimePickerDialog dialog = DatetimePickerDialog.this;
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
            converter.appendIntegerString();
            this.limitTextFieldHour.setMaxLength(2);
            this.limitTextFieldHour.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
            this.limitTextFieldHour.setText(String.valueOf(datetime.toHour()));
            ImeHelper.apply(this.limitTextFieldHour, ImeMode.OFF);
            this.limitTextFieldMinute.setMaxLength(2);
            this.limitTextFieldMinute.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
            this.limitTextFieldMinute.setText(String.valueOf(datetime.toMinute()));
            ImeHelper.apply(this.limitTextFieldMinute, ImeMode.OFF);
            this.limitTextFieldSecond.setMaxLength(2);
            this.limitTextFieldSecond.addPermitRegex(RegexPattern.INTEGER_NARROW_ONLY.getPattern(), false);
            this.limitTextFieldSecond.setText(String.valueOf(datetime.toSecond()));
            ImeHelper.apply(this.limitTextFieldSecond, ImeMode.OFF);
        }
        // ボタンのイベント定義
        this.buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (dialog.datePicker.getValue() != null) {
                    Datetime datetime = new Datetime(dialog.datePicker.getDate());
                    try {
                        datetime.modifyHour(StringConverter.stringToInteger(dialog.limitTextFieldHour.getText()));
                        datetime.modifyMinute(StringConverter.stringToInteger(dialog.limitTextFieldMinute.getText()));
                        datetime.modifySecond(StringConverter.stringToInteger(dialog.limitTextFieldSecond.getText()));
                    } catch (Exception exception) {
                    }
                    dialog.setResult(datetime.getDate());
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

    @Override
    public void show() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            this.show(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Date showAndWait() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"), this);
            return this.showAndWait(fxmlLoader.getPaneRoot());
        } catch (IOException exception) {
            exception.printStackTrace();
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

}

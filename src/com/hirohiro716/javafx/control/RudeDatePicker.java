package com.hirohiro716.javafx.control;

import java.time.LocalDate;
import java.util.Date;

import com.hirohiro716.StringConverter;
import com.hirohiro716.datetime.Datetime;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 少し乱暴な入力にも臨機応変に対応するDatePicker。
 *
 * @author hiro
 *
 */
public class RudeDatePicker extends DatePicker {

    private String lastInputText;

    /**
     * コンストラクタ。
     */
    public RudeDatePicker() {
        this(null);
    }

    /**
     * コンストラクタ。
     *
     * @param localDate
     */
    public RudeDatePicker(LocalDate localDate) {
        super(localDate);
        this.valueProperty().addListener(this.localDateChangeListener);
        this.getEditor().textProperty().addListener(this.lastInputTextChangeListener);
        this.getEditor().focusedProperty().addListener(this.focusChangeListener);
        this.addEventFilter(KeyEvent.KEY_PRESSED, this.keyEventHandler);
    }
    
    /**
     * Date型で値をセットする。
     *
     * @param date
     */
    public void setValue(Date date) {
        try {
            Datetime datetime = new Datetime(date);
            this.setValue(LocalDate.of(datetime.toYear(), datetime.toMonth(), datetime.toDay()));
        } catch (Exception exception) {
            super.setValue(null);
        }
    }
    
    private final ObjectProperty<Date> dateProperty = new SimpleObjectProperty<>();
    
    /**
     * 入力値をDate型で取得する。
     *
     * @return Date
     */
    public Date getDate() {
        return this.dateProperty.get();
    }
    
    /**
     * Date型で値をセットする。
     *
     * @param date
     */
    public void setDate(Date date) {
        this.setValue(date);
    }
    
    /**
     * 年月日をセットする。
     *
     * @param year 年(西暦)
     * @param month 月(1～12)
     * @param day 日(1～31)
     */
    public void setValue(int year, int month, int day) {
        this.setValue(LocalDate.of(year, month, day));
    }
    
    private ChangeListener<String> lastInputTextChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            RudeDatePicker picker = RudeDatePicker.this;
            picker.lastInputText = newValue;
            if (newValue != null) {
                picker.getEditor().setText(newValue.replaceAll("-", "/"));
            }
        }
    };
    
    private void convertDateString() {
        if (this.lastInputText != null) {
            String[] values = this.lastInputText.split("/");
            switch (values.length) {
            case 1:
                super.setValue(null);
                Integer day = StringConverter.stringToInteger(values[0]);
                if (day != null && day >= 1 && day <= 31) {
                    try {
                        Datetime datetime = new Datetime();
                        datetime.modifyDay(StringConverter.stringToInteger(values[0]));
                        this.setValue(LocalDate.of(datetime.toYear(), datetime.toMonth(), datetime.toDay()));
                    } catch (Exception exception) {
                    }
                }
                break;
            case 2:
                super.setValue(null);
                try {
                    Datetime datetime = new Datetime();
                    datetime.modifyMonth(StringConverter.stringToInteger(values[0]));
                    datetime.modifyDay(StringConverter.stringToInteger(values[1]));
                    this.setValue(LocalDate.of(datetime.toYear(), datetime.toMonth(), datetime.toDay()));
                } catch (Exception exception) {
                }
                break;
            case 3:
                break;
            default:
                super.setValue(null);
                break;
            }
        }
    }
    
    private ChangeListener<LocalDate> localDateChangeListener = new ChangeListener<LocalDate>() {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
            RudeDatePicker picker = RudeDatePicker.this;
            picker.dateProperty.set(null);
            if (newValue != null) {
                Datetime datetime = new Datetime(new Date(0));
                datetime.modifyYear(newValue.getYear());
                datetime.modifyMonth(newValue.getMonthValue());
                datetime.modifyDay(newValue.getDayOfMonth());
                picker.dateProperty.set(datetime.getDate());
            }
        }
    };
    
    private EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                RudeDatePicker picker = RudeDatePicker.this;
                picker.convertDateString();
            }
        }
    };
    
    private ChangeListener<Boolean> focusChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            RudeDatePicker picker = RudeDatePicker.this;
            if (newValue == false) {
                picker.convertDateString();
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        picker.getEditor().selectAll();
                    }
                });
            }
        }
    };
}

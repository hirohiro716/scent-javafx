package test.com.hirohiro716.javafx;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.print.PrintException;

import com.hirohiro716.StringConverter;
import com.hirohiro716.awt.RobotJapanese.ImeMode;
import com.hirohiro716.RudeArray;
import com.hirohiro716.RegexHelper;
import com.hirohiro716.RegexHelper.RegexPattern;
import com.hirohiro716.database.WhereSet;
import com.hirohiro716.javafx.FXMLLoader;
import com.hirohiro716.javafx.IMEHelper;
import com.hirohiro716.javafx.LayoutHelper;
import com.hirohiro716.javafx.PaneNodeFinder;
import com.hirohiro716.javafx.StageBuilder;
import com.hirohiro716.javafx.barcode.Jan13Helper;
import com.hirohiro716.javafx.barcode.NW7Helper;
import com.hirohiro716.javafx.control.AutoCompleteTextField;
import com.hirohiro716.javafx.control.AutoCompleteTextField;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.HashMapComboBox;
import com.hirohiro716.javafx.control.LimitComboBox;
import com.hirohiro716.javafx.control.LimitTextArea;
import com.hirohiro716.javafx.control.LimitTextField;
import com.hirohiro716.javafx.control.RudeDatePicker;
import com.hirohiro716.javafx.control.table.EditableTable;
import com.hirohiro716.javafx.control.table.EditableTable.ControlFactory;
import com.hirohiro716.javafx.control.table.EditableTable.FixControlFactory;
import com.hirohiro716.javafx.control.table.EditableTable.ReadOnlyControlFactory;
import com.hirohiro716.javafx.dialog.AbstractDialog.CloseEventHandler;
import com.hirohiro716.javafx.dialog.AbstractDialog;
import com.hirohiro716.javafx.dialog.AbstractPaneDialog;
import com.hirohiro716.javafx.dialog.DialogResult;
import com.hirohiro716.javafx.dialog.alert.Alert;
import com.hirohiro716.javafx.dialog.alert.AlertPane;
import com.hirohiro716.javafx.dialog.confirm.Confirm;
import com.hirohiro716.javafx.dialog.confirm.ConfirmPane;

import com.hirohiro716.javafx.dialog.database.WhereSetDialog;
import com.hirohiro716.javafx.dialog.database.WhereSetDialog.ColumnType;
import com.hirohiro716.javafx.dialog.database.WhereSetPaneDialog;
import com.hirohiro716.javafx.dialog.datetime.DatetimePickerDialog;
import com.hirohiro716.javafx.dialog.datetime.DatetimePickerPaneDialog;
import com.hirohiro716.javafx.dialog.select.ComboBoxDialog;
import com.hirohiro716.javafx.dialog.select.ComboBoxPaneDialog;
import com.hirohiro716.javafx.dialog.select.ListViewDialog;
import com.hirohiro716.javafx.dialog.select.ListViewPaneDialog;
import com.hirohiro716.javafx.dialog.sort.SortDialog;
import com.hirohiro716.javafx.dialog.sort.SortPaneDialog;
import com.hirohiro716.javafx.dialog.text.LimitTextAreaDialog;
import com.hirohiro716.javafx.dialog.text.LimitTextAreaPaneDialog;
import com.hirohiro716.javafx.dialog.text.LimitTextFieldDialog;
import com.hirohiro716.javafx.dialog.text.LimitTextFieldPaneDialog;
import com.hirohiro716.javafx.dialog.wait.WaitDialog;
import com.hirohiro716.javafx.dialog.wait.WaitPaneDialog;
import com.hirohiro716.javafx.print.AbstractPrintingPaneBuilder;
import com.hirohiro716.javafx.print.AbstractPrintingPaneBuilder.CanvasCallback;
import com.hirohiro716.javafx.print.PrinterJob;
import com.hirohiro716.thread.Task;
import com.sun.glass.ui.Robot;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.print.JobSettings;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PaperSource;
import javafx.print.PrintColor;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@SuppressWarnings("all")
public class Test extends Application {

    private static Stage STAGE;

    @Override
    public void start(Stage stage) throws Exception {
        StageBuilder stageBuilder = new StageBuilder(stage, getClass().getResource("Test.fxml"));
        stageBuilder.show();
        Test.STAGE = stage;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void initialize() {

        this.comboBox.setEditable(true);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                STAGE.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon16.png")));
                STAGE.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon32.png")));
                STAGE.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon48.png")));
                STAGE.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon64.png")));
                STAGE.getIcons().add(new Image(AbstractDialog.class.getResourceAsStream("icon128.png")));
            }
        });

        button.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Test test = Test.this;

                AbstractPrintingPaneBuilder builder = new AbstractPrintingPaneBuilder() {
                    @Override
                    public void build() throws PrintException {
                        this.setTextOriginVPos(VPos.TOP);
                        this.printText("前代未聞", 0, 0);

                        this.printRectangleLine(30, 30, 40, 0);
                        this.printTextRight("前代未聞", 30, 40, 0);
                        this.printTextRight("0", 30, 40, 5);
                        this.printTextRight("000", 30, 40, 10);
                        this.printTextCenter("前代未聞", 30, 40, 15);
                        this.printTextCenter("0", 30, 40, 20);
                        this.printTextCenter("000", 30, 40, 25);

                        this.setColorString("#f0f");
                        this.printText("テスト", 0, 40, "");
                        this.setColorString("#0f0");
                        this.printRectangleFill(30, 20, 50, 50, 10);
                        this.setStrokeWidth(0.5);


//                        this.setTextLineSpacing(-2);
                        this.setFont(new Font("メイリオ", 50));
                        this.printWrapTextAccordingToFrame("あいうえおかきくけこ", 50, 60, 130, 40, null);
                        this.printRectangleLine(50, 60, 130, 40);


                        this.setStrokeDashArray(3, 0.5, 1, 0.5);
                        this.printRectangleLine(30, 20, 100, 100, 5);

                        this.printRectangleLine(50, 20, 10, 170, "");

                        this.printEllipseLine(50, 20, 10, 170, null);
                        this.printEllipseLine(50, 20, 110, 170, null);

                        this.printEllipseFill(50, 20, 10, 200);
                        this.printEllipseFill(50, 20, 110, 200);

                        this.printHorizonalLine(5, 200, 100);
                        this.printVerticalLine(5, 200, 10);


                        this.printImageWithFitWidth(new Image("file:C:/Users/hiro/Desktop/test.jpg"), 50, 20, 240);
                        this.printImageWithFitHeight(new Image("file:C:/Users/hiro/Desktop/test.jpg"), 30, 100, 240);

                        this.printCanvas(100, 50, 20, 150, new CanvasCallback() {
                            @Override
                            public void call(Canvas canvas) {
                            }
                        });

                        this.printNW7("a444949948063a", 55, 11, 90, 5, 2);

                    }

                };


                try {
//                    PrinterJob job = new PrinterJob("Microsoft Print to PDF");
//                    job.setPaperSource(PaperSource.MANUAL);
//                    PrinterJob job = new PrinterJob("FX DocuCentre-V C2275");
//                    job.setPaperSource(PaperSource.AUTOMATIC);
                    PrinterJob job = new PrinterJob("PX-105");
                    job.setMargin(0, 0, 0, 0);
                    job.appendPage(builder);
                    job.print();
                } catch (PrintException e) {
                    e.printStackTrace();
                }

                STAGE.close();

            }
        });
        
        
        this.paneRoot.setStyle("-fx-background:#fff;");
        
        
        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < 1000; i++) {
            items.add(i + "test");
        }
        AutoCompleteTextField autoCompleteTextField = new AutoCompleteTextField(items);
        
        this.paneRoot.getChildren().add(autoCompleteTextField);
        
        
//        paneRoot.setDisable(true);
        
        button2.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                
            }
        });

        rudeDatePicker.setValue(new Date());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rudeDatePicker.requestFocus();

            }

        });

    }

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private AutoCompleteTextField autoCompleteTextField;

    @FXML
    private RudeDatePicker rudeDatePicker;

    @FXML
    private Button button;

    @FXML
    private EnterFireButton button2;

    @FXML
    private ComboBox<Integer> comboBox;

}

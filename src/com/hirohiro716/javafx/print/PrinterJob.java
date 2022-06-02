package com.hirohiro716.javafx.print;

import static com.hirohiro716.StringConverter.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.print.PrintException;

import com.hirohiro716.print.PrintHelper;
import com.sun.javafx.print.Units;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.print.JobSettings;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PaperSource;
import javafx.print.PrintColor;
import javafx.print.PrintQuality;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * AbstractPrintingPaneBuilderを印刷するクラス。
 *
 * @author hiro
 */
public class PrinterJob {

    /**
     * "印刷失敗" というダイアログタイトル用の文字列。
     */
    public static final String ERROR_DIALOG_TITLE_PRINT = "印刷失敗";

    /**
     * すべてのプリンタを取得する。
     *
     * @return すべてのプリンタ
     */
    public static ObservableSet<Printer> fetchAllPrinters() {
        return Printer.getAllPrinters();
    }
    
    /**
     * すべてのプリンタ名を取得する。
     *
     * @return すべてのプリンタの名前
     */
    public static ObservableList<String> fetchAllPrinterNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Printer printer: fetchAllPrinters()) {
            list.add(printer.getName());
        }
        return list;
    }

    private Printer printer;

    /**
     * 印刷に使用するプリンタを取得する。
     *
     * @return 印刷用プリンタ
     */
    public Printer getPrinter() {
        return this.printer;
    }

    /**
     * コンストラクタ デフォルトプリンタを使用する。
     *
     * @throws NullPointerException デフォルトプリンタが設定されていない場合
     */
    public PrinterJob() throws NullPointerException {
        this.printer = Printer.getDefaultPrinter();
        if (this.printer == null) {
            throw new NullPointerException("Default printer is not found.");
        }
    }

    /**
     * コンストラクタ プリンタを指定する。
     *
     * @param printer
     * @throws NullPointerException
     */
    public PrinterJob(Printer printer) {
        this.printer = printer;
        if (this.printer == null) {
            throw new NullPointerException("Printer is not found.");
        }
    }

    /**
     * コンストラクタ 名前でプリンタを指定する。
     *
     * @param printerName
     * @throws NullPointerException 指定されてプリンタが存在しない場合
     */
    public PrinterJob(String printerName) {
        for (Printer printer: Printer.getAllPrinters()) {
            if (printer.getName().equals(printerName)) {
                this.printer = printer;
            }
        }
        if (this.printer == null) {
            throw new NullPointerException(printerName + ":Printer is not found.");
        }
    }

    private String jobName = "JavaFX print job";

    /**
     * 印刷ジョブ名を指定する。
     *
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    
    /**
     * プリンタでサポートしている用紙トレイを取得する。
     *
     * @return サポートされている用紙トレイ
     */
    public Set<PaperSource> fetchSupportedPaperSources() {
        return this.printer.getPrinterAttributes().getSupportedPaperSources();
    }

    /**
     * プリンタでサポートしている用紙トレイ名を取得する。
     *
     * @return サポートされている用紙トレイ名
     */
    public ObservableList<String> fetchSupportedPaperSourceNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (PaperSource paperSource: this.fetchSupportedPaperSources()) {
            list.add(paperSource.getName());
        }
        return list;
    }

    private PaperSource paperSource = null;

    /**
     * 印刷に使用する用紙トレイを指定する。
     *
     * @param paperSource
     */
    public void setPaperSource(PaperSource paperSource) {
        this.paperSource = paperSource;
    }

    /**
     * 印刷に使用する用紙トレイを指定する。
     *
     * @param paperSourceName
     */
    public void setPaperSource(String paperSourceName) {
        for (PaperSource paperSource: this.printer.getPrinterAttributes().getSupportedPaperSources()) {
            if (paperSource.getName().equals(paperSourceName)) {
                this.paperSource = paperSource;
            }
        }
    }

    /**
     * プリンタでサポートしている用紙を取得する。
     *
     * @return サポートされている用紙
     */
    public ObservableList<Paper> fetchSupportedPapers() {
        ObservableList<Paper> list = FXCollections.observableArrayList();
        for (Paper paper: this.printer.getPrinterAttributes().getSupportedPapers()) {
            list.add(paper);
        }
        return list;
    }

    /**
     * プリンタでサポートしている用紙名を取得する。
     *
     * @return サポートされている用紙名
     */
    public ObservableList<String> fetchSupportedPaperNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Paper paper: this.fetchSupportedPapers()) {
            list.add(paper.getName());
        }
        return list;
    }

    private Paper paper = null;

    /**
     * 印刷に使用する用紙を指定する。
     *
     * @param paper 用紙種類
     */
    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    /**
     * 印刷に使用する用紙を設定する。
     *
     * @param paperName 用紙名
     */
    public void setPaper(String paperName) {
        for (Paper paper: this.printer.getPrinterAttributes().getSupportedPapers()) {
            if (paper.getName().equals(paperName)) {
                this.paper = paper;
            }
        }
    }
    
    /**
     * 印刷に使用する用紙を設定する。
     *
     * @param width 用紙幅
     * @param height 用紙高さ
     */
    public void setPaper(double width, double height) {
        this.paper = this.createPaper(width, height);
    }
    
    /**
     * 指定サイズの用紙を作成する。
     *
     * @param width 用紙幅
     * @param height 用紙高さ
     * @return Paper
     */
    private Paper createPaper(double width, double height) {
        for (Paper paper: this.fetchSupportedPapers()) {
            if (paper.getWidth() == width && paper.getHeight() == height) {
                return paper;
            }
        }
        double millimeterWidth = PrintHelper.pointToMillimeter(width);
        double millimeterHeight = PrintHelper.pointToMillimeter(height);
        String paperName = join(Math.round(millimeterWidth), "x", Math.round(millimeterHeight), "mm");
        return com.sun.javafx.print.PrintHelper.createPaper(paperName, width, height, Units.POINT);
    }

    /**
     * 印刷に使用する用紙を設定する。
     *
     * @param millimeterWidth 用紙幅
     * @param millimeterHeight 用紙高さ
     */
    public void setPaperMillimeter(double millimeterWidth, double millimeterHeight) {
        this.paper = this.createPaperMillimeter(millimeterWidth, millimeterHeight);
    }

    /**
     * 指定サイズの用紙を作成する。
     *
     * @param millimeterWidth 用紙幅
     * @param millimeterHeight 用紙高さ
     * @return Paper
     */
    private Paper createPaperMillimeter(double millimeterWidth, double millimeterHeight) {
        for (Paper paper: this.fetchSupportedPapers()) {
            double millimeterPaperWidth = PrintHelper.pointToMillimeter(paper.getWidth());
            double millimeterPaperHeight = PrintHelper.pointToMillimeter(paper.getHeight());
            double differenceWidth = Math.abs(millimeterPaperWidth - millimeterWidth);
            double differenceHeight = Math.abs(millimeterPaperHeight - millimeterHeight);
            if (differenceWidth <= 1 && differenceHeight <= 1) {
                return paper;
            }
        }
        String paperName = join(Math.round(millimeterWidth), "x", Math.round(millimeterHeight), "mm");
        return com.sun.javafx.print.PrintHelper.createPaper(paperName, millimeterWidth, millimeterHeight, Units.MM);
    }
    
    /**
     * プリンタでサポートしている用紙向きを取得する。
     *
     * @return サポートされている用紙向き
     */
    public Set<PageOrientation> fetchSupportedPageOrientations() {
        return this.printer.getPrinterAttributes().getSupportedPageOrientations();
    }

    private PageOrientation pageOrientation = null;

    /**
     * 印刷に使用する用紙向きを指定する。
     *
     * @param pageOrientation 向き
     */
    public void setPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    /**
     * 印刷に使用する用紙向きを指定する。
     *
     * @param pageOrientation 向き
     */
    public void setPageOrientation(com.hirohiro716.print.PageOrientation pageOrientation) {
        if (pageOrientation == null) {
            this.pageOrientation = null;
            return;
        }
        switch (pageOrientation) {
        case PORTRAIT:
            this.pageOrientation = PageOrientation.PORTRAIT;
            break;
        case LANDSCAPE:
            this.pageOrientation = PageOrientation.LANDSCAPE;
            break;
        case REVERSE_PORTRAIT:
            this.pageOrientation = PageOrientation.REVERSE_PORTRAIT;
            break;
        case REVERSE_LANDSCAPE:
            this.pageOrientation = PageOrientation.REVERSE_LANDSCAPE;
            break;
        }
    }
    
    private double leftMargin = 0;
    
    private double topMargin = 0;

    /**
     * 印刷に使用する余白を指定する。
     *
     * @param left
     * @param top
     */
    public void setMargin(double left, double top) {
        this.leftMargin = left;
        this.topMargin = top;
    }

    /**
     * 印刷に使用する余白を指定する。
     *
     * @param millimeterLeft
     * @param millimeterTop
     */
    public void setMarginMillimeter(double millimeterLeft, double millimeterTop) {
        this.leftMargin = PrintHelper.millimeterToPoint(millimeterLeft);
        this.topMargin = PrintHelper.millimeterToPoint(millimeterTop);
    }

    private int copies = 1;

    /**
     * 印刷部数を指定する。
     *
     * @param copies
     */
    public void setCopies(int copies) {
        this.copies = copies;
    }

    private PrintColor printColor = null;

    /**
     * カラーモードを指定する。
     *
     * @param printColor
     */
    public void setPrintColor(PrintColor printColor) {
        this.printColor = printColor;
    }

    private PrintQuality printQuality = null;

    /**
     * 印刷品質を指定する。
     *
     * @param printQuality
     */
    public void setPrintQuality(PrintQuality printQuality) {
        this.printQuality = printQuality;
    }

    private PrintSides printSides = null;

    /**
     * 両面印刷モードを指定する。
     *
     * @param printSides
     */
    public void setPrintSides(PrintSides printSides) {
        this.printSides = printSides;
    }
    
    private javafx.print.PrinterJob printerJob = null;
    
    /**
     * 印刷ジョブを開始する。
     *
     * @throws PrintException 印刷ジョブの作成に失敗した場合
     */
    public void start() throws PrintException {
        if (this.printerJob != null) {
            throw new PrintException("PrinterJob is already started. Create new instance please.");
        }
        this.printerJob = javafx.print.PrinterJob.createPrinterJob(this.printer);
        JobSettings jobSettings = this.printerJob.getJobSettings();
        jobSettings.setJobName(this.jobName);
        jobSettings.setCopies(this.copies);
        // 用紙
        if (this.paper == null) {
            this.paper = this.printer.getPrinterAttributes().getDefaultPaper();
        }
        if (this.pageOrientation == null) {
            this.pageOrientation = this.printer.getPrinterAttributes().getDefaultPageOrientation();
        }
        switch (this.pageOrientation) {
        case PORTRAIT:
        case REVERSE_PORTRAIT:
            break;
        case LANDSCAPE:
        case REVERSE_LANDSCAPE:
            this.paper = this.createPaper(this.paper.getHeight(), this.paper.getWidth());
            break;
        }
        jobSettings.setPageLayout(this.printer.createPageLayout(this.paper, PageOrientation.PORTRAIT, 0, 0, 0, 0));
        // トレイ
        if (this.paperSource != null) {
            jobSettings.setPaperSource(this.paperSource);
        }
        // カラーモード
        if (this.printColor != null) {
            jobSettings.setPrintColor(this.printColor);
        }
        // 品質
        if (this.printQuality != null) {
            jobSettings.setPrintQuality(this.printQuality);
        }
        // 両面印刷
        if (this.printSides != null) {
            jobSettings.setPrintSides(this.printSides);
        }
    }
    
    private HashMap<Pane, ArrayList<Transform>> hashMapBackupTransforms = new HashMap<>();
    
    /**
     * 余白と用紙向きをTransformで実現する。
     *
     * @param pane 対象
     */
    private void applyMarginAndOrientation(Pane pane) {
        ArrayList<Transform> transforms = new ArrayList<>();
        transforms.addAll(pane.getTransforms());
        this.hashMapBackupTransforms.put(pane, transforms);
        double paperWidth = this.paper.getWidth();
        double paperHeight = this.paper.getHeight();
        Rotate rotate;
        Translate translate;
        switch (this.pageOrientation) {
        case PORTRAIT:
            translate = new Translate(this.leftMargin, this.topMargin);
            pane.getTransforms().add(translate);
            break;
        case LANDSCAPE:
            rotate = new Rotate(90, 0, 0);
            translate = new Translate(0 + this.leftMargin, paperWidth * -1 + this.topMargin);
            pane.getTransforms().addAll(rotate, translate);
            break;
        case REVERSE_PORTRAIT:
            rotate = new Rotate(180, 0, 0);
            translate = new Translate(paperWidth * -1 + this.leftMargin, paperHeight * -1 + this.topMargin);
            pane.getTransforms().addAll(rotate, translate);
            break;
        case REVERSE_LANDSCAPE:
            rotate = new Rotate(270, 0, 0);
            translate = new Translate(paperHeight * -1 + this.leftMargin, 0 + this.topMargin);
            pane.getTransforms().addAll(rotate, translate);
            break;
        }
    }
    
    /**
     * 余白と用紙向きのTransformを取り除く。
     *
     * @param pane 対象
     */
    private void removeMarginAndOrientation(Pane pane) {
        pane.getTransforms().clear();
        pane.getTransforms().addAll(this.hashMapBackupTransforms.get(pane));
    }
    
    /**
     * ページを印刷する。
     *
     * @param pane
     * @throws PrintException 印刷ジョブの作成に失敗した場合
     */
    public void print(Pane pane) throws PrintException {
        if (this.printerJob == null) {
            throw new PrintException("PrinterJob is not started.");
        }
        this.applyMarginAndOrientation(pane);
        this.printerJob.printPage(pane);
        this.removeMarginAndOrientation(pane);
    }

    /**
     * ページを印刷する。このメソッド内でAbstractPrintingPaneBuilderのbuildが自動で呼ばれる。
     *
     * @param page AbstractPrintingPaneBuilderを継承したクラスのインスタンス
     * @throws PrintException 印刷ジョブの作成に失敗した場合
     */
    public void print(AbstractPrintingPaneBuilder page) throws PrintException {
        if (this.printerJob == null) {
            throw new PrintException("PrinterJob is not started.");
        }
        page.build();
        this.applyMarginAndOrientation(page.getPane());
        this.printerJob.printPage(page.getPane());
        this.removeMarginAndOrientation(page.getPane());
    }
    
    /**
     * 印刷ジョブを終了する。
     */
    public void end() {
        this.printerJob.endJob();
    }}

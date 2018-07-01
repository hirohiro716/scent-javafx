package com.hirohiro716.javafx.print;

import java.util.ArrayList;
import java.util.Set;

import javax.print.PrintException;

import com.hirohiro716.PrintHelper;

import javafx.collections.ObservableSet;
import javafx.print.JobSettings;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PaperSource;
import javafx.print.PrintColor;
import javafx.print.PrintQuality;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javafx.print.Printer.MarginType;
import javafx.scene.layout.Pane;

/**
 * AbstractPrintingPaneBuilderを印刷するクラス.
 * @author hiro
 */
public class PrinterJob {

    /**
     * すべてのプリンタを取得する.
     * @return すべてのプリンタ
     */
    public static ObservableSet<Printer> fetchAllPrinters() {
        return Printer.getAllPrinters();
    }

    private Printer printer;

    /**
     * 印刷に使用するプリンタを取得する.
     * @return 印刷用プリンタ
     */
    public Printer getPrinter() {
        return this.printer;
    }

    /**
     * コンストラクタ デフォルトプリンタを使用する.
     * @throws NullPointerException デフォルトプリンタが設定されていない場合
     */
    public PrinterJob() throws NullPointerException {
        this.printer = Printer.getDefaultPrinter();
        if (this.printer == null) {
            throw new NullPointerException();
        }
    }

    /**
     * コンストラクタ プリンタを指定する.
     * @param printer
     * @throws NullPointerException
     */
    public PrinterJob(Printer printer) {
        this.printer = printer;
        if (this.printer == null) {
            throw new NullPointerException();
        }
    }

    /**
     * コンストラクタ 名前でプリンタを指定する.
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
            throw new NullPointerException();
        }
    }

    private String jobName = "JavaFX print job";

    /**
     * 印刷ジョブ名を指定する.
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    private ArrayList<Pane> printingPaneList = new ArrayList<>();

    /**
     * 印刷するページを追加する.
     * @param pane
     */
    public void appendPage(Pane pane) {
        this.printingPaneList.add(pane);
    }

    /**
     * 印刷するページを追加する. このメソッド内でAbstractPrintingPaneBuilderのbuildが自動で呼ばれる.
     * @param page AbstractPrintingPaneBuilderを継承したクラスのインスタンス
     * @throws PrintException 
     */
    public void appendPage(AbstractPrintingPaneBuilder page) throws PrintException {
        page.build();
        this.printingPaneList.add(page.getPane());
    }

    /**
     * プリンタでサポートしている用紙トレイを取得する.
     * @return サポートされている用紙トレイ
     */
    public Set<PaperSource> fetchSupportedPaperSources() {
        return this.printer.getPrinterAttributes().getSupportedPaperSources();
    }

    private PaperSource paperSource = null;

    /**
     * 印刷に使用する用紙トレイを指定する.
     * @param paperSource
     */
    public void setPaperSource(PaperSource paperSource) {
        this.paperSource = paperSource;
    }

    /**
     * 印刷に使用する用紙トレイを指定する.
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
     * プリンタでサポートしている用紙を取得する.
     * @return サポートされている用紙
     */
    public Set<Paper> fetchSupportedPapers() {
        return this.printer.getPrinterAttributes().getSupportedPapers();
    }

    private Paper paper = null;

    /**
     * 印刷に使用する用紙を指定する.
     * @param paper 用紙種類
     */
    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    /**
     * 印刷に使用する用紙を設定する.
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
     * プリンタでサポートしている用紙向きを取得する.
     * @return サポートされている用紙向き
     */
    public Set<PageOrientation> fetchSupportedPageOrientations() {
        return this.printer.getPrinterAttributes().getSupportedPageOrientations();
    }

    private PageOrientation pageOrientation = null;

    /**
     * 印刷に使用する用紙向きを指定する.
     * @param pageOrientation 向き
     */
    public void setPageOrientation(PageOrientation pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    private MarginType marginType = null;

    /**
     * 印刷に使用する余白タイプを指定する.
     * @param marginType
     */
    public void setMarginType(MarginType marginType) {
        if (marginType == null) {
            return;
        }
        this.leftMargin = null;
        this.rightMargin = null;
        this.topMargin = null;
        this.bottomMargin = null;
        this.marginType = marginType;
    }

    private Double leftMargin = null;
    private Double rightMargin = null;
    private Double topMargin = null;
    private Double bottomMargin = null;

    /**
     * 印刷に使用する余白を指定する.
     * @param left
     * @param right
     * @param top
     * @param bottom
     */
    public void setMargin(double left, double right, double top, double bottom) {
        this.leftMargin = left;
        this.rightMargin = right;
        this.topMargin = top;
        this.bottomMargin = bottom;
    }

    /**
     * 印刷に使用する余白を指定する.
     * @param millimeterLeft
     * @param millimeterRight
     * @param millimeterTop
     * @param millimeterBottom
     */
    public void setMarginMillimeter(double millimeterLeft, double millimeterRight, double millimeterTop, double millimeterBottom) {
        this.leftMargin = PrintHelper.millimeterToPoint(millimeterLeft);
        this.rightMargin = PrintHelper.millimeterToPoint(millimeterRight);
        this.topMargin = PrintHelper.millimeterToPoint(millimeterTop);
        this.bottomMargin = PrintHelper.millimeterToPoint(millimeterBottom);
    }

    private int copies = 1;

    /**
     * 印刷部数を指定する.
     * @param copies
     */
    public void setCopies(int copies) {
        this.copies = copies;
    }

    private PrintColor printColor = null;

    /**
     * カラーモードを指定する.
     * @param printColor
     */
    public void setPrintColor(PrintColor printColor) {
        this.printColor = printColor;
    }

    private PrintQuality printQuality = null;

    /**
     * 印刷品質を指定する.
     * @param printQuality
     */
    public void setPrintQuality(PrintQuality printQuality) {
        this.printQuality = printQuality;
    }

    private PrintSides printSides = null;

    /**
     * 両面印刷モードを指定する.
     * @param printSides
     */
    public void setPrintSides(PrintSides printSides) {
        this.printSides = printSides;
    }

    /**
     * 印刷を実行する.
     * @throws PrintException 印刷ジョブの作成に失敗した場合
     */
    public void print() throws PrintException {
        try {
            javafx.print.PrinterJob printerJob = javafx.print.PrinterJob.createPrinterJob(this.printer);
            JobSettings jobSettings = printerJob.getJobSettings();
            jobSettings.setJobName(this.jobName);
            jobSettings.setCopies(this.copies);
            // 用紙
            if (this.paper == null) {
                this.paper = this.printer.getPrinterAttributes().getDefaultPaper();
            }
            if (this.pageOrientation == null) {
                this.pageOrientation = this.printer.getPrinterAttributes().getDefaultPageOrientation();
            }
            if (this.marginType != null) {
                jobSettings.setPageLayout(this.printer.createPageLayout(this.paper, this.pageOrientation, this.marginType));
            }
            if (this.leftMargin != null) {
                jobSettings.setPageLayout(this.printer.createPageLayout(this.paper, this.pageOrientation, this.leftMargin, this.rightMargin, this.topMargin, this.bottomMargin));
            }
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
            for (Pane pane: this.printingPaneList) {
                printerJob.printPage(pane);
            }
            printerJob.endJob();
        } catch (Exception exception) {
            throw new PrintException(exception);
        }
    }

}

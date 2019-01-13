package com.hirohiro716.javafx.control.table;

import java.text.NumberFormat;
import java.util.Date;

import com.hirohiro716.StringConverter;
import com.hirohiro716.datetime.Datetime;
import com.hirohiro716.RudeArray;
import com.hirohiro716.javafx.control.EnterFireButton;
import com.hirohiro716.javafx.control.table.cell.ButtonTableCell;
import com.hirohiro716.javafx.control.table.cell.EnterFireButtonTableCell;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * 動的にカラムを生成できるTableViewクラス.
 * @author hiro
 */
public class DynamicTableView extends TableView<RudeArray> {

    /**
     * コンストラクタ.
     */
    public DynamicTableView() {
        super();
        this.setPlaceholder(new Label("データがありません"));
    }

    /**
     * コンストラクタ.
     * @param items リストItems
     */
    public DynamicTableView(ObservableList<RudeArray> items) {
        super(items);
        this.setPlaceholder(new Label("データがありません"));
    }

    /**
     * TableViewのItemオブジェクトから行番号を取得する.
     * @param target 対象オブジェクト
     * @return 行インデックス
     */
    public int getRowIndex(RudeArray target) {
        int index = -1;
        for (RudeArray row: this.getItems()) {
            index++;
            if (target == row) {
                break;
            }
        }
        return index;
    }

    /**
     * columnNameから対象カラムを取得する.
     * @param id ID（columnName）
     * @return TableColumn
     */
    public TableColumn<RudeArray, ?> getColumn(String id) {
        for (TableColumn<RudeArray, ?> column : this.getColumns()) {
            if (id.equals(column.getId())) {
                return column;
            }
        }
        return null;
    }

    /**
     * カラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param cellDataFeaturesCallback セルの値のプロパティを生成するコールバック
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Object> createColumn(String columnName, String columnHeaderText, double prefWidth,
            Callback<TableColumn.CellDataFeatures<RudeArray, Object>, ObservableValue<Object>> cellDataFeaturesCallback) {
        TableColumn<RudeArray, Object> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(cellDataFeaturesCallback);
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * カラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param cellDataFeaturesCallback セルの値のプロパティを生成するコールバック
     * @param tableCellCallback セルを生成するコールバック
     */
    public void addColumn(String columnName, String columnHeaderText,
            Callback<TableColumn.CellDataFeatures<RudeArray, Object>, ObservableValue<Object>> cellDataFeaturesCallback,
                Callback<TableColumn<RudeArray, Object>, TableCell<RudeArray, Object>> tableCellCallback) {
        this.addColumn(columnName, columnHeaderText, -1, cellDataFeaturesCallback, tableCellCallback);
    }

    /**
     * カラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth 初期列幅
     * @param cellDataFeaturesCallback セルの値のプロパティを生成するコールバック
     * @param tableCellCallback セルを生成するコールバック
     */
    public void addColumn(String columnName, String columnHeaderText, double prefWidth,
            Callback<TableColumn.CellDataFeatures<RudeArray, Object>, ObservableValue<Object>> cellDataFeaturesCallback,
                Callback<TableColumn<RudeArray, Object>, TableCell<RudeArray, Object>> tableCellCallback) {
        TableColumn<RudeArray, Object> column = createColumn(columnName, columnHeaderText, prefWidth, cellDataFeaturesCallback);
        column.setCellFactory(tableCellCallback);
        this.getColumns().add(column);
    }

    /**
     * String型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, String> createColumnString(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, String> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<RudeArray, String> param) {
                String value = param.getValue().getString(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleObjectProperty<String>(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * String型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     */
    public void addColumnString(String columnName, String columnHeaderText, Pos pos) {
        this.addColumnString(columnName, columnHeaderText, -1, pos);
    }

    /**
     * String型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth 初期列幅
     * @param pos セルの文字配置
     */
    public void addColumnString(String columnName, String columnHeaderText, double prefWidth, Pos pos) {
        TableColumn<RudeArray, String> column = createColumnString(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray, String>, TableCell<RudeArray, String>>() {
            @Override
            public TableCell<RudeArray, String> call(TableColumn<RudeArray, String> param) {
                TableCell<RudeArray, String> cell = new TableCell<RudeArray, String>() {
                    @Override
                    protected void updateItem(String item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            this.setText(item);
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText(row.getString(columnName));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Number型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Number> createColumnNumber(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Number> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray,Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(CellDataFeatures<RudeArray, Number> param) {
                Number value = param.getValue().getNumber(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleObjectProperty<Number>(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * Number型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     * @param numberFormat 数値の表示形式
     */
    public void addColumnNumber(String columnName, String columnHeaderText, Pos pos, NumberFormat numberFormat) {
        this.addColumnNumber(columnName, columnHeaderText, -1, pos, numberFormat);
    }

    /**
     * Number型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     * @param numberFormat 数値の表示形式
     */
    public void addColumnNumber(String columnName, String columnHeaderText, double prefWidth, Pos pos, NumberFormat numberFormat) {
        TableColumn<RudeArray, Number> column = createColumnNumber(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray, Number>, TableCell<RudeArray, Number>>() {
            @Override
            public TableCell<RudeArray, Number> call(TableColumn<RudeArray, Number> param) {
                TableCell<RudeArray, Number> cell = new TableCell<RudeArray, Number>() {
                    @Override
                    protected void updateItem(Number item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(numberFormat.format(item));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText(numberFormat.format(row.get(columnName)));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Integer型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Integer> createColumnInteger(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Integer> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray,Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(CellDataFeatures<RudeArray, Integer> param) {
                Integer value = param.getValue().getInteger(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleObjectProperty<Integer>(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * Integer型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     */
    public void addColumnInteger(String columnName, String columnHeaderText, Pos pos) {
        this.addColumnInteger(columnName, columnHeaderText, -1, pos);
    }

    /**
     * Integer型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     */
    public void addColumnInteger(String columnName, String columnHeaderText, double prefWidth, Pos pos) {
        TableColumn<RudeArray, Integer> column = createColumnInteger(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray, Integer>, TableCell<RudeArray, Integer>>() {
            @Override
            public TableCell<RudeArray, Integer> call(TableColumn<RudeArray, Integer> param) {
                TableCell<RudeArray, Integer> cell = new TableCell<RudeArray, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(String.valueOf(item));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText((row.getString(columnName)));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Long型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Long> createColumnLong(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Long> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray,Long>, ObservableValue<Long>>() {
            @Override
            public ObservableValue<Long> call(CellDataFeatures<RudeArray, Long> param) {
                Long value = param.getValue().getLong(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleObjectProperty<Long>(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * Long型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     */
    public void addColumnLong(String columnName, String columnHeaderText, Pos pos) {
        this.addColumnLong(columnName, columnHeaderText, -1, pos);
    }

    /**
     * Long型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     */
    public void addColumnLong(String columnName, String columnHeaderText, double prefWidth, Pos pos) {
        TableColumn<RudeArray, Long> column = createColumnLong(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray, Long>, TableCell<RudeArray, Long>>() {
            @Override
            public TableCell<RudeArray, Long> call(TableColumn<RudeArray, Long> param) {
                TableCell<RudeArray, Long> cell = new TableCell<RudeArray, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(String.valueOf(item));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText((row.getString(columnName)));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Double型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Double> createColumnDouble(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Double> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray,Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(CellDataFeatures<RudeArray, Double> param) {
                Double value = param.getValue().getDouble(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleObjectProperty<Double>(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * Double型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     */
    public void addColumnDouble(String columnName, String columnHeaderText, Pos pos) {
        this.addColumnDouble(columnName, columnHeaderText, -1, pos);
    }

    /**
     * Double型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     */
    public void addColumnDouble(String columnName, String columnHeaderText, double prefWidth, Pos pos) {
        TableColumn<RudeArray, Double> column = createColumnDouble(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray, Double>, TableCell<RudeArray, Double>>() {
            @Override
            public TableCell<RudeArray, Double> call(TableColumn<RudeArray, Double> param) {
                TableCell<RudeArray, Double> cell = new TableCell<RudeArray, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(StringConverter.tryNonFraction(String.valueOf(item)));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText(StringConverter.tryNonFraction(String.valueOf(row.getString(columnName))));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Boolean型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Boolean> createColumnBoolean(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Boolean> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray,Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<RudeArray, Boolean> param) {
                Boolean value = param.getValue().getBoolean(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleBooleanProperty(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * Boolean型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     */
    public void addColumnBoolean(String columnName, String columnHeaderText) {
        this.addColumnBoolean(columnName, columnHeaderText, -1);
    }

    /**
     * Boolean型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     */
    public void addColumnBoolean(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Boolean> column = createColumnBoolean(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray, Boolean>, TableCell<RudeArray, Boolean>>() {
            @Override
            public TableCell<RudeArray, Boolean> call(TableColumn<RudeArray, Boolean> param) {
                TableCell<RudeArray, Boolean> cell = new TableCell<RudeArray, Boolean>() {
                    @Override
                    public void updateItem(Boolean item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null && item) {
                                this.setText("●");
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            Boolean isEnabled = row.getBoolean(columnName);
                            if (isEnabled != null && isEnabled) {
                                this.setText("●");
                            }
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Date型を格納するカラムを作成する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @return TableColumn
     */
    private static TableColumn<RudeArray, Date> createColumnDate(String columnName, String columnHeaderText, double prefWidth) {
        TableColumn<RudeArray, Date> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RudeArray,Date>, ObservableValue<Date>>() {
            @Override
            public ObservableValue<Date> call(CellDataFeatures<RudeArray, Date> param) {
                Date value = param.getValue().getDate(columnName);
                if (value == null) {
                    return null;
                }
                return new SimpleObjectProperty<>(value);
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        return column;
    }

    /**
     * Date型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     */
    public void addColumnDatetime(String columnName, String columnHeaderText, Pos pos) {
        this.addColumnDatetime(columnName, columnHeaderText, -1, pos);
    }

    /**
     * Date型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     */
    public void addColumnDatetime(String columnName, String columnHeaderText, double prefWidth, Pos pos) {
        TableColumn<RudeArray, Date> column = createColumnDate(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Date>, TableCell<RudeArray,Date>>() {
            @Override
            public TableCell<RudeArray, Date> call(TableColumn<RudeArray, Date> param) {
                TableCell<RudeArray, Date> cell = new TableCell<RudeArray, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(Datetime.dateToString(item));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText(Datetime.dateToString(row.getDate(columnName)));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Date型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     */
    public void addColumnDate(String columnName, String columnHeaderText, Pos pos) {
        this.addColumnDate(columnName, columnHeaderText, -1, pos);
    }

    /**
     * Date型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     */
    public void addColumnDate(String columnName, String columnHeaderText, double prefWidth, Pos pos) {
        TableColumn<RudeArray, Date> column = createColumnDate(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Date>, TableCell<RudeArray,Date>>() {
            @Override
            public TableCell<RudeArray, Date> call(TableColumn<RudeArray, Date> param) {
                TableCell<RudeArray, Date> cell = new TableCell<RudeArray, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(Datetime.dateToString(item, "yyyy/MM/dd"));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText(Datetime.dateToString(row.getDate(columnName), "yyyy/MM/dd"));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Date型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param pos セルの文字配置
     * @param dateFormatPattern 日付を文字列に変換するパターン
     */
    public void addColumnDatetime(String columnName, String columnHeaderText, Pos pos, String dateFormatPattern) {
        this.addColumnDatetime(columnName, columnHeaderText, -1, pos, dateFormatPattern);
    }

    /**
     * Date型のカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param pos セルの文字配置
     * @param dateFormatPattern 日付を文字列に変換するパターン
     */
    public void addColumnDatetime(String columnName, String columnHeaderText, double prefWidth, Pos pos, String dateFormatPattern) {
        TableColumn<RudeArray, Date> column = createColumnDate(columnName, columnHeaderText, prefWidth);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Date>, TableCell<RudeArray,Date>>() {
            @Override
            public TableCell<RudeArray, Date> call(TableColumn<RudeArray, Date> param) {
                TableCell<RudeArray, Date> cell = new TableCell<RudeArray, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        this.setText(null);
                        if (isEmpty == false) {
                            if (item != null) {
                                this.setText(Datetime.dateToString(item, dateFormatPattern));
                            }
                        }
                    }
                    @Override
                    public void cancelEdit() {
                        super.cancelEdit();
                        this.setText(null);
                        if (this.getTableRow() != null && this.getTableRow().getItem() != null) {
                            RudeArray row = (RudeArray) this.getTableRow().getItem();
                            if (row.get(columnName) != null) {
                                this.setText(Datetime.dateToString(row.getDate(columnName), dateFormatPattern));
                            }
                        }
                    }
                };
                cell.setAlignment(pos);
                return cell;
            }
        });
        this.getColumns().add(column);
    }

    /**
     * Buttonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     */
    public void addColumnButton(String columnName, String columnHeaderText, String buttonText, EventHandler<ActionEvent> actionEvent) {
        this.addColumnButton(columnName, columnHeaderText, -1, buttonText, actionEvent);
    }

    /**
     * Buttonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     */
    public void addColumnButton(String columnName, String columnHeaderText, double prefWidth, String buttonText, EventHandler<ActionEvent> actionEvent) {
        TableColumn<RudeArray, Void> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Void>, TableCell<RudeArray,Void>>() {
            @Override
            public TableCell<RudeArray, Void> call(TableColumn<RudeArray, Void> param) {
                ButtonTableCell<RudeArray> cell = new ButtonTableCell<RudeArray>(buttonText, actionEvent);
                return cell;
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        this.getColumns().add(column);
    }

    /**
     * Buttonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     * @param callback ボタンに対する処理
     */
    public void addColumnButton(String columnName, String columnHeaderText, String buttonText, EventHandler<ActionEvent> actionEvent, NodeCallback<Button, Void> callback) {
        this.addColumnButton(columnName, columnHeaderText, -1, buttonText, actionEvent);
    }

    /**
     * Buttonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     * @param callback ボタンに対する処理
     */
    public void addColumnButton(String columnName, String columnHeaderText, double prefWidth, String buttonText, EventHandler<ActionEvent> actionEvent, NodeCallback<Button, Void> callback) {
        TableColumn<RudeArray, Void> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Void>, TableCell<RudeArray,Void>>() {
            @Override
            public TableCell<RudeArray, Void> call(TableColumn<RudeArray, Void> param) {
                ButtonTableCell<RudeArray> cell = new ButtonTableCell<RudeArray>(buttonText, actionEvent);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        callback.call(cell.getButton(), cell);
                    }
                });
                return cell;
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        this.getColumns().add(column);
    }

    /**
     * EnterFireButtonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     */
    public void addColumnEnterFireButton(String columnName, String columnHeaderText, String buttonText, EventHandler<ActionEvent> actionEvent) {
        this.addColumnEnterFireButton(columnName, columnHeaderText, -1, buttonText, actionEvent);
    }

    /**
     * EnterFireButtonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     */
    public void addColumnEnterFireButton(String columnName, String columnHeaderText, double prefWidth, String buttonText, EventHandler<ActionEvent> actionEvent) {
        TableColumn<RudeArray, Void> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Void>, TableCell<RudeArray,Void>>() {
            @Override
            public TableCell<RudeArray, Void> call(TableColumn<RudeArray, Void> param) {
                EnterFireButtonTableCell<RudeArray> cell = new EnterFireButtonTableCell<RudeArray>(buttonText, actionEvent);
                return cell;
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        this.getColumns().add(column);
    }

    /**
     * EnterFireButtonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     * @param callback ボタンに対する処理
     */
    public void addColumnEnterFireButton(String columnName, String columnHeaderText, String buttonText, EventHandler<ActionEvent> actionEvent, NodeCallback<EnterFireButton, Void> callback) {
        this.addColumnEnterFireButton(columnName, columnHeaderText, -1, buttonText, actionEvent, callback);
    }

    /**
     * EnterFireButtonカラムを追加する.
     * @param columnName カラム名
     * @param columnHeaderText ヘッダーテキスト
     * @param prefWidth カラム幅
     * @param buttonText ボタンテキスト
     * @param actionEvent ボタン押下イベント
     * @param callback ボタンに対する処理
     */
    public void addColumnEnterFireButton(String columnName, String columnHeaderText, double prefWidth, String buttonText, EventHandler<ActionEvent> actionEvent, NodeCallback<EnterFireButton, Void> callback) {
        TableColumn<RudeArray, Void> column = new TableColumn<>(columnHeaderText);
        column.setId(columnName);
        column.setCellFactory(new Callback<TableColumn<RudeArray,Void>, TableCell<RudeArray,Void>>() {
            @Override
            public TableCell<RudeArray, Void> call(TableColumn<RudeArray, Void> param) {
                EnterFireButtonTableCell<RudeArray> cell = new EnterFireButtonTableCell<RudeArray>(buttonText, actionEvent);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        callback.call(cell.getEnterFireButton(), cell);
                    }
                });
                return cell;
            }
        });
        if (prefWidth > -1) {
            column.setPrefWidth(prefWidth);
        }
        this.getColumns().add(column);
    }

    /**
     * CellFactory内で生成されたCell内のNodeに対する処理を呼び出し元で定義させるクラス.
     * @author hiro
     * @param <T> Nodeタイプ
     * @param <V> 値のタイプ
     */
    public static interface NodeCallback<T extends Node, V> {

        /**
         * 呼び出し元で定義されたNodeに対する処理を実行する.
         * @param node
         * @param cell
         */
        public void call(T node, TableCell<RudeArray, V> cell);

    }

}

package com.example.nnknotepad;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;

public class UiController {
    @FXML
    private TextArea textArea;
    @FXML
    private Label rowLabel;
    @FXML
    private Label colLabel;
    @FXML
    private Label encodeLabel;
    public String path = "";
    public Api api = new Api();
    public UndoStack SnapShotStack = new UndoStack();
    public UndoStack UnSnapShotStack = new UndoStack();
    public UndoStack tempStack = new UndoStack();
    private static boolean isCtrlOperation = false;
    private static boolean isOpenFileOperation = false;
    private static boolean isSave = true;

    private void clearTextArea() {
        textArea.clear();
        path = "";
        isSave = true;
        SnapShotStack.clear();
    }

    @FXML
    protected void openNewFile() {
        isOpenFileOperation = true;
        if (!isSave) {
            int confirmSave = api.showSaveConfirmationDialog();
            switch (confirmSave) {
                case 0 -> {
                    saveFile();
                    clearTextArea();
                }
                case 1 -> clearTextArea();
            }
        } else {
            clearTextArea();
        }
        isOpenFileOperation = false;
    }

    @FXML
    protected void openFile() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = chooser.showOpenDialog(null);
        if (selectedFile != null) {
            isOpenFileOperation = true;
            String content = api.readFile(selectedFile);
            path = selectedFile.getAbsolutePath();
            textArea.setText(content);
            isSave = true;
            isOpenFileOperation = false;
        }
    }

    @FXML
    protected boolean saveFile() {
        isSave = true;
        if (path.isEmpty()) {
            return saveFileAs();
        } else {
            String content = textArea.getText();
            return api.writeFile(new File(path), content);
        }
    }

    @FXML
    protected void textAreaWatcher(KeyEvent event) {
        if (event.isControlDown()) {
            isCtrlOperation = true;
            switch (event.getCode()) {
                case S -> {
                    saveFile();
                    isCtrlOperation = false;
                }
                case F, R -> {
                    openFindReplaceDialog();
                    isCtrlOperation = false;
                }
                case Z -> {
                    UnSnapShotStack.push(tempStack.pop());
                    String oldText = SnapShotStack.pop();
                    if (oldText != null) {
                        textArea.setText(oldText);
                    }
                    isCtrlOperation = false;
                }
                case Q -> {
                    //重做功能
                    String newText = UnSnapShotStack.pop();
                    if (newText != null) {
                        textArea.setText(newText);
                    }
                    isCtrlOperation = false;
                }
            }

        }


    }

    @FXML
    protected boolean saveFileAs() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = chooser.showSaveDialog(null);
        if (selectedFile != null) {
            String content = textArea.getText();
            path = selectedFile.getAbsolutePath();
            return api.writeFile(selectedFile, content);
        }
        return false;
    }

    @FXML
    public void initialize() {
        // 添加文本变化监听器，以实时更新光标位置和文本编码格式
        textArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            int row = api.getRow(newValue.intValue(), textArea);
            int column = api.getColumn(newValue.intValue(), textArea);
            rowLabel.setText("行" + row);
            colLabel.setText("列" + column);
            encodeLabel.setText("编码" + api.getTextEncoding(textArea));
        });
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isCtrlOperation && !isOpenFileOperation) {
                SnapShotStack.push(oldValue);
                tempStack.push(newValue);
                isSave = false;
            }
        });
    }
    @FXML
    public void openSettings(){
        SetUi.openSetting(textArea);
    }

    @FXML
    public void openFindReplaceDialog() {
        var ref = new Object() {
            double xOffset = 0;
            double yOffset = 0;
        };
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);

        // 创建查找文本框、替换文本框和按钮
        TextField findField = new TextField();
        findField.setPromptText("Find String");
        TextField replaceField = new TextField();
        replaceField.setPromptText("Replace String");
        VBox vbox = getVbox(findField, replaceField, stage);
        vbox.setStyle("-fx-padding: 10px; -fx-background-color: pink;");

        // 创建一个场景并将布局添加到场景中
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();

        // 使窗口可拖拽
        vbox.setOnMousePressed(event -> {
            ref.xOffset = event.getSceneX();
            ref.yOffset = event.getSceneY();
        });

        vbox.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - ref.xOffset);
            stage.setY(event.getScreenY() - ref.yOffset);
        });
    }

    private VBox getVbox(TextField findField, TextField replaceField, Stage stage) {
        Button findButton = new Button("Find");
        Button replaceButton = new Button("Replace");
        Button repalceSingleButton = new Button("ReplaceSingle");
        Button exitButton = new Button("Exit");
        Text indexText = new Text("0/0");
        Button precedingLine = new Button("preLine");
        Button nextLine = new Button("nextLine");
        HBox buttonBox = new HBox(10, findButton, replaceButton, exitButton);
        HBox controlInfoBox = new HBox(10, indexText, precedingLine, nextLine);
        VBox textBox = new VBox(5);
        var ref = new Object() {
            ArrayList<Integer> indexList = new ArrayList<>();
            int nowPage = 0;
            int totalPage = 0;
        };

        // 创建并设置标签
        Label findLabel = new Label("Find:      ");
        Label replaceLabel = new Label("Replace:");
        findLabel.getStyleClass().add("label");
        replaceLabel.getStyleClass().add("label");

        // 将标签与输入框组合并添加到 textBox 中
        textBox.getChildren().addAll(new HBox(5, findLabel, findField), new HBox(5, replaceLabel, replaceField));

        // 设置按钮样式
        findButton.getStyleClass().add("action-button");
        replaceButton.getStyleClass().add("action-button");
        exitButton.getStyleClass().add("exit-button");
        precedingLine.getStyleClass().add("nav-button");
        nextLine.getStyleClass().add("nav-button");

        // 设置文本样式
        indexText.getStyleClass().add("index-text");

        // 设置布局的样式和间距
        textBox.setPadding(new Insets(10));
        controlInfoBox.setPadding(new Insets(5));
        buttonBox.setPadding(new Insets(10));
        textBox.getStyleClass().add("text-box");
        controlInfoBox.getStyleClass().add("control-info-box");
        buttonBox.getStyleClass().add("button-box");

        // 设置按钮点击事件处理程序
        findButton.setOnAction(event -> {
            ref.indexList = api.handleFindButtonClick(textArea.getText(), findField.getText());
            ref.totalPage = ref.indexList.size();
            if (ref.totalPage != 0) {
                ref.nowPage = 1;
                textArea.positionCaret(ref.indexList.get(0));
                indexText.setText(ref.nowPage + "/" + ref.totalPage);
            } else {
                ref.nowPage = 0;
            }
            textArea.getScene().getWindow().requestFocus();
        });
        replaceButton.setOnAction(event -> {
            api.handleReplaceButtonClick(replaceField.getText(), textArea, findField.getText());
            findField.setText("");
            replaceField.setText("");
            textArea.getScene().getWindow().requestFocus();
        });
        precedingLine.setOnAction(event -> {
            if (ref.nowPage > 1) {
                ref.nowPage--;
                textArea.positionCaret(ref.indexList.get(ref.nowPage - 1));
                indexText.setText(ref.nowPage + "/" + ref.totalPage);
            }
            textArea.getScene().getWindow().requestFocus();
        });
        nextLine.setOnAction(event -> {
            if (ref.nowPage < ref.totalPage) {
                ref.nowPage++;
                textArea.positionCaret(ref.indexList.get(ref.nowPage - 1));
                indexText.setText(ref.nowPage + "/" + ref.totalPage);
            }
            textArea.getScene().getWindow().requestFocus();
        });
        repalceSingleButton.setOnAction(event -> {
            api.handleReplaceSingleButtonClick(replaceField.getText(), textArea, findField.getText(), ref.nowPage);
            findField.setText("");
            replaceField.setText("");
            textArea.getScene().getWindow().requestFocus();
        });
        exitButton.setOnAction(event -> stage.close());

        // 创建一个垂直布局并添加控件
        return new VBox(10, textBox, controlInfoBox, buttonBox);
    }


}
package com.example.nnknotepad;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SetUi {

    public static void openSetting(TextArea textArea) {
        // 创建设置窗口
        Stage settingStage = new Stage();
        settingStage.initModality(Modality.APPLICATION_MODAL);
        settingStage.setTitle("Text Area Settings");

        // 创建字体样式选择框
        ChoiceBox<String> fontFamilyChoiceBox = new ChoiceBox<>();
        fontFamilyChoiceBox.getItems().addAll("Arial", "Times New Roman", "Courier New");
        fontFamilyChoiceBox.setValue(textArea.getFont().getFamily());

        // 创建字体大小选择框
        ChoiceBox<Integer> fontSizeChoiceBox = new ChoiceBox<>();
        fontSizeChoiceBox.getItems().addAll(10, 12, 14, 16, 18);
        fontSizeChoiceBox.setValue((int)textArea.getFont().getSize());

        // 创建字体颜色选择框
        ChoiceBox<String> fontColorChoiceBox = new ChoiceBox<>();
        fontColorChoiceBox.getItems().addAll("Black", "Red", "Blue");
        fontColorChoiceBox.setValue("Black");

        // 创建 Apply 和 Cancel 按钮
        Button applyButton = new Button("Apply");
        applyButton.setOnAction(event -> {
            // 应用设置
            textArea.setStyle("-fx-font-family: '" + fontFamilyChoiceBox.getValue() + "';"
                    + "-fx-font-size: " + fontSizeChoiceBox.getValue() + "px;"
                    + "-fx-text-fill: " + fontColorChoiceBox.getValue().toLowerCase() + ";");
            settingStage.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> settingStage.close());

        // 创建布局并添加控件
        GridPane gridPane = new GridPane();
        gridPane.addRow(0, new Label("Font Family:"), fontFamilyChoiceBox);
        gridPane.addRow(1, new Label("Font Size:"), fontSizeChoiceBox);
        gridPane.addRow(2, new Label("Font Color:"), fontColorChoiceBox);

        HBox buttonBox = new HBox(10, applyButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, gridPane, buttonBox);
        layout.setPadding(new Insets(10));
        // 创建场景并设置到窗口
        Scene scene = new Scene(layout);
        settingStage.setScene(scene);
        settingStage.showAndWait(); // 等待窗口关闭
    }
}

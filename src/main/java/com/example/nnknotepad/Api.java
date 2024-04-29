package com.example.nnknotepad;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Api {

    // 获取光标所在行
    public int getRow(int caretPosition, TextArea textArea) {
        return textArea.getText().substring(0, caretPosition).split("\n", -1).length;
    }

    // 获取光标所在列
    public int getColumn(int caretPosition, TextArea textArea) {
        String text = textArea.getText();
        int lineStart = text.lastIndexOf('\n', caretPosition - 1); // 上一行的起始位置（包含换行符）
        return caretPosition - lineStart;
    }

    // 获取文本编码格式
    public String getTextEncoding(TextArea textArea) {
        Charset encoding = StandardCharsets.UTF_8; // 默认编码为UTF-8
        byte[] bytes = textArea.getText().getBytes();
        if (bytes.length >= 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
            encoding = StandardCharsets.UTF_8; // UTF-8 with BOM
        }
        return encoding.displayName();
    }


    public String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
                content.append("\n");

            }
        } catch (FileNotFoundException err) {
            err.printStackTrace();
        }
        return content.toString();
    }

    public boolean writeFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            return true;
        } catch (IOException err) {
            err.printStackTrace();
        }
        return false;
    }

    public ArrayList<Integer> handleFindButtonClick(String parent, String sub) {
        int index = parent.indexOf(sub);
        ArrayList<Integer> list = new ArrayList<>();
        while (index >= 0) {
            list.add(index);
            index = parent.indexOf(sub, index + 1);
        }
        return list;
    }

    public void handleReplaceButtonClick(String replaceString, TextArea textArea, String findString) {
        textArea.setText(textArea.getText().replace(findString, replaceString));
    }

    public void handleReplaceSingleButtonClick(String replaceString, TextArea textArea, String findString, int count) {
        String parent = textArea.getText();
        int index = -1;
        int foundCount = 0;
        int startIndex = 0;

        while (foundCount < count) {
            index = parent.indexOf(findString, startIndex);
            if (index != -1) {
                foundCount++;
                startIndex = index + findString.length();
            } else {
                break;
            }
        }

        if (index != -1) {
            String newText = parent.substring(0, index) + replaceString + parent.substring(index + findString.length());
            textArea.setText(newText);
        }
    }
    public int showSaveConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Confirmation");
        alert.setHeaderText("The text has been modified.");
        alert.setContentText("Do you want to save the changes?");

        ButtonType saveButton = new ButtonType("Save");
        ButtonType noSaveButton = new ButtonType("Don't Save");
        ButtonType cancelButton = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(saveButton, noSaveButton, cancelButton);

        // 显示对话框并等待用户的选择
        ButtonType result = alert.showAndWait().orElse(cancelButton);

        // 根据用户的选择返回相应的布尔值
        if (result == saveButton) {
            return 0; // 保存
        } else if (result == noSaveButton) {
            return 1; // 不保存
        } else {
            return 2; // 取消
        }
    }











}

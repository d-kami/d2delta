package net.d_kami.d2delta.view;

import javafx.stage.Popup;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseButton;

import javafx.scene.input.Dragboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.util.Callback;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

import java.io.File;
import java.io.IOException;

public class FileTableView extends TableView<File>{
    private TableColumn<File, String> nameColumn;
    private TableColumn<File, String> modifyColumn;
    private TableColumn<File, String> typeColumn;
    private TableColumn<File, String> sizeColumn;
    
    private static final DateTimeFormatter MODIFY_FOMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    
    public FileTableView(){
        super();
        
        nameColumn = FileTableView.createNameColumn();
        modifyColumn = FileTableView.createModifyColumn();
        typeColumn = FileTableView.createTypeColumn();
        sizeColumn = FileTableView.createSizeColumn();
        
        getColumns().addAll(nameColumn, modifyColumn, typeColumn, sizeColumn);
        setRowFactory(tv -> createFileRow());
    }
    
    private static TableRow<File> createFileRow(){
        TableRow<File> row = new TableRow<>();

        row.setOnMouseClicked(event -> {
            if(!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                File file = row.getItem();

                if(file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")){
                    showImagePopup(file, row);
                }
            }
        });
        
        return row;
    }
    
    private static TableColumn<File, String> createNameColumn(){
        TableColumn<File, String> nameColumn = new TableColumn<>("名前");
        
        nameColumn.setCellValueFactory(new Callback<CellDataFeatures<File, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(CellDataFeatures<File, String> param){
                File file = param.getValue();
                String name = file.getAbsolutePath();
                
                return new SimpleStringProperty(name);
            }
        });
        
        nameColumn.setCellFactory(column -> {
            return new TableCell<File, String>(){
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if(empty || item == null){
                        setText(null);
                        setGraphic(null);
                        
                        return;
                    }
                    
                    File file = new File(item);
                    setText(file.getName());
                    setGraphic(FileIcon.createFileIcon(file));
                }
            };
        });
        
        return nameColumn;
    }
    
    private static TableColumn<File, String> createModifyColumn(){
        TableColumn<File, String> modifyColumn = new TableColumn<>("更新日時");
        
        modifyColumn.setCellValueFactory(new Callback<CellDataFeatures<File, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(CellDataFeatures<File, String> param){
                String modifyTime = "";
                
                try{
                    FileTime time = Files.getLastModifiedTime(param.getValue().toPath());
                    LocalDateTime date = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
                
                    modifyTime = MODIFY_FOMATTER.format(date);
                }catch(IOException e){
                    e.printStackTrace();
                }

                return new SimpleStringProperty(modifyTime);
            }
        });
        
        return modifyColumn;
    }
    
    private static TableColumn<File, String> createTypeColumn(){
        TableColumn<File, String> typeColumn = new TableColumn<>("種類");
        
        typeColumn.setCellValueFactory(new Callback<CellDataFeatures<File, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(CellDataFeatures<File, String> param){
                File file = param.getValue();
                
                if(file.isFile()){
                    return new SimpleStringProperty(FileTableView.getSuffix(file.getName()));
                }else{
                    return new SimpleStringProperty("");
                }
            }
        });
        
        return typeColumn;
    }
    
    private static TableColumn<File, String> createSizeColumn(){
        TableColumn<File, String> sizeColumn = new TableColumn<>("サイズ");
        
        String[] suffix = {"B", "KB", "MB", "GB"};
        
        sizeColumn.setCellValueFactory(new Callback<CellDataFeatures<File, String>, ObservableValue<String>>(){
            @Override
            public ObservableValue<String> call(CellDataFeatures<File, String> param){
                long size = param.getValue().length();
                String display = "";;
                
                for(int i = 0; i < 4; i++){
                    long presize = size / 1024;
                    
                    if(presize == 0){
                        display = Long.toString(size) + suffix[i];
                        break;
                    }
                    
                    size = presize;
                }
                
                return new SimpleStringProperty(display);
            }
        });
        
        return sizeColumn;
    }
    
    private static void showImagePopup(File file, TableRow<File> row){
        Popup popup = new Popup();

        ImageView image = new ImageView(new Image(file.toURI().toString()));
        image.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.PRIMARY){
                popup.hide();
            }
        });

        image.maxHeight(600);
        image.maxWidth(600);

        popup.getContent().addAll(image);
        popup.show(row, 5, 5);
    }
    
    private static String getSuffix(String fileName) {
        if (fileName == null){
            return "";
        }
        
        int point = fileName.lastIndexOf(".");
        
        if (point == -1) {
            return "";
        }else{
            return fileName.substring(point + 1);
        }
    }
    
    private void setDropAction(){
        setOnDragOver(event -> {
            if(event.getGestureSource() != this && event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.MOVE);
            }
            
            event.consume();
        });
        
        setOnDragDropped(event -> {
            Dragboard drag = event.getDragboard();

            if(drag.hasFiles()){
                drag.getFiles().forEach(file -> {

                });
            }
            
            event.setDropCompleted(true);
            event.consume();
        });
    }
}

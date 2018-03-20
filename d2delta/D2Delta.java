import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeCell;

import javafx.scene.Group;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import java.awt.Desktop;

import java.io.File;

import javafx.embed.swing.SwingNode;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.SwingUtilities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import net.d_kami.d2delta.view.FileIcon;
import net.d_kami.d2delta.view.FileTreeItem;
import net.d_kami.d2delta.view.FileTableView;

public class D2Delta extends Application {
    private Desktop desktop;
    private static FileSystemView FILE_VIEW = FileSystemView.getFileSystemView();
    
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) throws Exception {
        desktop = Desktop.getDesktop();
        
        FileTreeItem rootItem = new FileTreeItem();
        TreeView<File> tree = new TreeView<File>(rootItem);
        tree.setPrefWidth(160.0);
        
        setCellFactory(tree);
        setRoot(tree, rootItem);
        
        FileTableView table = new FileTableView();
        
        tree.setOnMouseClicked(event -> {
            TreeItem<File> item = tree.getSelectionModel().selectedItemProperty().get();
            
            if(item == null){
                return;
            }
            
            File dir = item.getValue();
            File[] children = dir.listFiles();
            ObservableList<File> fileList = FXCollections.observableArrayList();
            
            if(children != null){
                Arrays.sort(children, (f1, f2) -> {
                    if(f1.isDirectory() && f2.isDirectory()){
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }else if(f1.isDirectory() && !f2.isDirectory()){
                        return -1;
                    }else if(!f1.isDirectory() && f2.isDirectory()){
                        return 1;
                    }else{
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                });
                
                for(File file : children){
                    fileList.add(file);
                }
            }
            
            table.setItems(fileList);
        });
        
        BorderPane pane = new BorderPane();
        
        pane.setLeft(tree);
        pane.setCenter(table);
        
        Scene scene = new Scene(pane, 480, 320);
        stage.setScene(scene);
        stage.show();
    }
    
    private void setRoot(TreeView tree, FileTreeItem rootItem){
        File[] roots = File.listRoots();
        
        for(File root : roots){
            FileTreeItem item = new FileTreeItem(root);
            rootItem.getChildren().add(item);
        }
        
        tree.setShowRoot(false);
    }

    private void setCellFactory(TreeView<File> tree){
        tree.setCellFactory(tv -> {
            return new TreeCell<File>() {
                @Override
                protected void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if(empty || item == null){
                        setText(null);
                        setGraphic(null);
                    }else{
                        String name = item.getName();
                        
                        if(name.trim().isEmpty()){
                            name = item.getAbsolutePath();
                        }

                        setText(name);
                        setGraphic(FileIcon.createFileIcon(item));
                    }
                }
            };
        });
    }
}
package net.d_kami.d2delta.view;

import java.io.File;

import javafx.scene.Node;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;

public class FileTreeItem extends TreeItem<File>{
    public FileTreeItem(){
        this(null);
    }

    public FileTreeItem(File file){
        super(file);
    }

    @Override
    public boolean isLeaf(){
        File file = getValue();
 
        return file != null && file.isFile();
    }

    @Override
    public ObservableList<TreeItem<File>> getChildren(){
        ObservableList<TreeItem<File>> children = super.getChildren();
        
        if(!children.isEmpty()){
            return children;
        }

        File file = getValue();
        
        if(file == null){
            return children;
        }

        if(!file.isDirectory()){
            return FXCollections.emptyObservableList();
        }
        
        File[] files = file.listFiles();
        
        if(files == null){
            return FXCollections.emptyObservableList();
        }
        
        for(File f : files){
            if(f.isDirectory()){
                children.add(new FileTreeItem(f));
            }
        }
        
        return children;
    }
}
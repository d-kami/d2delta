package net.d_kami.d2delta.view;

import javafx.embed.swing.SwingNode;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.SwingUtilities;

import java.io.File;

public class FileIcon{
    private static FileSystemView FILE_VIEW = FileSystemView.getFileSystemView();
    
    public static SwingNode createFileIcon(File file){
        SwingNode node = new SwingNode();
        
        SwingUtilities.invokeLater(() -> {
            ImageIcon icon = (ImageIcon)FILE_VIEW.getSystemIcon(file);

            JLabel label = new JLabel(icon);
            node.setContent(label);
        });

        return node;
    }
}

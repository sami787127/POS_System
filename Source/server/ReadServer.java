/**
 * Class: A5Server
 * @author Sami Uzzaman, 7852285
 * A singleton that created only a single server class and makes sure there are no server class available at the same time
 */


package Source.server;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class ReadServer {
    //stores server
    private static POSServer server;
    //private constructor so nobody can create an instance
    private ReadServer(){}

    /**
     * Creates a server
     * @return  server
     */
    public static POSServer getServer(){
        if(server==null){
            //create the server if doesnt exist
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnVal = fileChooser.showOpenDialog(null);
            while (!(returnVal == JFileChooser.APPROVE_OPTION)){
                returnVal = fileChooser.showOpenDialog(null);
            }
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
            server = new Server(selectedFile);
        }

        //return the server
        return server;
    }
}

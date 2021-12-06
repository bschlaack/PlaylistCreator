/* */
package com.scbtech.playlistcreator;

import java.io.File;
import java.util.List;

/**
 *
 * @author bschlaac
 */
public class PlaylistDir {
    private String path;
    private File directory;
    private List<PlaylistDir> listSubDir;
    private List<Mp3Values> listMp3Values;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<PlaylistDir> getListSubDir() {
        return listSubDir;
    }

    public void setListSubDir(List<PlaylistDir> listSubDir) {
        this.listSubDir = listSubDir;
    }

    public List<Mp3Values> getListMp3Values() {
        return listMp3Values;
    }

    public void setListMp3Values(List<Mp3Values> listMp3Values) {
        this.listMp3Values = listMp3Values;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }
    
    
}

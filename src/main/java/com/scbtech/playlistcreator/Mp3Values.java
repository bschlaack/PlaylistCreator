/* */
package com.scbtech.playlistcreator;

import com.mpatric.mp3agic.Mp3File;
import java.io.File;

/**
 *
 * @author bschlaac
 */
public class Mp3Values {

    //private File file;
    //private Mp3File mp3File;
    private String title;
    private String path;
    private long length;

    public StringBuilder getM3uStringBuilder() {
        StringBuilder sBuild = new StringBuilder();
        sBuild.append("#EXTINF:").append(Long.toString(length)).append(",").append(title).append("\r\n").append(path).append("\r\n");
        return sBuild;
    }
    
    public String getM3uString() {
        return getM3uStringBuilder().toString();
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    /*public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public void setMp3File(Mp3File mp3File) {
        this.mp3File = mp3File;
    }*/

}

package com.scbtech.playlistcreator;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class PlaylistCreator {

    private boolean DEV = false;

    /**
     * @param args the command line arguments
     */
    private String getPath(String[] args) {
        String path = null;
        if (args.length > 0) {
            path = args[0];
        }
        if (path == null || path.length() == 0) {
            try {
                path = new File(PlaylistCreator.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            } catch (URISyntaxException uEx) {
                uEx.printStackTrace();
            }
            if (path != null && path.startsWith("file:/")) {
                path = path.substring(6);
            }
            if (path != null && path.toLowerCase().endsWith(".jar")) {
                if ( path.contains("\\")) {
                    path = path.substring(0, path.lastIndexOf("\\"));
                } else if ( path.contains("/")) {
                    path = path.substring(0, path.lastIndexOf("/"));
                }
            }
        }
        if (DEV) {
            return "D:\\mp3\\Tracks\\A Ha - How can i sleep - Live (MONXX)";
            //return "D:\\mp3\\04 Sandra - Maria Magdalena.mp3";
        } else {
            return path;
        }
    }

    private void writeToM3u(PlaylistDir playlistDir) {
        if (playlistDir.getListMp3Values() != null && !playlistDir.getListMp3Values().isEmpty()) {
            File dir = new File(playlistDir.getPath());
            File parentDir = dir.getParentFile();
            StringBuilder sBuild = new StringBuilder();
            sBuild.append("#EXTM3U\r\n");
            for (Mp3Values mp3 : playlistDir.getListMp3Values()) {
                sBuild.append(mp3.getM3uStringBuilder());
            }
            String playlistname = getM3uNameFromDir(dir);
            File playlist = new File(playlistname);
            try {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(playlist), StandardCharsets.UTF_8);
                writer.append('\ufeff');
                writer.append(sBuild.toString());
                writer.flush();
                writer.close();
                System.out.println(playlistname + " written.");
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }
        if ( playlistDir.getListSubDir() != null && !playlistDir.getListSubDir().isEmpty()) {
            for ( PlaylistDir subDir : playlistDir.getListSubDir()) {
                writeToM3u(subDir);
            }
        }
    }

    private String getM3uNameFromDir(File file) {
        if (file.isDirectory()) {
            String filename = file.getAbsolutePath();
            return filename + ".m3u8";
        }
        String filename = file.getAbsolutePath();
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        return filename + ".m3u8";
    }

    private File checkPathExists(String path) {
        File filePath = null;
        try {
            filePath = new File(path);
            if (filePath.exists()) {
                return filePath;
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private String getRealPath(String path) {
        String workpath = path;
        if (path.toLowerCase().endsWith(".mp3")) {
            if (workpath.contains("\\")) {
                workpath = path.substring(0, path.lastIndexOf("\\"));
            } else if (workpath.contains("/")) {
                workpath = path.substring(0, path.lastIndexOf("/"));
            }
        }
        return workpath;
    }

    private void addMp3ToPlaylist(PlaylistDir playlistDir, File file) {
        if (!file.getAbsolutePath().toLowerCase().endsWith(".mp3")) {
            System.out.println("File \"+file.getAbsolutePath()+\" no mp3, skipped!");
            return;
        }
        if (playlistDir.getListMp3Values() == null) {
            playlistDir.setListMp3Values(new ArrayList<>());
        }
        Mp3Values mp3 = new Mp3Values();
        //mp3.setFile(file);
        Mp3File mp3File = null;
        try {
            mp3File = new Mp3File(file, 65536, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("File \"+file.getAbsolutePath()+\" not supported, skipped!");
            return;
        }
        //mp3.setMp3File(mp3File);
        mp3.setPath(file.getAbsolutePath());
        mp3.setLength(mp3File.getLengthInSeconds());
        String title = null;
        if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2 = mp3File.getId3v2Tag();
            title = id3v2.getTitle();
        }
        if (title == null) {
            if (mp3File.hasId3v1Tag()) {
                ID3v1 id3v1 = mp3File.getId3v1Tag();
                title = id3v1.getTitle();
            }
        }
        if (title == null) {
            title = file.getAbsolutePath();
            if (title.contains("\\")) {
                title = title.substring(title.lastIndexOf("\\"));
            } else if (title.contains("/")) {
                title = title.substring(title.lastIndexOf("/"));
            }
        }
        mp3.setTitle(title);
        playlistDir.getListMp3Values().add(mp3);
    }

    private void parseDirectory(PlaylistDir playlistDir) {
        if (playlistDir != null && playlistDir.getPath()!= null && playlistDir.getPath().length() > 0) {
            System.out.println("Parsing directory: "+playlistDir.getPath());
            File directory = new File(playlistDir.getPath());
            if (directory.listFiles() != null && directory.listFiles().length > 0) {
                File[] files = directory.listFiles();
                Arrays.sort(files);
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (playlistDir.getListSubDir() == null) {
                            playlistDir.setListSubDir(new ArrayList<>());
                        }
                        PlaylistDir subDir = new PlaylistDir();
                        subDir.setPath(file.getAbsolutePath());
                        //subDir.setDirectory(file);
                        playlistDir.getListSubDir().add(subDir);
                        parseDirectory(subDir);
                    } else if (file.getAbsolutePath().toLowerCase().endsWith(".mp3")) {
                        addMp3ToPlaylist(playlistDir, file);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        PlaylistDir rootPath = new PlaylistDir();
        PlaylistCreator playlistCreator = new PlaylistCreator();
        String path = playlistCreator.getPath(args);
        System.out.println("Try path: " + path);
        File filePath = playlistCreator.checkPathExists(path);
        if (filePath != null) {
            System.out.println("Working on dir: " + path);
            if (filePath.isDirectory()) {
                rootPath.setPath(playlistCreator.getRealPath(path));
                //rootPath.setDirectory(filePath);
                playlistCreator.parseDirectory(rootPath);
                playlistCreator.writeToM3u(rootPath);
            } else {
                if (path.toLowerCase().endsWith(".mp3")) {
                    rootPath.setPath(playlistCreator.getRealPath(path));
                    //rootPath.setDirectory(filePath.getParentFile());
                    playlistCreator.addMp3ToPlaylist(rootPath, filePath);
                } else {
                    System.out.println("Path was file, but no mp3, stopping!");
                }
                playlistCreator.writeToM3u(rootPath);
            }
        } else {
            System.out.println("Path not found, stopping!");
        }
    }

}

package io.github.cactacea.backend;

import javax.print.URIException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class FileAccessUtil {

    public void copyAll(String src, String dst) {
        URL url = getClass().getClassLoader().getResource(src);
        if (url != null) {
            try {

                Path to = Paths.get(dst);
                if (!Files.exists(to)) {
                    Files.createDirectories(to);
                }
                if (url.getProtocol().equals("jar")) {
                    String[] s = url.getPath().split(":");
                    String path = s[s.length - 1].split("!")[0];
                    File f = new File(path);
                    JarFile jarFile;
                    try {
                        jarFile = new JarFile(f);
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(src) && entry.isDirectory() == false) {
                                InputStream in = jarFile.getInputStream(entry);
                                String fileName = name.substring(src.length() + 1);
                                Path toFile = Paths.get(dst + fileName);
                                Files.deleteIfExists(toFile);
                                Files.copy(in, toFile);
                            }
                        }
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                } else {
                    URI uri = url.toURI();
                    Path resourcePath = Paths.get(uri);
                    Stream<Path> walk = Files.walk(resourcePath, 1);
                    Iterator<Path> it = walk.sorted().iterator();
                    it.next();
                    while (it.hasNext()) {
                        Path path = it.next();
                        Path toFile = to.resolve(path.getFileName());
                        Files.deleteIfExists(toFile);
                        Files.copy(path, toFile);
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
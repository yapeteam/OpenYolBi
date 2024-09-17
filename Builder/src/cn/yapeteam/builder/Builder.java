package cn.yapeteam.builder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import proguard.ProGuard;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("SameParameterValue")
public class Builder {
    private static void copyStream(OutputStream os, InputStream is) throws IOException {
        int len;
        byte[] bytes = new byte[4096];
        while ((len = is.read(bytes)) != -1)
            os.write(bytes, 0, len);
    }

    private static byte[] readStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1)
            outStream.write(buffer, 0, len);
        outStream.close();
        return outStream.toByteArray();
    }

    public interface Action {
        void execute(File file);
    }

    public static void traverseFiles(File folder, Action action) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    traverseFiles(file, action);
                else action.execute(file);
            }
        }
    }

    private static void disposeInclude(Node node, ZipOutputStream output, String root_dir) {
        boolean is_root = true;
        for (char c : root_dir.toCharArray()) {
            if (c != '/' && c != '.') {
                is_root = false;
                break;
            }
        }
        if (is_root) root_dir = "";
        Node name_attr = node.getAttributes().getNamedItem("name");
        Node platform_node = node.getAttributes().getNamedItem("platform");
        if (platform_node != null) {
            String platformName = platform_node.getNodeValue();
            if (platformName.equals("windows") && !OS.isFamilyWindows()) return;
            else if (platformName.equals("mac") && !OS.isFamilyMac()) return;
            else if (platformName.equals("linux") && !OS.isFamilyUnix()) return;
        }
        switch (node.getNodeName()) {
            case "dir": {
                if (name_attr == null) {
                    System.out.println("dir " + node.getTextContent());
                    File dir = new File(node.getTextContent());
                    String parent = dir.getParent();
                    String root = parent != null ? parent : "/";
                    String finalRoot_dir = root_dir;
                    traverseFiles(dir, file -> {
                        String path = file.toString();
                        System.out.println(path);
                        String entry_name = root.length() > 1 ? finalRoot_dir + path.substring(root.length()).replace("\\", "/").substring(1) : finalRoot_dir + path.replace("\\", "/");
                        ZipEntry entry = new ZipEntry(entry_name);
                        try {
                            output.putNextEntry(entry);
                            output.write(readStream(Files.newInputStream(file.toPath())));
                            output.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    root_dir = root_dir + "/" + name_attr.getNodeValue();
                    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                        Node child = node.getChildNodes().item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE)
                            disposeInclude(child, output, root_dir);
                    }
                }
            }
            break;
            case "files": {
                System.out.println("files " + node.getTextContent());
                File dir = new File(node.getTextContent());
                String root = node.getTextContent();
                String finalRoot_dir = root_dir;
                traverseFiles(dir, file -> {
                    String path = file.toString();
                    String entry_name = finalRoot_dir + (finalRoot_dir.isEmpty() ? "" : "/") + path.substring(root.length()).replace("\\", "/");
                    if (entry_name.startsWith("/"))
                        entry_name = entry_name.substring(1);
                    ZipEntry entry = new ZipEntry(entry_name);
                    try {
                        output.putNextEntry(entry);
                        output.write(readStream(Files.newInputStream(file.toPath())));
                        output.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            break;
            case "file": {
                System.out.println("file " + node.getTextContent());
                File file = new File(node.getTextContent());
                String path = file.toString();
                String entry_name = root_dir + (root_dir.isEmpty() ? "" : "/") + path.substring(file.getParent() != null ? file.getParent().length() : 0).replace("\\", "/");
                if (entry_name.startsWith("/"))
                    entry_name = entry_name.substring(1);
                ZipEntry entry = new ZipEntry(entry_name);
                try {
                    output.putNextEntry(entry);
                    output.write(readStream(Files.newInputStream(file.toPath())));
                    output.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "extract": {
                try {
                    System.out.println("extract " + node.getTextContent());
                    String path = node.getTextContent();
                    ZipInputStream input = new ZipInputStream(Files.newInputStream(Paths.get(path)));
                    ZipEntry entry_in;
                    while ((entry_in = input.getNextEntry()) != null) {
                        if (entry_in.isDirectory()) continue;
                        String entry_name = entry_in.getName();
                        if (entry_name.startsWith("module-info.class")) continue;
                        if (entry_name.startsWith("META-INF/MANIFEST.MF")) continue;
                        if (entry_name.startsWith("META-INF/LICENSE")) continue;
                        if (entry_name.startsWith(root_dir))
                            entry_name = entry_name.substring(root_dir.length());
                        if (entry_name.startsWith("/"))
                            entry_name = entry_name.substring(1);
                        ZipEntry entry_out = new ZipEntry(root_dir + (root_dir.isEmpty() ? "" : "/") + entry_name);
                        output.putNextEntry(entry_out);
                        copyStream(output, input);
                        output.closeEntry();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "archive": {
                String name = name_attr.getNodeValue();
                System.out.println("archive " + name);
                ZipEntry entry = new ZipEntry(root_dir + (root_dir.isEmpty() ? "" : "/") + name);
                try {
                    output.putNextEntry(entry);
                    ZipOutputStream output_inner = new ZipOutputStream(output);
                    output_inner.setMethod(ZipOutputStream.DEFLATED);
                    output_inner.setLevel(Deflater.BEST_COMPRESSION);
                    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                        Node child = node.getChildNodes().item(i);
                        if (child.getNodeType() == Node.ELEMENT_NODE)
                            disposeInclude(child, output_inner, "/");
                    }
                    output_inner.finish();
                    output.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    private static void downloadFile(String file_url, File file) throws Exception {
        SSLUtils.ignoreSsl();
        URL url = new URL(file_url);
        URLConnection connection = url.openConnection();
        int totalFileSize = connection.getContentLength();
        FileOutputStream outputFile = new FileOutputStream(file);
        int blockSize = 1024 * 1024;
        byte[] buffer = new byte[blockSize];
        int bytesRead;
        int downloadedBytes = 0;
        ProcessBar progressBar = new ProcessBar(100);
        while (downloadedBytes < totalFileSize) {
            int bytesToRead = Math.min(blockSize, totalFileSize - downloadedBytes);
            InputStream inputStream = connection.getInputStream();
            bytesRead = inputStream.read(buffer, 0, bytesToRead);
            if (bytesRead == -1) break;
            outputFile.write(buffer, 0, bytesRead);
            downloadedBytes += bytesRead;
            progressBar.update((int) (((float) downloadedBytes / totalFileSize) * 100));
        }
        outputFile.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) return;
        boolean advanced_mode = args[0].equals("release");
        System.setSecurityManager(new NoExitSecurityManager());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("YBuild.xml");
        Element root = document.getDocumentElement();
        String root_dir = root.getElementsByTagName("rootdir").item(0).getTextContent();
        String output_dir = root.getElementsByTagName("output").item(0).getTextContent();
        Element build = (Element) root.getElementsByTagName("build").item(0);
        deleteFileByStream(output_dir);
        new File(output_dir).mkdirs();
        for (int i = 0; i < build.getChildNodes().getLength(); i++) {
            Node node = build.getChildNodes().item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                Node platform_node = element.getAttributes().getNamedItem("platform");
                if (platform_node != null) {
                    String platformName = platform_node.getNodeValue();
                    if (platformName.equals("windows") && !OS.isFamilyWindows()) continue;
                    else if (platformName.equals("mac") && !OS.isFamilyMac()) continue;
                    else if (platformName.equals("linux") && !OS.isFamilyUnix()) continue;
                }
                if (element.getTagName().equals("artifact")) {
                    String artifact_name = element.getAttribute("name");
                    String artifact_id = artifact_name.substring(0, artifact_name.lastIndexOf("."));
                    File output_file = new File(output_dir, artifact_name);
                    boolean ignored = output_file.getParentFile().mkdirs();
                    Node proguard_cfg = element.getAttributes().getNamedItem("proguard-config");
                    System.out.printf("building artifact %s...%n", artifact_name);
                    ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(output_file.toPath()));
                    List<Node> includes_list = new ArrayList<>();
                    for (int j = 0; j < element.getChildNodes().getLength(); j++) {
                        Node include = element.getChildNodes().item(j);
                        output.setMethod(ZipOutputStream.DEFLATED);
                        output.setLevel(Deflater.BEST_COMPRESSION);
                        if (include.getNodeType() == Node.ELEMENT_NODE)
                            includes_list.add(include);
                    }
                    for (int j = 0; j < includes_list.size(); j++) {
                        Node include = includes_list.get(j);
                        disposeInclude(include, output, root_dir + "/");
                        System.out.printf("artifact %s: included %s, %s of %s%n", artifact_name, include.getNodeName(), j + 1, includes_list.size());
                    }
                    output.close();
                    if (!advanced_mode) continue;
                    if (proguard_cfg != null) {
                        File build_dir = new File(output_dir, artifact_id);
                        if (build_dir.exists())
                            deleteFileByStream(build_dir.getAbsolutePath());
                        boolean ignored0 = build_dir.mkdirs();
                        File tobe_proguard = new File(build_dir, artifact_name);
                        File artifact_file = new File(output_dir, artifact_name);
                        copyStream(Files.newOutputStream(tobe_proguard.toPath()), Files.newInputStream(artifact_file.toPath()));
                        try {
                            ProGuard.main(new String[]{"@" + proguard_cfg.getNodeValue()});
                        } catch (ExitException ignored1) {
                        }
                        copyStream(Files.newOutputStream(artifact_file.toPath()), Files.newInputStream(new File(build_dir, artifact_name + "-obf.jar").toPath()));
                    }
                }
            }
        }
        System.out.println("BUILD SUCCESS");
    }

    public static void deleteFileByStream(String filePath) {
        Path path = Paths.get(filePath);
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder()).forEach(Builder::deleteDirectoryStream);
        } catch (NoSuchFileException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteDirectoryStream(Path path) {
        try {
            Files.delete(path);
        } catch (NoSuchFileException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) {
            // allow anything.
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
            // allow anything.
        }

        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ExitException(status);
        }
    }

    public static class ExitException extends SecurityException {
        private static final long serialVersionUID = 1L;
        public final int status;

        public ExitException(int status) {
            super("ignore");
            this.status = status;
        }
    }
}

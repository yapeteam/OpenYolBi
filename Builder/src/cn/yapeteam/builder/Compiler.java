package cn.yapeteam.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static void main(String[] args) throws Exception {
        buildModule(new String[]{"VersionInfo/src"}, new String[]{}, "build/VersionInfo");
        buildModule(new String[]{"YMixin/src"}, new String[]{"libs", "deps"}, "build/YMixin");
        buildModule(new String[]{"Core-1.8.9/src"}, new String[]{"libs", "libs-low", "deps", "minecraft-lib/minecraft-1.8.9.jar", "build/VersionInfo:", "build/YMixin:"}, "build/Core-1.8.9");
        buildModule(new String[]{"Core-1.12.2/src"}, new String[]{"libs", "libs-low", "deps", "minecraft-lib/minecraft-1.12.2.jar", "build/VersionInfo:", "build/YMixin:"}, "build/Core-1.12.2");
        buildModule(new String[]{"Core-1.18.1/src"}, new String[]{"libs", "Core-1.18.1/libs", "deps", "minecraft-lib/minecraft-1.18.1.jar", "build/VersionInfo:", "build/YMixin:"}, "build/Core-1.18.1");
    }

    public static void buildModule(String[] sourcePath, String[] libraryPath, String buildDir) throws Exception {
        boolean ignored = new File(buildDir).mkdirs();
        List<String> command = new ArrayList<>();

        command.add(System.getenv("WORKFLOW_JAVA") + "/bin/javac"); // 指定 javac 命令

        command.add("-encoding");
        command.add("UTF-8");

        command.add("-cp");

        StringBuilder sb = new StringBuilder();
        // 构建类路径
        for (String s : libraryPath) {
            if (s.endsWith(":")) {
                sb.append(OS.isFamilyUnix() ? ":" : ";");
                String absolutePath = new File(s.substring(0, s.length() - 1)).getAbsolutePath();
                sb.append(absolutePath);
                continue;
            }
            File file = new File(s);
            String fileName = file.getName();
            if (file.isDirectory()) {
                if (fileName.endsWith(":")) {
                    sb.append(OS.isFamilyUnix() ? ":" : ";");
                    String absolutePath = file.getAbsolutePath();
                    absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
                    sb.append(absolutePath);
                } else Builder.traverseFiles(file, jar -> {
                    if (jar.getName().endsWith(".jar")) {
                        sb.append(OS.isFamilyUnix() ? ":" : ";");
                        sb.append(jar.getAbsolutePath());
                    }
                });
            } else if (fileName.endsWith(".jar")) {
                sb.append(OS.isFamilyUnix() ? ":" : ";");
                sb.append(file.getAbsolutePath());
            }
        }
        if (sb.length() > 1)
            sb.deleteCharAt(0);

        command.add(sb.toString());

        // 设置输出目录
        command.add("-d");
        command.add(buildDir);

        for (String s : sourcePath) {
            // 添加要编译的源文件
            File srcDirectory = new File(s);

            Builder.traverseFiles(srcDirectory, file -> {
                if (file.getName().endsWith(".java")) {
                    command.add(file.getAbsolutePath());
                }
            });
        }

        Terminal terminal = new Terminal(new File("."), null);
        terminal.execute(command.toArray(new String[0]));
    }
}

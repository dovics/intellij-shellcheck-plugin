package com.shellcheck.settings;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public final class ShellcheckFinder {
    private ShellcheckFinder() {
    }

    static String findShellcheckExe() {
        File path = PathEnvironmentVariableUtil.findInPath(getBinName("shellcheck"));
        if (path == null) {
            return null;
        }

        return path.getAbsolutePath();
    }
    @NotNull
    static List<String> findAllShellcheckExe() {
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(getBinName("shellcheck"));
        return fromPath.stream().map(File::getAbsolutePath).distinct().collect(Collectors.toList());
    }

    static String getBinName(String baseBinName) {
        // TODO do we need different name for windows?
        return SystemInfo.isWindows ? baseBinName + ".exe" : baseBinName;
    }

    static boolean validatePath(Project project, String path) {
        File file = new File(path);
        if (file.isAbsolute()) {
            return file.exists() && file.isFile() && file.canExecute();
        } else {
            if (project == null) {
                return false;
            }

            String basePath = project.getBasePath();
            if (basePath == null) {
                return false;
            }

            VirtualFile baseDir = VfsUtil.findFile(Paths.get(basePath), false);
            if (baseDir == null) {
                return false;
            }

            VirtualFile child = VfsUtil.findRelativeFile(baseDir, path);
            return child != null && child.exists() && !child.isDirectory() && file.canExecute();
        }
    }
}
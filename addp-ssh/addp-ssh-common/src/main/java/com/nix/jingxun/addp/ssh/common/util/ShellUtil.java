package com.nix.jingxun.addp.ssh.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author keray
 * @date 2019/05/12 16:56
 */
public final class ShellUtil {

    public static String getShellContent(String result) {
        if (StrUtil.isBlank(result)) {
            return result;
        }
        String[] lines = result.split(System.lineSeparator());
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            if (i == 0 && lines[i].matches("\\[[^\\[|^\\]]+][#|$][\\s].*")) {
                continue;
            }
            if (i == lines.length - 1 && lines[i].matches("\\[[^\\[|^\\]]+][#|$][\\s].*")) {
                break;
            }
            buffer.append(lines[i]).append(System.lineSeparator());
        }
        return buffer.toString();
    }

    public static boolean commandIsExec(String result) {
        return getShellContent(result) != null && !getShellContent(result).matches("command not found");
    }


    public static boolean shellEnd(String shell) {
        return shell.matches("[\\S|\\s]*\\$[\\s]*") ||
                shell.matches("[\\S|\\s]*#[\\s]*") ||
                shell.matches("[\\S|\\s]*:[\\s]*") ||
                shell.matches("[\\s|\\S]*\\)\\?[\\s]*");
    }

    public static boolean shellNeedKeydown(String shell) {
        return shell.endsWith(": ");
    }

    public static boolean shellYN(String shell) {
        return shell.matches("[\\s|\\S]*\\)\\?[\\s]*");
    }


    public static boolean cd(String cdDir, ShellExe shellExe) {
        try {
            String result = shellExe.oneCmd("cd " + cdDir);
            String endDir = cdDir.split("/")[cdDir.split("/").length > 0 ? cdDir.split("/").length - 1 : 0];
            return result.matches("[\\s|\\S]*\\[.*" + endDir + "][#|$].*");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}

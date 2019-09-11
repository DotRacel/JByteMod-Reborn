package me.grax.jbytemod.scanner;

import hakery.club.raccscanner.Raccoon;
import hakery.club.raccscanner.results.Result;
import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.utils.DeobfusacteUtils;
import org.objectweb.asm.tree.ClassNode;

import javax.swing.*;
import java.util.Map;

public class ScannerThread extends Thread {
    private Map<String, ClassNode> input;
    private Result result;
    private Raccoon scanner;
    private byte[] jarManifest = null;

    public ScannerThread(Map<String, ClassNode> input) {
        this.input = input;
    }

    @Override
    public void run() {
        scanner = new Raccoon(input, jarManifest);
        scanner.initialize(null);
        scanner.scan();
        System.out.println("[Raccoon] Finished scanning the file.");
        result = scanner.getResult();

        result.printResults();

        JOptionPane.showMessageDialog(null, String.join("\n", result.getResults()) + "\n\nPowered by Raccoon Scanner.",
                JByteMod.res.getResource("raccoon"), JOptionPane.INFORMATION_MESSAGE);
    }

    public void cleanCache() {
        this.input = null;
    }

    public void setJarManifest(byte[] jarManifest){
        this.jarManifest = jarManifest;
    }
}

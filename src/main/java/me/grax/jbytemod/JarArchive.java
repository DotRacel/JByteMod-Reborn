package me.grax.jbytemod;

import me.grax.jbytemod.utils.ErrorDisplay;
import me.grax.jbytemod.utils.task.LoadTask;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JarArchive {
    protected Map<String, ClassNode> classes;
    protected Map<String, byte[]> output;
    protected byte[] jarManifest;
    private boolean singleEntry;

    public JarArchive(ClassNode cn) {
        super();
        this.classes = new HashMap<>();
        this.singleEntry = true;
        classes.put(cn.name, cn);
    }

    public JarArchive(JByteMod jbm, File input) {
        try {
            new LoadTask(jbm, input, this).execute();
        } catch (Throwable t) {
            new ErrorDisplay(t);
        }
    }

    public JarArchive(Map<String, ClassNode> classes, Map<String, byte[]> output) {
        super();
        this.classes = classes;
        this.output = output;
    }

    public Map<String, ClassNode> getClasses() {
        return classes;
    }

    public byte[] getJarManifest() {
        return jarManifest;
    }

    public void setJarManifest(byte[] jarManifest){
        this.jarManifest = jarManifest;
    }

    public void setClasses(Map<String, ClassNode> classes) {
        this.classes = classes;
    }

    public Map<String, byte[]> getOutput() {
        return output;
    }

    public void setOutput(Map<String, byte[]> output) {
        this.output = output;
    }

    public boolean isSingleEntry() {
        return singleEntry;
    }
}

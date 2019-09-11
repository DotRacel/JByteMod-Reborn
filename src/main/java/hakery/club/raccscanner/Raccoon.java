package hakery.club.raccscanner;

import hakery.club.raccscanner.logger.RaccoonLogger;
import hakery.club.raccscanner.results.Result;
import hakery.club.raccscanner.scanner.Scanners;
import hakery.club.raccscanner.util.DataUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Raccoon {

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
    private File targetFile;
    private Map<String, ClassNode> classes;
    private boolean debugging = false;
    private Scanners scanners;
    private boolean debugInfoFound = false;
    private Result result;

    private RaccoonLogger logger = new RaccoonLogger();

    private byte[] jarManifest = null;

    public Raccoon(File target) throws IOException {

        if (!target.exists()) {
            getLogger().error("Uh oh! Input file doesn't exist! Throwing exception...");
            throw new FileNotFoundException("[Raccoon] Couldn't locate the targeted file");
        }

        this.targetFile = target;
        this.classes = readClasses();

        assert this.classes.size() > 0;
    }

    /**
     * Use this if you've already filtered the jar file /  already have a classpath you want to scan
     * this will prevent limitations that might exist
     *
     * @param classes
     */
    public Raccoon(Map<String, ClassNode> classes, byte[] jarManifest) {
        this.classes = classes;
        this.targetFile = null;
        if(jarManifest != null) this.jarManifest = jarManifest;

        assert this.classes.size() > 0;
    }

    public void initialize(OutputStream target) {
        this.scanners = new Scanners(this);
        this.setOutputStream(target == null ? System.out : target);

        /** if we're debugging, we will scan right away */
        if (this.isDebugging()) {
            this.scanners.scan();
        }
    }

    public void scan() {
        /** If we're debugging, we would've already scanned */
        if (!this.isDebugging())
            this.scanners.scan();
    }

    public Map<String, ClassNode> readClasses() throws IOException {
        Map<String, ClassNode> tmp = new HashMap<>();
        assert targetFile != null;

        ZipFile zip = new ZipFile(this.targetFile);
        Enumeration<? extends ZipEntry> zipEntries = zip.entries();

        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = zipEntries.nextElement();

            try {
                /** Credits to ItzSomebody **/
                if (entry.getName().endsWith(".class")) {
                    byte[] classBytes = DataUtils.INSTANCE.toByteArray(zip.getInputStream(entry));

                    ClassReader classReader = new ClassReader(classBytes);
                    ClassNode classNode = new ClassNode();

                    classReader.accept(classNode, Opcodes.ASM6 | ClassReader.SKIP_FRAMES);

                    for (int i = 0; i < classNode.methods.size(); i++) {
                        MethodNode methodNode = classNode.methods.get(i);
                        JSRInlinerAdapter adapter = new JSRInlinerAdapter(methodNode, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[0]));
                        methodNode.accept(adapter);
                        classNode.methods.set(i, adapter);
                    }

                    tmp.put(classNode.name, classNode);

                } else if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                    this.jarManifest = DataUtils.INSTANCE.toByteArray(zip.getInputStream(entry));
                }
            } catch (IllegalArgumentException e) {
                this.logger.log("Couldn't parse %s", entry.getName());
            }
        }
        zip.close();
        return tmp;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean val) {
        this.debugging = val;
    }

    public boolean isDebugInfoFound() {
        return debugInfoFound;
    }

    public void setDebugInfoFound(boolean value) {
        this.debugInfoFound = value;
    }

    public Map<String, ClassNode> getClasses() {
        return this.classes;
    }

    public String getDateCreated() {
        if (this.targetFile == null)
            return "Classpath was directly loaded...";
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(this.targetFile.toPath(), BasicFileAttributes.class);
            long creationTime = basicFileAttributes.creationTime().toMillis();

            Date date = new Date(creationTime);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";
    }

    public String getLastModified() {
        if (this.targetFile == null)
            return "Classpath was directly loaded...";
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(this.targetFile.toPath(), BasicFileAttributes.class);
            long creationTime = basicFileAttributes.lastModifiedTime().toMillis();

            Date date = new Date(creationTime);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Null";
    }

    public Result getResult() {
        if (this.result == null)
            this.result = new Result(this);

        return this.result;
    }

    public RaccoonLogger getLogger() {
        return logger;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.logger.setTargetOutputStream(outputStream);
    }

    public File getTargetFile() {
        return targetFile;
    }

    public Scanners getScanner() {
        return scanners;
    }

    public byte[] getJarManifest() {
        return jarManifest;
    }
}

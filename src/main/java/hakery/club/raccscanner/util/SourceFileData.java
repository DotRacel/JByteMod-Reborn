package hakery.club.raccscanner.util;

import org.objectweb.asm.tree.ClassNode;

public class SourceFileData {

    private final String name;
    private final ClassNode node;

    public SourceFileData(ClassNode classNode, String name) {
        this.name = name;
        this.node = classNode;
    }

    public String getName() {
        return name;
    }

    public ClassNode getNode() {
        return node;
    }

}

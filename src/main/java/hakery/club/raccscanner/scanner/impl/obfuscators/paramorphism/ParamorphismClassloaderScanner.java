package hakery.club.raccscanner.scanner.impl.obfuscators.paramorphism;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.OpcodeUtils;
import hakery.club.raccscanner.util.opcodes.InstructionList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Now we get more advanced, this scanner will search for any ClassLoader from the Paramorphism obfuscator
 */
public class ParamorphismClassloaderScanner extends Scanner<ArrayList<ClassNode>> {

    private final InstructionList classLoaderInstructions = new InstructionList(Arrays.asList(
            Opcodes.ALOAD, /* aload */
            Opcodes.ALOAD, /* aload*/
            Opcodes.INVOKESPECIAL, /* invokespecial */
            0xFF, /* bytes */
            Opcodes.ALOAD, /* aload */
            Opcodes.ALOAD, /* aload*/
            Opcodes.INVOKEVIRTUAL, /* invokevirtual */
            0xFF, /* bytes */
            Opcodes.NEW, /* new */
            Opcodes.DUP, /* dup */
            Opcodes.ALOAD, /* aload */
            Opcodes.INVOKESPECIAL, /* invokespecial */
            0xFF, /* random */
            Opcodes.ALOAD /* aload */
    ));

    @Override
    public boolean scan() {
        ArrayList<ClassNode> res = new ArrayList<>();

        /* do this for every class */
        raccoon.getClasses().forEach((name, node) -> {

            AtomicInteger flags = new AtomicInteger();

            /* Class must be a classloader */
            if (node.superName.equals("java/lang/ClassLoader")) {
                node.fields.forEach(fieldNode -> {
                    /* every classloader contained a byte array (this is generally found in custom classloaders anyway) */
                    if (fieldNode.desc.equals("[B") && node.fields.size() == 1)
                        flags.addAndGet(1);
                });

                node.methods.forEach(methodNode -> {
                    /* function createClass */
                    if (methodNode.name.equals("createClass") && methodNode.signature.equals("(Ljava/lang/String;)Ljava/lang/Class<*>;"))
                        flags.addAndGet(1);
                    else {
                        InstructionList instructionList = new InstructionList(methodNode.instructions);

                        if (OpcodeUtils.getInstance().compareOpcodes(classLoaderInstructions, instructionList, 0))
                            flags.addAndGet(1);
                    }
                });

                if (raccoon.isDebugging() && flags.get() != 0)
                    log("%s.class with certainty level: %d (%s)", name, flags.get(), flags.get() == 1 ? "Unsure" : flags.get() == 2 ? "Undecisive" : "Confident");

                if (flags.get() >= 2)
                    res.add(node);

                this.reset();
            }
        });

        this.setResult(res);

        return true;
    }
}

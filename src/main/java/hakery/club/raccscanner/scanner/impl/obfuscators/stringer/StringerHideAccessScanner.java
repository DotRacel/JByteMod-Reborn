package hakery.club.raccscanner.scanner.impl.obfuscators.stringer;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.OpcodeUtils;
import hakery.club.raccscanner.util.opcodes.InstructionList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Info about this stub:
 * - Always has 6 fields
 * - Static initializer parses a bunch of Integers
 * - Has method that takes integer as arguments, xor's a bunch of crap and returns a Method
 * - Has method that returns Field value and can throw NoSuchFieldError
 */
public class StringerHideAccessScanner extends Scanner<ArrayList<ClassNode>> {

    /**
     * Confirmed to work up to latest 9.1.15
     */
    final InstructionList stringerHideAccessFieldGetter3_0 = new InstructionList(Arrays.asList(
            Opcodes.ILOAD,
            Opcodes.INVOKESTATIC,
            Opcodes.ASTORE,
            Opcodes.ALOAD,
            Opcodes.IFNONNULL,
            Opcodes.NEW,
            Opcodes.DUP,
            Opcodes.ILOAD,
            Opcodes.INVOKESTATIC,
            Opcodes.INVOKESPECIAL,
            Opcodes.ATHROW,
            0xFF, /* smth about f_new */
            Opcodes.ALOAD,
            Opcodes.ALOAD,
            Opcodes.INVOKEVIRTUAL,
            Opcodes.ARETURN
    ));

    /** Confirmed to work up to latest 9.1.15 */
    final InstructionList stringerHideAccessMethodGetter3_0 = new InstructionList(Arrays.asList(
            Opcodes.ILOAD,
            Opcodes.LDC,
            0xFF, /* either isub or iadd or imul */
            Opcodes.GETSTATIC,
            Opcodes.IXOR,
            Opcodes.LDC,
            0xFF, /* again isub or iadd (my tests havent received anything diff tho */
            Opcodes.LDC,
            Opcodes.IXOR,
            Opcodes.GETSTATIC,
            Opcodes.IADD,
            Opcodes.ISTORE,
            Opcodes.ILOAD,
            Opcodes.BIPUSH,
            Opcodes.IUSHR,
            Opcodes.ISTORE,
            Opcodes.ILOAD,
            Opcodes.LDC,
            Opcodes.IAND,
            Opcodes.ISTORE,
            Opcodes.GETSTATIC
    ));

    @Override
    public boolean scan() {
        ArrayList<ClassNode> res = new ArrayList<>();

        raccoon.getClasses().forEach((classPath, classNode) -> {

            AtomicInteger flags = new AtomicInteger();

            /* Class must NOT be a Thread */
            if (!classNode.superName.contains("java/lang/Thread")) {
                if (classNode.fields.size() == 6)
                    if (classNode.fields.stream().allMatch(fd -> fd.desc.equals("[Ljava/lang/Object;")
                            || fd.desc.equals("[Ljava/lang/Class;")
                            || fd.desc.equals("[I")
                            || fd.desc.equals("[S")
                            || fd.desc.equals("I")))
                        this.incrementFlagsReached();

                classNode.methods.forEach(methodNode -> {
                    InstructionList instructionList = new InstructionList(methodNode.instructions);

                    if (OpcodeUtils.getInstance().compareOpcodes(stringerHideAccessFieldGetter3_0, instructionList, 0))
                        this.incrementFlagsReached();

                    if (OpcodeUtils.getInstance().compareOpcodes(stringerHideAccessMethodGetter3_0, instructionList, 0))
                        this.incrementFlagsReached();
                });

                if (raccoon.isDebugging() && getFlagsReached() != 0)
                    log("%s.class with certainty level: %d (%s)", classPath, getFlagsReached(), getFlagsReached() == 1 ? "Unsure" : getFlagsReached() == 2 ? "Undecisive" : "Confident");

                if (getFlagsReached() >= 2)
                    res.add(classNode);

                this.reset();
            }
        });

        setResult(res);
        return true;
    }

}

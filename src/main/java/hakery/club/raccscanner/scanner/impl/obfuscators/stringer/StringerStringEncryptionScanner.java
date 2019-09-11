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
 * Info about the class in question:
 * - Extends thread
 * - Has three fields [object array, int, BigInteger array]
 * - Decryption method always has 1147 instructions
 * - first 30 (even more) instructions are always the same
 * - If I were to implement frame checks, there's a bunch of local variables
 */

public class StringerStringEncryptionScanner extends Scanner<ArrayList<ClassNode>> {

    final InstructionList stringerDecrypterStringEncryptionV3_0 = new InstructionList(Arrays.asList(
            Opcodes.SIPUSH,
            Opcodes.NEWARRAY,
            Opcodes.ASTORE,
            Opcodes.SIPUSH,
            Opcodes.NEWARRAY,
            Opcodes.ASTORE,
            Opcodes.SIPUSH,
            Opcodes.NEWARRAY,
            Opcodes.ASTORE,
            Opcodes.SIPUSH,
            Opcodes.NEWARRAY,
            Opcodes.ASTORE,
            Opcodes.BIPUSH,
            Opcodes.ANEWARRAY,
            Opcodes.DUP,
            0xFF, /* i forgot what this was */
            Opcodes.ALOAD,
            Opcodes.AASTORE,
            Opcodes.DUP
    ));

    final InstructionList stringerDecrypterStringEncryptionV9_15 = new InstructionList(Arrays.asList(
            Opcodes.GETSTATIC,
            Opcodes.NEW,
            Opcodes.DUP,
            Opcodes.ALOAD,
            Opcodes.CHECKCAST,
            Opcodes.INVOKESPECIAL,
            Opcodes.ASTORE,
            Opcodes.ALOAD,
            Opcodes.INVOKEVIRTUAL,
            Opcodes.IFNULL,
            Opcodes.GETSTATIC,
            Opcodes.ALOAD,
            Opcodes.INVOKEVIRTUAL,
            Opcodes.CHECKCAST,
            Opcodes.ARETURN,
            0xFF,
            Opcodes.ALOAD,
            Opcodes.CHECKCAST,
            Opcodes.CHECKCAST,
            Opcodes.ALOAD,
            Opcodes.CHECKCAST,
            Opcodes.CHECKCAST,
            Opcodes.ARRAYLENGTH,
            0xFF,
            Opcodes.ISUB,
            Opcodes.CALOAD,
            Opcodes.BIPUSH,
            Opcodes.ISHL,
            Opcodes.ILOAD,
            Opcodes.IOR

    ));

    @Override
    public boolean scan() {

        ArrayList<ClassNode> res = new ArrayList<>();

        raccoon.getClasses().forEach((classPath, classNode) -> {

            AtomicInteger flags = new AtomicInteger();

            /* Class must be a Thread */
            if (classNode.superName.contains("java/lang/Thread")) {

                /** V3 */
                if (classNode.fields.size() == 3)
                    if (classNode.fields.stream().allMatch(fd -> fd.desc.equals("[Ljava/lang/Object;")
                            || fd.desc.equals("I")
                            || fd.desc.equals("[Ljava/math/BigInteger;")))
                        flags.incrementAndGet();

                classNode.methods.forEach(methodNode -> {
                    if (methodNode.desc.equals("(ILjava/lang/Object;)V")) {
                        flags.incrementAndGet();

                        InstructionList instructionList = new InstructionList(methodNode.instructions);

                        if (OpcodeUtils.getInstance().findOpcodes(stringerDecrypterStringEncryptionV3_0, instructionList))
                            flags.incrementAndGet();
                    }
                });

                if (raccoon.isDebugging() && flags.get() != 0)
                    log("%s.class for 3_0 with certainty level: %d (%s)", classPath, flags.get(), flags.get() == 1 ? "Unsure" : flags.get() == 2 ? "Undecisive" : "Confident");

                if (flags.get() >= 2)
                    res.add(classNode);
            } /** End of V3 **/

            /** Save performance if V3 was already detected */
            if (!(flags.get() > 1)) {
                if (classNode.fields.size() == 2) {
                    if (classNode.fields.stream().anyMatch(fieldNode -> fieldNode.desc.equals("[Ljava/lang/Object;")))
                        incrementFlagsReached();
                }

                classNode.methods.forEach(methodNode -> {
                    InstructionList instructionList = new InstructionList(methodNode.instructions);

                    if (OpcodeUtils.getInstance().findOpcodes(stringerDecrypterStringEncryptionV9_15, instructionList))
                        incrementFlagsReached();

                    if (instructionList.size() > 600)
                        incrementFlagsReached();
                });

                if (raccoon.isDebugging() && getFlagsReached() != 0)
                    log("%s.class for V9_15 with certainty level: %d (%s)", classPath, getFlagsReached(), getFlagsReached() == 1 ? "Unsure" : getFlagsReached() == 2 ? "Undecisive" : "Confident");

                if (getFlagsReached() > 2)
                    res.add(classNode);

                this.reset();
            }
        });

        setResult(res);
        return true;
    }
}

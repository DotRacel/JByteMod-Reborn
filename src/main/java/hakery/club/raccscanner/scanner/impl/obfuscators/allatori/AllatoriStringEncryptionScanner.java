package hakery.club.raccscanner.scanner.impl.obfuscators.allatori;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.OpcodeUtils;
import hakery.club.raccscanner.util.opcodes.InstructionList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * V4
 * <p>
 * Put's a bunch of random integers and longs (key values) on the stack,
 * uses some xor trickery, so this is easily found by looking at Xor operations and
 * amount of values pushed onto the stack.
 *
 * New as of v0.4 -> Some character lenght related stuff is repeated in v4 (creating new array etc..)
 * due to the fact there isn't any stack scanning, the lenght of those arrays and characters isn't applied, and thus
 * should work on everything. As of 4/7/19 this isn't confirmed yet.
 * <p>
 * V3
 * Always starts with a new exception, getting stacktrace, initializing a new buffer
 * and getting the StackTraceElement name.
 */
public class AllatoriStringEncryptionScanner extends Scanner<ArrayList<ClassNode>> {

    private final InstructionList allatoriStringEncryptionV_3 = new InstructionList(Arrays.asList(
            Opcodes.ALOAD,
            Opcodes.ICONST_1,
            Opcodes.DUP,
            Opcodes.DUP_X2,
            Opcodes.NEW,
            Opcodes.DUP,
            Opcodes.INVOKESPECIAL,
            Opcodes.INVOKEVIRTUAL,
            Opcodes.SWAP,
            Opcodes.AALOAD,
            Opcodes.NEW,
            Opcodes.DUP,
            Opcodes.INVOKESPECIAL,
            Opcodes.SWAP,
            Opcodes.DUP,
            Opcodes.INVOKEVIRTUAL /* StackTraceElement#getClassName() */
    ));

    private final InstructionList allatoriStringEncryptionV4_1 = new InstructionList(Arrays.asList(
            Opcodes.ALOAD,
            Opcodes.INVOKEVIRTUAL,
            Opcodes.DUP,
            Opcodes.NEWARRAY,
            Opcodes.ICONST_1,
            Opcodes.DUP,
            Opcodes.POP2,
            Opcodes.SWAP,
            Opcodes.ICONST_1,
            Opcodes.ISUB,
            Opcodes.DUP_X2,
            Opcodes.ISTORE,
            Opcodes.ASTORE,
            Opcodes.ISTORE,
            Opcodes.DUP_X2,
            0xFF,
            Opcodes.ISTORE
    ));

    private final InstructionList allatoriStringEncryptionV4_2 = new InstructionList(Arrays.asList(
            Opcodes.INVOKEVIRTUAL,
            Opcodes.IINC,
            Opcodes.ILOAD,
            Opcodes.IXOR,
            Opcodes.I2C,
            Opcodes.CASTORE,
            Opcodes.ILOAD,
            Opcodes.IFLT,
            Opcodes.ALOAD,
            Opcodes.ALOAD,
            Opcodes.ILOAD,
            Opcodes.IINC,
            Opcodes.DUP_X1,
            Opcodes.INVOKEVIRTUAL
    ));

    @Override
    public boolean scan() {
        ArrayList<ClassNode> tmp = new ArrayList<>();

        raccoon.getClasses().forEach((classPath, classNode) -> classNode.methods.forEach(methodNode -> {
            InstructionList instructionList = new InstructionList(methodNode.instructions);

            /**
             * V4 Check
             */

            if (OpcodeUtils.getInstance().findOpcodes(allatoriStringEncryptionV4_1, instructionList)
                    && OpcodeUtils.getInstance().findOpcodes(allatoriStringEncryptionV4_2, instructionList)) {
                if (raccoon.isDebugging())
                    log("%s.class might contain encrypted strings using v4", classPath);

                tmp.add(classNode);
            }

            /**
             * End of V4 Check
             */

            /**
             * V3 Check *
             * */

            if (OpcodeUtils.getInstance().findOpcodes(allatoriStringEncryptionV_3, instructionList)) {
                if (raccoon.isDebugging())
                    log("%s.class might contain encrypted strings using v3", classPath);

                tmp.add(classNode);
            }

            /**
             * End of V3 Check
             */

            this.reset();
        }));

        this.setResult(tmp);
        return true;
    }


}

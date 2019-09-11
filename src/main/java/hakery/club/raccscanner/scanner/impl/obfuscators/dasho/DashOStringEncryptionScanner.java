package hakery.club.raccscanner.scanner.impl.obfuscators.dasho;

import hakery.club.raccscanner.scanner.Scanner;
import hakery.club.raccscanner.util.OpcodeUtils;
import hakery.club.raccscanner.util.opcodes.InstructionList;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;


public class DashOStringEncryptionScanner extends Scanner<ArrayList<ClassNode>> {

    final InstructionList dashO_V10_0_StringEncryption_MIN = new InstructionList(Arrays.asList(
            0xFF,
            DUP,
            IADD,
            DUP,
            0xFF,
            IADD,
            0xFF,
            ALOAD,
            INVOKEVIRTUAL,
            DUP,
            ARRAYLENGTH,
            ISTORE,
            ASTORE,
            ISTORE,
            ISHL,
            0xFF,
            ISUB,
            BIPUSH,
            IXOR,
            ISTORE
    ));

    @Override
    public boolean scan() {
        ArrayList<ClassNode> tmp = new ArrayList<>();

        this.raccoon.getClasses().forEach((classPath, classNode) -> {

            classNode.methods.forEach(methodNode -> {
                if (methodNode.desc.equals("(Ljava/lang/String;I)Ljava/lang/String;"))
                    incrementFlagsReached();

                InstructionList instructionList = new InstructionList(methodNode.instructions);

                if (instructionList.size() > 40)
                    incrementFlagsReached();

                if (OpcodeUtils.getInstance().findOpcodes(dashO_V10_0_StringEncryption_MIN, instructionList))
                    incrementFlagsReached();

            });

            if (getFlagsReached() >= 2) {
                if (raccoon.isDebugging())
                    log("found stub in %s.class with certainty level: %d (%s)", classPath, getFlagsReached(), getFlagsReached() == 1 ? "Unsure" : getFlagsReached() == 2 ? "Undecisive" : "Confident");

                tmp.add(classNode);
            }

            reset();
        });

        setResult(tmp);
        return true;
    }

}

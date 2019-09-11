package hakery.club.raccscanner.util.opcodes;

import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstructionList {

    private final ArrayList<Integer> opcodes = new ArrayList<>();

    public InstructionList(InsnList insnList) {
        /* fill the opcodes */
        Arrays.stream(insnList.toArray()).forEach(abstractInsnNode -> opcodes.add(abstractInsnNode.getOpcode()));
    }

    public InstructionList(List<Integer> list) {
        list.stream().forEach(integer -> opcodes.add(integer));
    }

    public InstructionList(int[] opcodes) {
        for (int opcode : opcodes)
            this.opcodes.add(opcode);
    }

    public int size() {
        return this.opcodes.size();
    }

    public int get(int idx) {
        return this.opcodes.get(idx);
    }

    public boolean isReplaceable(int idx) {
        return this.opcodes.get(idx) == 0xFF;
    }

    public ArrayList<Integer> getOpcodes() {
        return opcodes;
    }
}

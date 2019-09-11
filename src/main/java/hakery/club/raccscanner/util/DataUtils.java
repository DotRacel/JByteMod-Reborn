package hakery.club.raccscanner.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public enum DataUtils {
    INSTANCE;

    public byte[] toByteArray(InputStream is) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;

            byte[] data = new byte[1024 * 4 * 4];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean compareSectionOfByteArray(byte[] toFind, byte[] src, int offset) {
        int searchLen = toFind.length;

        for (int i = 0; i < searchLen; i++) {
            byte from = toFind[i];
            byte original = src[i + offset];

            if (from != original)
                return false;
        }
        return true;
    }

    public boolean findInByteArray(byte[] toFind, byte[] src) {
        if (toFind.length > src.length)
            return false;

        /* loop over every instruction in the source */
        for (int i = 0; i < src.length; i++) {
            if (compareSectionOfByteArray(toFind, src, i))
                return true;
        }

        return false;
    }
}

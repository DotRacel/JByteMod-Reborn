package hakery.club.raccscanner.logger;

import java.io.OutputStream;
import java.util.Formatter;

public class RaccoonLogger {

    private OutputStream targetOutputStream;

    public RaccoonLogger(OutputStream targetOutputStream) {
        this.targetOutputStream = targetOutputStream;
    }

    public RaccoonLogger() {
        this.targetOutputStream = System.out;
    }

    public void newLine() {
        try {
            this.targetOutputStream.write("\n".getBytes());
            this.targetOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log(String format, Object... formatting) {
        String message = new Formatter().format(format, formatting).toString();
        this.log(message);
    }

    public void log(String message) {
        try {
            String msg = "[Raccoon] " + message;
            this.targetOutputStream.write(msg.getBytes());
            this.newLine();
            this.targetOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTargetOutputStream(OutputStream targetOutputStream) {
        this.targetOutputStream = targetOutputStream;
    }

    public void error(String message) {
        try {
            String msg = "[Error] " + message;
            this.log(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

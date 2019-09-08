package me.grax.jbytemod.ui;

import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.decompiler.*;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.grax.jbytemod.decompiler.Decompilers.KOFFEE;

public class DecompilerTab extends JPanel {
    private static File tempDir = new File(System.getProperty("java.io.tmpdir"));
    private static File userDir = new File(System.getProperty("user.dir"));
    protected Decompilers decompiler = Decompilers.CFR;
    private DecompilerPanel dp;
    private JLabel label;
    private JByteMod jbm;
    private JButton compile = new JButton("Compile");

    public DecompilerTab(JByteMod jbm) {
        this.jbm = jbm;
        this.dp = new DecompilerPanel();
        this.label = new JLabel(decompiler + " Decompiler");
        jbm.setDP(dp);
        this.setLayout(new BorderLayout(0, 0));
        JPanel lpad = new JPanel();
        lpad.setBorder(new EmptyBorder(1, 5, 0, 1));
        lpad.setLayout(new GridLayout());
        lpad.add(label);
        JPanel rs = new JPanel();
        rs.setLayout(new GridLayout(1, 5));
        for (int i = 0; i < 3; i++)
            rs.add(new JPanel());
        JComboBox<Decompilers> decompilerCombo = new JComboBox<Decompilers>(Decompilers.values());
        decompilerCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DecompilerTab.this.decompiler = (Decompilers) decompilerCombo.getSelectedItem();
                label.setText(decompiler.getName() + " " + decompiler.getVersion());
                decompile(Decompiler.last, Decompiler.lastMn, true);
            }
        });
        rs.add(decompilerCombo);

        compile.setVisible(false);
    /*compile.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(decompiler != KOFFEE) {
          compile.setVisible(false);
          return;
        }

        synchronized (Decompiler.last){
          ClassNode classNode = new ClassNode();

          try {
            checkCompiler();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
          String toCompile = "ToCompile-" + Math.random() * 100 + ".kt";

          try{
            File file = new File(tempDir.getAbsolutePath() + "/" + toCompile);
            if(!file.exists()) {
              file.createNewFile();
            }else {
              file.delete();
              file.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(file, true);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(dp.getText());
            out.write(stringBuffer.toString().getBytes(StandardCharsets.UTF_8));

            out.close();

            JByteMod.LOGGER.println("Saved temp file to " + file.getAbsolutePath());

            String command = "" + userDir.getAbsolutePath() + "\\kotlinc\\bin\\kotlinc.bat" + " " + file.getAbsolutePath();
            JByteMod.LOGGER.println("Command: " + command);
            Runtime.getRuntime().exec(command);

            JByteMod.LOGGER.println("Compiled file with kotlinc.");

          }catch (FileNotFoundException exception) {} catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    });*/
        rs.add(compile);

        JButton reload = new JButton(JByteMod.res.getResource("reload"));
        reload.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                decompile(Decompiler.last, Decompiler.lastMn, true);
            }
        });
        rs.add(reload);
        lpad.add(rs);
        this.add(lpad, BorderLayout.NORTH);
        JScrollPane scp = new RTextScrollPane(dp);
        scp.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scp, BorderLayout.CENTER);
    }

    public static void checkCompiler() throws IOException {
        File kotlinc = new File(userDir.getAbsolutePath() + "/kotlinc");
        if (!userDir.exists()) {
            userDir.mkdirs();
        } else {
            if (kotlinc.exists()) {
                return;
            }
        }

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(DecompilerTab.class.getResourceAsStream("/resources/kotlinc.zip"));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(userDir, fileName);
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public void decompile(ClassNode cn, MethodNode mn, boolean deleteCache) {
        if (cn == null) {
            return;
        }
        Decompiler d = null;

        if (decompiler == KOFFEE) {
            //dp.setEditable(true);
            //compile.setVisible(true);
        } else {
            compile.setVisible(false);
            dp.setEditable(false);
        }

        switch (decompiler) {
            case PROCYON:
                d = new ProcyonDecompiler(jbm, dp);
                break;
            case FERNFLOWER:
                d = new FernflowerDecompiler(jbm, dp);
                break;
            case CFR:
                d = new CFRDecompiler(jbm, dp);
                break;
            case KRAKATAU:
                d = new KrakatauDecompiler(jbm, dp);
                break;
            case KOFFEE:
                d = new KoffeeDecompiler(jbm, dp);
                break;
        }
        d.setNode(cn, mn);
        if (deleteCache) {
            d.deleteCache();
        }
        d.start();
    }

    public void compile(ClassNode cn, MethodNode mn) {

    }
}
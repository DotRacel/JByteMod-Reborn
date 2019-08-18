package me.grax.jbytemod.utils.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.swing.SwingWorker;

import me.grax.jbytemod.discord.Discord;
import org.apache.commons.io.IOUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.JarArchive;
import me.grax.jbytemod.ui.PageEndPanel;
import me.grax.jbytemod.utils.ErrorDisplay;
import me.lpk.util.ASMUtils;

public class LoadTask extends SwingWorker<Void, Integer> {

	private ZipFile input;
	private PageEndPanel jpb;
	private JByteMod jbm;
	private File file;
	private int jarSize; // including directories
	private int loaded;
	private JarArchive ja;
	private long maxMem;
	private boolean memoryWarning;

	public LoadTask(JByteMod jbm, File input, JarArchive ja) {
		try {
			this.jarSize = countFiles(this.input = new ZipFile(input, "UTF-8"));
			JByteMod.LOGGER.log(jarSize + " files to load!");
			this.jbm = jbm;
			this.jpb = jbm.getPP();
			this.ja = ja;
			this.file = input;
			// clean old cache
			// ja.setClasses(null);
			this.maxMem = Runtime.getRuntime().maxMemory();
			this.memoryWarning = JByteMod.ops.get("memory_warning").getBoolean();
		} catch (IOException e) {
			new ErrorDisplay(e);
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		publish(0);
		this.loadFiles(input);
		publish(100);
		return null;
	}

	public int countFiles(final ZipFile zipFile) {
		final Enumeration<ZipEntry> entries = zipFile.getEntries();
		int c = 0;
		while (entries.hasMoreElements()) {
			entries.nextElement();
			++c;
		}
		return c;
	}

	/**
	 * loads both classes and other files at the same time
	 */
	public void loadFiles(ZipFile jar) throws IOException {
		long mem = Runtime.getRuntime().totalMemory();
		if (mem / (double) maxMem > 0.75) {
			JByteMod.LOGGER.warn("Memory usage is high: " + Math.round((mem / (double) maxMem * 100d)) + "%");
		}
		System.gc();
		Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
		Map<String, byte[]> otherFiles = new HashMap<String, byte[]>();

		final Enumeration<ZipEntry> entries = jar.getEntries();
		while (entries.hasMoreElements()) {
			readJar(jar, entries.nextElement(), classes, otherFiles);
		}
		jar.close();
		ja.setClasses(classes);
		ja.setOutput(otherFiles);
		return;
	}

	private void readJar(ZipFile jar, ZipEntry zipEntry, Map<String, ClassNode> classes,
			Map<String, byte[]> otherFiles) {
		long ms = System.currentTimeMillis();
		publish((int) (((float) loaded++ / (float) jarSize) * 100f));
		String name = zipEntry.getName();
		try (InputStream jis = jar.getInputStream(zipEntry)) {
			byte[] bytes = IOUtils.toByteArray(jis);
			if (name.endsWith(".class") || name.endsWith(".class/")) {
				synchronized (classes) {
					try{
						//JByteMod.LOGGER.log("Class file: " + name + "-" + bytes.length);
						String cafebabe = String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]);
						if (cafebabe.toLowerCase().equals("cafebabe")) {
							try {
								final ClassNode cn = convertToASM(bytes);
								if (cn != null) { // && (cn.name.equals("java/lang/Object") ? true : cn.superName != null)
									classes.put(cn.name, cn);
								}
							} catch (IllegalArgumentException e) {
								synchronized (otherFiles) {
									otherFiles.put(name, bytes);
								}
							}
						}
					}catch (ArrayIndexOutOfBoundsException ex){
						synchronized (otherFiles) {
							otherFiles.put(name, bytes);
						}
					}

				}
			} else {
				//JByteMod.LOGGER.log("Other file: " + name + "-" + bytes.length);

				synchronized (otherFiles) {
					otherFiles.put(name, bytes);
				}
			}

			if (memoryWarning) {
				long timeDif = System.currentTimeMillis() - ms;
				if (timeDif > 60 * 3 * 1000 && Runtime.getRuntime().totalMemory() / (double) maxMem > 0.95) { // if
					JByteMod.LOGGER.logNotification(JByteMod.res.getResource("memory_full"));
					publish(100);
					this.cancel(true);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JByteMod.LOGGER.err("Failed loading file");
		}
		return;
	}

	public static ClassNode convertToASM(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, ClassReader.EXPAND_FRAMES);
		return cn;
	}

	@Override
	protected void process(List<Integer> chunks) {
		int i = chunks.get(chunks.size() - 1);
		Discord.updatePresence("Loading " + file.getName() + " (" + i + "%)", "");
		jpb.setValue(i);
		super.process(chunks);
	}

	@Override
	protected void done() {
		JByteMod.lastEditFile = file.getName();
		Discord.updatePresence("Working on " + file.getName(), "Idle ...");
		JByteMod.LOGGER.log("Successfully loaded file!");
		jbm.refreshTree();
	}
}

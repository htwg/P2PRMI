package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.Files;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util.ClassUtil;

/**
 * An implementation of the {@link ICodebase} serving from
 * a string list, each entry must point to an existing 
 * directory.
 * 
 * TODO: Put should not write only to the first directory entry,
 * 		 maybe to a temp directory
 * TODO: allow only class and jar files to be saved?
 * 
 * @author Marco Krammer
 * 
 */
public class CodebaseLocalImpl implements ICodebase<String[]> {
	
	private Logger logger = Logger.getLogger(CodebaseLocalImpl.class.toString());

	private String[] dirPaths;
	private List<String> jarPathList = new ArrayList<>();
	private Map<String, String> classNameToJarPathMap = new HashMap<>();
	private Map<String, JarEntry> classNameToJarEntryMap = new HashMap<>();
	private Map<String, List<JarEntry>> jarPathToJarEntryMap = new HashMap<>();

	private boolean allow_save;	
	
	/**
	 * Constructor for CodebaseLocalImpl,
	 * sets the allow_save parameter
	 * 
	 * @param allow_save
	 */
	public CodebaseLocalImpl(boolean allow_save) {
		this.allow_save = allow_save;
	}
	
	/**
	 * Returns the file content as byte[]
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#get(java.lang.String)
	 */
	@Override
	public byte[] get(String name) throws IOException {
		byte[] bytes = null;
		logger.info("requesting " + name);
		if (name.toLowerCase().endsWith(".class")) {
			bytes = getClassBytes(name);
		} else { //TODO if (path.toLowerCase().endsWith(".jar")) {
			bytes = getFileBytes(name);
		}
		
		if (bytes != null) {
			logger.info("returning " + bytes.length + " bytes");
		}
		return bytes;
	}
	
	/**
	 * Saves a file to the first directory entry
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#put(java.lang.String, byte[])
	 */
	@Override
	public void put(String name, byte[] content) throws IOException {	//TODO which dirPath? prefix?
		if (allow_save) {
//			if (name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".class")) {
				File tosave = new File(dirPaths[0], name);
				if (! tosave.exists() && ! tosave.getParentFile().exists()) {
					logger.info("creating dirs for file path: " + tosave.getParentFile());
					if (! tosave.getParentFile().mkdirs()) {
						throw new IOException("Failed to create dirs");
					}
				}
//			} else {
//				logger.info("unknown file type: " + name);
//			}
			
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(tosave);
				fos.write(content);
				fos.flush();
				fos.close();
				logger.info("file " + tosave + " written " + content.length + " bytes");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Setter for the source
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#setSource(java.lang.Object)
	 */
	@Override
	public void setSource(String[] dirPaths) {
		if (dirPaths != null && dirPaths.length > 0) {
			this.dirPaths = dirPaths;
			
			jarPathList.clear();
			classNameToJarPathMap.clear();
			classNameToJarEntryMap.clear();
			jarPathToJarEntryMap.clear();
			
			indexDirectories();	//TODO start in setter?
		} else {
			logger.severe("Can't setDirPaths, parameter null or empty!");
		}
	}
	
	/**
	 * Getter for the source
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#getSource()
	 */
	@Override
	public String[] getSource() {
		return dirPaths;
	}
	
	private void indexDirectories() {
		for (int i = 0; i < dirPaths.length; i++) {
			File dir = new File(dirPaths[i]);
			indexDirectory(dir, dir);
		}
	}

	private void indexDirectory(File root, File dir) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				indexDirectory(root, files[i]);
			} else {
				indexJarEntries(root, files[i]);
			}
		}
	}

	private void indexJarEntries(File cbDir, File file) {
		if (! file.getName().toLowerCase().endsWith(".jar")) {
			return;
		}
		
		JarFile jarFile = null;
		try {
			if (! jarPathList.contains(file.getAbsolutePath())) {
				logger.fine("Indexing Jar " + file.getName());
				jarFile = new JarFile(file);
				
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = (JarEntry) entries.nextElement();
					String entryName = jarEntry.getName();
					if (entryName.toLowerCase().endsWith(".class")) {
						entryName = ClassUtil.toClassName(entryName);
						mapClassNameToJarFile(entryName, file.getAbsolutePath());
						mapClassNameToJarEntry(entryName, jarEntry, file.getAbsolutePath());
					}
				}
				jarPathList.add(file.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) { }
			}
		}
	}

	private void mapClassNameToJarEntry(String className, JarEntry jarEntry, String jarFile) {
		classNameToJarEntryMap.put(className, jarEntry);
		
		List<JarEntry> jes = jarPathToJarEntryMap.get(jarFile);
		if (null == jes) {
			jes = new ArrayList<>();
			jarPathToJarEntryMap.put(jarFile, jes);
		}
		jes.add(jarEntry);
	}

	private void mapClassNameToJarFile(String className, String jarFile) {
		classNameToJarPathMap.put(className, jarFile);
	}
	
	private byte[] getClassBytes(String path) {
		byte[] bytes = null;
		int length = 0;
		String className = "";
		
		try {
			className = ClassUtil.toClassName(path);
			logger.info("looking up " + className);
			
			DataInputStream in = null;
			
			// first look if its in a jar file
			JarFile jarFile = null;
			if (classNameToJarPathMap.get(className) != null) {
				jarFile = new JarFile(classNameToJarPathMap.get(className)); 
			}
			
			if (jarFile != null) {
				logger.info("found class in jar file: " + jarFile.getName());
	
				// get the jar entry for the class specified. We have to get the 
				// jar entry in order to get the input stream
				JarEntry jarEntry = (JarEntry) classNameToJarEntryMap.get(className);
				length = new Long(jarEntry.getSize()).intValue();
	
				// get input streams 
				InputStream is = jarFile.getInputStream(jarEntry);
				in = new DataInputStream(is);
			} else {
				// look in each dirPath for class file
				for (String dirPath : dirPaths) {
				    File file = new File(dirPath, path);
				    if (file.exists() && file.canRead()) {
					    length = Long.valueOf(file.length()).intValue();
					    if (length == 0) {
					      throw new IOException(className+": empty");	//TODO
					    } else {
					      in = new DataInputStream(new FileInputStream(file));
					    }
						logger.info("found class in " + dirPath);
					    break;
				    }
				}
			}
			
			if (in != null) {
				// get the data
				bytes = new byte[length];
				in.readFully(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		logger.info(length + " bytes returned for " + className);
		return bytes;
	}
	
	private byte[] getFileBytes(String path) {
		byte[] bytes = null;
		for (String dirPath : dirPaths) {
		    File file = new File(dirPath, path);
		    if (file.exists() && file.isFile() && file.canRead()) {
		    	try {
					bytes = Files.toByteArray(file);
			    	logger.info("found file in " + dirPath);
			    	break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    } //TODO dir / ?
		}
		return bytes;
	}
	
	
	/**
	 * Intended for debugging
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CodebaseLocalImpl serving from " + Arrays.toString(dirPaths);
	}

}

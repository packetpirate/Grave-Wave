package com.gzsr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.gzsr.entities.Attributes;

public class ConfigManager {
	private static OpenOption [] WRITE_OPTIONS = new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.CREATE };
	
	private static ConfigManager instance;
	public static ConfigManager getInstance() {
		if(instance == null) instance = new ConfigManager();
		return instance;
	}
	
	private Attributes attributes;
	public Attributes getAttributes() { return attributes; }
	
	private ConfigManager() {
		attributes = new Attributes();
	}
	
	public void init() {
		Path filePath = FileSystems.getDefault().getPath("settings", ".config");
		try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset())) {
			Attributes attributes = instance.getAttributes();
			String line = null;
			while((line = reader.readLine()) != null) {
				if(!line.isEmpty()) {
					String [] tokens = line.split(" |=");
					if(tokens.length == 3) {
						String type = tokens[0];
						String name = tokens[1];
						String value = tokens[2];
						
						if(type.equals("int")) {
							int v = Integer.parseInt(value);
							attributes.set(name, v);
							//System.out.printf("Loaded int \"%s\" with val %d!\n", name, v);
						} else if(type.equals("long")) {
							long v = Long.parseLong(value);
							attributes.set(name, v);
							//System.out.printf("Loaded long \"%s\" with val %d!\n", name, v);
						} else if(type.equals("float")) {
							float v = Float.parseFloat(value);
							attributes.set(name, v);
							//System.out.printf("Loaded float \"%s\" with val %.2f!\n", name, v);
						} else if(type.equals("double")) {
							double v = Double.parseDouble(value);
							attributes.set(name, v);
							//System.out.printf("Loaded double \"%s\" with val %.2f!\n", name, v);
						} else if(type.equals("boolean")) {
							boolean v = Boolean.parseBoolean(value);
							attributes.set(name, v);
							//System.out.printf("Loaded boolean \"%s\" with val %s!\n", name, (v ? "true" : "false"));
						} else {
							System.err.printf("Invalid property type specified for \"%s\"!", name);
						}
					} else System.err.println("Invalid property in config file!");
				}
			}
		} catch(FileNotFoundException fnf) {
			System.err.println("Unable to locate config file!");
			fnf.printStackTrace();
		} catch(IOException io) {
			System.err.println("Unable to close config file!");
			io.printStackTrace();
		}
	}
	
	public void save() {
		Path filePath = FileSystems.getDefault().getPath("settings", ".config");
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.defaultCharset(), WRITE_OPTIONS)) {
			Map<String, Object> attributesMap = attributes.getMap();
			for(Map.Entry<String, Object> entry : attributesMap.entrySet()) {
				String key = entry.getKey();
				Object val = entry.getValue();
				
				if(val instanceof Integer) {
					String str = String.format("int %s=%d", key, ((Integer)val).intValue());
					writer.write(str);
				} else if(val instanceof Long) {
					String str = String.format("long %s=%d", key, ((Long)val).longValue());
					writer.write(str);
				} else if(val instanceof Float) {
					String str = String.format("float %s=%.2f", key, ((Float)val).floatValue());
					writer.write(str);
				} else if(val instanceof Double) {
					String str = String.format("double %s=%.2f", key, ((Double)val).doubleValue());
					writer.write(str);
				} else if(val instanceof Boolean) {
					String str = String.format("boolean %s=%s", key, (((Boolean)val).booleanValue() ? "true" : "false"));
					writer.write(str);
				} else {
					System.err.printf("Problem writing property %s!\n", key);
					continue;
				}
				
				writer.newLine();
			}
		} catch(FileNotFoundException fnf) {
			System.err.println("Unable to locate config file!");
			fnf.printStackTrace();
		} catch(IOException io) {
			System.err.println("Unable to close / write to config file!");
			io.printStackTrace();
		}
	}
}

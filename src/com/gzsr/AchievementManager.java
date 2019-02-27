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
import java.util.Arrays;
import java.util.List;

import com.gzsr.achievements.Achievement;
import com.gzsr.controllers.AchievementController;

public class AchievementManager {
	private static OpenOption [] WRITE_OPTIONS = new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.CREATE };

	public static void init() {
		// Read achievement progress from the achievements data file.
		Path filePath = FileSystems.getDefault().getPath("data", ".achievements");
		try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset())) {
			String line = null;
			List<Achievement> achievements = AchievementController.getInstance().getAchievements();
			while((line = reader.readLine()) != null) {
				if(!line.isEmpty()) {
					// Find the achievement matching the ID of this line.
					String [] tokens = line.split(" ");
					int id = Integer.parseInt(tokens[0]);
					boolean match = false;
					if(tokens.length > 1) {
						for(Achievement achievement : achievements) {
							if(id == achievement.getID()) {
								// Pass the line to the achievement for parsing.
								achievement.parseSaveData(Arrays.copyOfRange(tokens, 1, tokens.length));
								System.out.printf("Achievement Loaded From Save: %s\n", achievement.getName());
								match = true;
								break;
							}
						}
					}

					if(!match) System.err.println("Unknown achievement ID detected in data file!");
				}
			}
		} catch(FileNotFoundException fnf) {
			System.err.println("Unable to locate achievements file!");
			fnf.printStackTrace();
		} catch(IOException io) {
			System.err.println("Unable to close achievements file!");
			io.printStackTrace();
		} catch(NumberFormatException nfe) {
			System.err.println("Malformed achievements file! Could not load achievement progress.");
			nfe.printStackTrace();
		}
	}

	public static void save() {
		Path filePath = FileSystems.getDefault().getPath("data", ".achievements");
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.defaultCharset(), WRITE_OPTIONS)) {
			List<Achievement> achievements = AchievementController.getInstance().getAchievements();
			for(Achievement achievement : achievements) {
				if(achievement.isEarned() || !achievement.resets()) {
					String format = achievement.saveFormat();
					writer.write(format);
					writer.newLine();
				}
			}
		} catch(FileNotFoundException fnf) {
			System.err.println("Unable to locate achievements file!");
			fnf.printStackTrace();
		} catch(IOException io) {
			System.err.println("Unable to close / write to achievements file!");
			io.printStackTrace();
		}
	}
}

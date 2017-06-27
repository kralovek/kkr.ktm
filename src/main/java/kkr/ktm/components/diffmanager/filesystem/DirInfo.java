package kkr.ktm.components.diffmanager.filesystem;

import java.util.regex.Pattern;

public class DirInfo {
	private String name;
	private String path;
	private Pattern pattern;
	private boolean content = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMask(String mask) {
		if (mask != null) {
			pattern = Pattern.compile(mask);
		}
	}

	public Pattern getPattern() {
		return pattern;
	}

	public boolean isContent() {
		return content;
	}

	public void setContent(boolean content) {
		this.content = content;
	}
	
	public String toString() {
		return "[" + name + "] " + path;
	}
}

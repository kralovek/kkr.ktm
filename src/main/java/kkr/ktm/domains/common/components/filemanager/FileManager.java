package kkr.ktm.domains.common.components.filemanager;

import kkr.common.errors.BaseException;

public interface FileManager {

	boolean isFile(String filename, String dir) throws BaseException;

	String fileToContent(String filename, String encoding, String dir) throws BaseException;

	void contentToFile(String content, String filename, String encoding, String dir) throws BaseException;

	void contentToGzFile(String content, String filename, String encoding, String dir) throws BaseException;
}

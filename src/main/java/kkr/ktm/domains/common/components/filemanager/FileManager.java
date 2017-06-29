package kkr.ktm.domains.common.components.filemanager;

import kkr.ktm.exception.BaseException;

public interface FileManager {

	void contentToFile(String content, String filename, String encoding, String dir) throws BaseException;
	void contentToGzFile(String content, String filename, String encoding, String dir) throws BaseException;
}

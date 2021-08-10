package kr.co.bigsnow.core.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import org.springframework.util.FileCopyUtils;

import lombok.extern.java.Log;

@Log
public class UploadFileUtils {
	


	public static String uploadFile(String uploadPath, String originalName, byte[] fileData) throws Exception {
		
		//확장자 추출
		int pos = originalName.lastIndexOf(".");
		String ext = originalName.substring(pos + 1);
		//저장파일명 생성 (UUID)
		String savedName = UUID.randomUUID().toString().replaceAll("-", "") + "." + ext;
		//savefullpathName설정
		String uploadedFullPathName = uploadPath + File.separator + savedName;
		uploadedFullPathName = uploadedFullPathName.substring(uploadPath.length()).replace(File.separatorChar, '/');
		//파일 저장
		File target = new File(uploadPath, savedName);
		FileCopyUtils.copy(fileData, target);

		return uploadedFullPathName;
	}


	public static String calcPath(String uploadPath) {
		Calendar cal = Calendar.getInstance();

		String yearPath = File.separator + cal.get(Calendar.YEAR);
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
		String replaceUploadPath = uploadPath.replace(File.separatorChar, '/');
		
		makeDir(replaceUploadPath, yearPath, monthPath, datePath);

		return replaceUploadPath + datePath;
	}

	private static void makeDir(String uploadPath, String... paths) {
		if (new File(paths[paths.length - 1]).exists()) {
			return;
		}

		for (String path : paths) {
			File dirPath = new File(uploadPath + path);

			if (!dirPath.exists()) {
				dirPath.mkdir();
			}
		}
	}

}

package generate.p2;

import java.io.File;
import java.io.IOException;

import constant.Constants;
import util.CommUtil;

public class PrepareBuilds {

	static String localDestFileStr = System.getProperty("localDestFileStr");
	static String licensePrefix = System.getProperty("licensePrefix");
	static String smbDestFolderStr = System.getProperty("smbDestFolderStr");

	public static void main(String[] args) throws IOException {
		new PrepareBuilds().generatP2();
	}

	public void generatP2() throws IOException {
		// get latest build folder
		String latstBuldRtFlderStr = CommUtil.getLatstBuldRtFlderStr();
		System.err.println("latest build: " + latstBuldRtFlderStr);

		localDestFileStr = localDestFileStr + File.separator + CommUtil.latstBuldRtFlderNm;

		// get license
		File latstLicense = CommUtil.getLatstLicenseFile(latstBuldRtFlderStr);
		System.err.println("latest license: " + latstLicense);
		String destLicenseFileStr = CommUtil.copyBuild(latstLicense, localDestFileStr);
		CommUtil.unzip(destLicenseFileStr, localDestFileStr);
		System.err.println("unzip license done: " + destLicenseFileStr);

		// get mixed license
		File latstMixedLicense = CommUtil.getLatstMixedLicenseFile(latstBuldRtFlderStr);

		// get studio
		String allFolerStr = latstBuldRtFlderStr + File.separator + Constants.ALL;
		File latstStudioFile = CommUtil.getLatstBuildFile(allFolerStr, Constants.V_PREFIX, Constants.SUBALL,Constants.STUDIO_PREFIX, Constants.ZIP_SUFFIX);

		// get full p2
		File latstFullP2File = CommUtil.getLatstFullP2BuildFile(allFolerStr, Constants.FullP2_PREFIX,Constants.ZIP_SUFFIX);
		System.out.println(latstFullP2File.getAbsolutePath());

		// get ci builder
		String ciFolerStr = latstBuldRtFlderStr + File.separator + Constants.CIFolder;
		File latstCIBuilderFile = CommUtil.getLatstCIBuildERFile(ciFolerStr, Constants.CIBuilder_PREFIX,Constants.ZIP_SUFFIX);
		System.out.println(latstCIBuilderFile.getAbsolutePath());
		
		// get signer file
		File latstSignerFile = CommUtil.getLatstCIBuildERFile(ciFolerStr, Constants.CISigner_PREFIX,Constants.ZIP_SUFFIX);
		System.out.println(latstSignerFile.getAbsolutePath());
				

		// upload studio
		if (Boolean.getBoolean("isNeedStudio")) {
			try {
				CommUtil.copyBuild(latstStudioFile, smbDestFolderStr);
				System.err.println("upload studio done");
			} catch (Exception e1) {
				System.err.println("upload studio failed");
			}
		}

		// upload License
		if (Boolean.getBoolean("isNeedLicense")) {
			try {
				CommUtil.copyBuild(latstLicense, smbDestFolderStr);
				System.err.println("upload License done");
			} catch (Exception e1) {
				System.err.println("upload License failed");
			}
		}

		// upload mixed License
		if (Boolean.getBoolean("isNeedLicense")) {
			try {
				CommUtil.copyBuild(latstMixedLicense, smbDestFolderStr);
				System.err.println("upload mixed License done");
			} catch (Exception e1) {
				System.err.println("upload mixed License failed");
			}
		}
		
		// upload full p2
		if (Boolean.getBoolean("isNeedfullP2")) {
			try {
				CommUtil.copyBuild(latstFullP2File, System.getProperty("smbDestFolderStr"));
				System.err.println("upload FullP2 done");
			} catch (Exception e1) {
				System.err.println("upload FullP2 failed");
			}
		}

		// upload ci builder
		if (Boolean.getBoolean("isNeedCIBuilder")) {
			try {
				CommUtil.copyBuild(latstCIBuilderFile, System.getProperty("smbDestFolderStr"));
				System.err.println("upload CIBuilder done");
			} catch (Exception e1) {
				System.err.println("upload CIBuilder failed");
			}
		}		
		// upload signer build
				if (Boolean.getBoolean("isNeedCISigner")) {
					try {
						CommUtil.copyBuild(latstSignerFile, smbDestFolderStr);
						System.err.println("upload SignerFile done");
					} catch (Exception e1) {
						System.err.println("upload SignerFile failed");
					}
		}
				
		try {
			System.err.println("latest studio: " + latstStudioFile);
			String destStudioFileStr = CommUtil.copyBuild(latstStudioFile, localDestFileStr);
			CommUtil.unzip(destStudioFileStr, localDestFileStr);
			System.err.println("unzip studio done: " + destStudioFileStr);

			// get swtbotP2
			String swtFolderStr = latstBuldRtFlderStr + File.separator + Constants.SWT;
			File latstSwtbotP2File = CommUtil.getLatstBuildFile(swtFolderStr, Constants.SWT_PREFIX, "", "",Constants.ZIP_SUFFIX);
			System.err.println("latest swtbotP2: " + latstSwtbotP2File);
			String destSwtbotP2FileStr = CommUtil.copyBuild(latstSwtbotP2File, localDestFileStr);
			String swtbotP2FolderString = localDestFileStr + File.separator	+ latstSwtbotP2File.getName().replace(".zip", "");
			CommUtil.unzip(destSwtbotP2FileStr, swtbotP2FolderString);
			System.err.println("unzip swtbotP2 done: " + destSwtbotP2FileStr);

			// copy license to Studio
			String licenseFolderStr = destLicenseFileStr.replace(".zip", "");
			String studioFolderStr = destStudioFileStr.replace(".zip", "");
			File licenseFile = CommUtil.getFilesWithStartEndFilter(new File(licenseFolderStr), licensePrefix, "");
			CommUtil.copyBuild(licenseFile, studioFolderStr);
			System.err.println("copy license to studio done");

			// generate p2
			String commandStr = studioFolderStr + File.separator + Constants.STUDIO_EXE	+ " -nosplash -consoleLog -application org.eclipse.equinox.p2.director -repository file:///" + swtbotP2FolderString + " -installIU org.talend.swtbot.update.site.feature.feature.group";
			CommUtil.runCommand(commandStr, null);
			System.err.println("generate p2 done");

			// zip p2
			File localDestFile = new File(localDestFileStr);
			String studioFolderNameStr = latstStudioFile.getName();
			String zipCommStr = "jar -cMf " + Constants.P2_PREFIX + studioFolderNameStr + " "+ studioFolderNameStr.replace(".zip", "");
			CommUtil.runCommand(zipCommStr, localDestFile);
			System.err.println("zip p2 done");

			// upload p2
			File p2SrcFileStr = CommUtil.getFilesWithStartEndFilter(localDestFile, Constants.P2_PREFIX, "");
			CommUtil.copyBuild(p2SrcFileStr, smbDestFolderStr);
			System.err.println("upload p2 done");

			// copy tac
			if (Boolean.getBoolean("isNeedTAC")) {
				try {
					File latstTACFile = CommUtil.getLatstBuildFile(allFolerStr, Constants.V_PREFIX, Constants.SUBALL,Constants.TAC_PREFIX, Constants.ZIP_SUFFIX);
					CommUtil.copyBuild(latstTACFile, smbDestFolderStr);
					System.err.println("upload tac done");
				} catch (Exception e1) {
					System.err.println("upload tac failed");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// clean up locally download folder
			CommUtil.deleteFolder(localDestFileStr);
		}

	}
}

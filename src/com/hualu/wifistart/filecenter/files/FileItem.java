package com.hualu.wifistart.filecenter.files;

import android.graphics.Bitmap;

public class FileItem {

	private String FileName;
	private String extraName;
	private String FilePath;
	private boolean isDirectory;
	
	private long fileSize;
	
	private long createData;
	private String modifyData;
	
	private boolean canWrite;
	private boolean canRead;
	private boolean isHide;

    private int iconId;
    private Bitmap icon;

	public void setFileName(String fileName) {
		this.FileName = fileName;
	}

	public String getFileName() {
		return FileName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}

	public String getExtraName() {
		return extraName;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	public boolean isSmbFile(){
		return FilePath.startsWith("smb");
	}
	public boolean isDirectory() {
		return isDirectory;
	}

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setCreateData(long createData) {
        this.createData = createData;
    }

    public long getCreateData() {
        return createData;
    }

    public void setModifyData(String modifyData) {
        this.modifyData = modifyData;
    }

    public String getModifyData() {
        return modifyData;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setHide(boolean isHide) {
        this.isHide = isHide;
    }

    public boolean isHide() {
        return isHide;
    }
    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
    public int getIconId() {
        return iconId;
    }

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
}

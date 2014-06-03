package me.lazerka.meme.api;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Link;
import com.googlecode.objectify.annotation.Ignore;

/**
 * @author Dzmitry Lazerka
 */
public class Image {
	BlobKey blobKey;

	String fileName;

	int size;

	/**
	 * UI only -- serving url through ImagesService, to serve directly from Blobstore, bypassing server.
	 */
	@Ignore
	Link url;

	public Image() {
	}

	public Image(BlobKey blobKey, String fileName, int size) {
		this.blobKey = blobKey;
		this.fileName = fileName;
		this.size = size;
	}

	public BlobKey getBlobKey() {
		return blobKey;
	}

	public String getFileName() {
		return fileName;
	}

	public int getSize() {
		return size;
	}

	public String getUrl() {
		return url.getValue();
	}

	public void setUrl(String url) {
		this.url = new Link(url);
	}

	@Override
	public String toString() {
		return "Image{" +
				"blobKey=" + blobKey +
				", fileName='" + fileName + '\'' +
				", size=" + size +
				", url=" + url +
				'}';
	}
}

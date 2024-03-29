package szeweq.craftery.util;

import szeweq.craftery.net.Downloader;
import szeweq.craftery.net.Downloader;
import szeweq.desktopose.core.LongBiConsumer;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public interface FileLoader {
	CompletableFuture<InputStream> load(LongBiConsumer progress);

	static FileLoader fromURL(final String url) {
		Objects.requireNonNull(url);
		final String urlFixed = url.replace(" ", "%20");
		return progress -> Downloader.downloadFile(urlFixed, progress);
	}

	static FileLoader fromFile(final File file) {
		Objects.requireNonNull(file);
		return progress -> CompletableFuture.supplyAsync(() -> {
			var out = new ByteArrayOutputStream(5120);
			try {
				var fis = new FileInputStream(file);
				var len = file.length();
				progress.accept(0, len);
				var copied = 0L;
				var buf = new byte[4096];
				var bytes = fis.read(buf);
				while (bytes >= 0) {
					out.write(buf, 0, bytes);
					copied += bytes;
					progress.accept(copied, len);
					bytes = fis.read(buf);
				}
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ByteArrayInputStream(out.toByteArray());
		});
	}
}

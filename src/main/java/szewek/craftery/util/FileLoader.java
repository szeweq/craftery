package szewek.craftery.util;

import kotlin.Unit;

import java.io.*;
import java.util.Objects;

public interface FileLoader {
	InputStream load(LongBiConsumer progress);

	static FileLoader fromURL(final String url) {
		Objects.requireNonNull(url);
		return progress -> Downloader.INSTANCE.downloadFile(url, progress);
	}

	static FileLoader fromFile(final File file) {
		Objects.requireNonNull(file);
		return progress -> {
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
		};
	}
}

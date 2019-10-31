package net.pk.traas.server.controller.update;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import net.pk.data.type.AbstractValue;
import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * Takes care of data stream entries coming from the stream engine. The stream
 * data is stored in a file directory given by
 * {@link EnvironmentConfig#getStreamFileDir()}.
 * 
 * @author peter
 *
 */
public abstract class FileInputController<V extends AbstractValue> {

	private Class<V> type;

	private Thread fileWatchThread;

	/**
	 * @param type
	 */
	public FileInputController(Class<V> type) {
		this.type = type;
	}

	/**
	 * This method handles the update process. It is responsible for reading new
	 * stream values and store it in a shared storage object.
	 */
	protected abstract void update();

	/**
	 * The type of the values that this controller is caring of.
	 * 
	 * @return type of values
	 */
	public Class<V> getType() {
		return type;
	}

	/**
	 * Start file watch service {@link FileWatchService} which observes if a file
	 * has changed. If so, this object's {@link #update()} method is called.
	 */
	public void start() {
		fileWatchThread = new Thread(new FileWatchService());
		fileWatchThread.start();
	}

	/**
	 * Stop {@link FileWatchService}.
	 */
	public void stop() {
		fileWatchThread.interrupt();
	}

	/**
	 * The update job is done in this runnable class.
	 * 
	 * @author peter
	 *
	 */
	private class FileWatchService implements Runnable {

		@Override
		public void run() {
			final Path dirPath = Paths.get(EnvironmentConfig.getInstance().getStreamFileDir());
			final Path absoluteFilePath = Paths.get(EnvironmentConfig.getInstance().getAbsoluteFilePathByType(type));
			try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
				dirPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
				while (true) {
					final WatchKey wk = watchService.take();
					for (WatchEvent<?> event : wk.pollEvents()) {
						// we only register "ENTRY_MODIFY" so the context is always a Path.
						final Path changed = (Path) event.context();
						if (absoluteFilePath.equals(changed.toAbsolutePath())) {
							FileInputController.this.update();
						}
					}
					// reset the key
					wk.reset();
				}
			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

package system.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import system.Logger;
import system.TeaseViewer;
import system.downloader.MilovanaGallery.GalleryEntry;
import utilities.Encryptor;
import utilities.PathUtilities;
import utilities.ScriptConverter;

public abstract class TeaseDownloader implements Runnable {

	boolean zip;
	String sourceUrl;
	String teaseID;
	float performedActionsIdentification;
	int totalActionsIdentification = 2;
	float identificationWeight = 1;
	float performedActionsScript;
	int totalActionsScript = 1;
	float scriptWeight = 1;
	float performedActionsMedia;
	int totalActionsMedia = 2;
	float mediaWeight = 10;
	float performedActionsCompress = 1;
	int totalActionsCompress = 1;
	float compressWeight = 0;
	String teaseTitle;
	String teaseAuthor;
	String authorID;
	String mediaFolder;
	boolean cba;
	int maxRetries = 5;
	String finalTarget;
	String thumbnailURL;
	TeaseViewer app;

	public TeaseDownloader(TeaseViewer app) {
		this.app = app;
	}

	@Override
	public void run() {
		identifyTease();
		downloadMedia();
		report("Completed download", PROGRESS);
		onCompletion();
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public void setTarget(String target) {
		this.finalTarget = target;
	}

	public void setCba(boolean cba) {
		this.cba = cba;
	}

	public void setZip(boolean zip) {
		this.zip = zip;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public abstract void updateProgress(float progress);

	public void updateProgress() {
		float progress = performedActionsIdentification / totalActionsIdentification * identificationWeight
				+ performedActionsScript / totalActionsScript * scriptWeight
				+ performedActionsMedia / totalActionsMedia * mediaWeight;
		progress /= (identificationWeight + scriptWeight + mediaWeight);
		updateProgress(progress);
	}

	public abstract void report(String message, int severity);

	public static final int PROGRESS = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;

	private void identifyTease() {
		Pattern patt = Pattern.compile("id=(\\d*)");
		Matcher mat = patt.matcher(sourceUrl);
		updateProgress(0f);
		if (!mat.find()) {
			report("Unidentifiable tease: " + sourceUrl, ERROR);
			return;
		}
		teaseID = mat.group(1);
		sourceUrl = "https://milovana.com/webteases/showtease.php?id=" + teaseID;
		report("Starting download of " + sourceUrl, PROGRESS);
		String flashURL = "https://milovana.com/webteases/showflash.php?id=" + teaseID;

		boolean isNyxOnly = false;
		try {
			Document teaseDataDoc = Jsoup.connect(sourceUrl).get();
			performedActionsIdentification = 1;
			updateProgress();

			if (teaseDataDoc.title().equalsIgnoreCase("Milovana.com - This tease has been removed.")) {
				teaseDataDoc = Jsoup.connect(flashURL).get();
				if (!teaseDataDoc.title().equalsIgnoreCase("Milovana.com - This tease has been removed.")) {
					isNyxOnly = true;
				} else {
					report("Tease has been removed: " + sourceUrl, ERROR);
					return;
				}
			} else if (teaseDataDoc.title().equalsIgnoreCase("Milovana.com - This tease is invisible.")) {
				report("Tease is invisible: " + sourceUrl, ERROR);
				return;
			} else if (teaseDataDoc.title().equalsIgnoreCase("Milovana.com - Tease not found.")) {
				report("Tease not found: " + sourceUrl, ERROR);
				return;
			}

			if (isNyxOnly) {
				Element headerBar = teaseDataDoc.getElementById("headerbar");
				Element title = headerBar.child(0);
				Element author = title.getElementsByTag("a").get(0);
				teaseTitle = title.ownText().substring(0, title.ownText().length() - 3);
				teaseAuthor = author.ownText();
				if (teaseAuthor.length() < 1) {
					teaseAuthor = "UnknownName";
				}
				authorID = author.attr("href").replace("webteases/?author=", "");

				finalTarget = finalTarget + (cba ? ("\\" + teaseAuthor) : "") + "\\"
						+ PathUtilities.makeCompliantPath(teaseTitle + " - " + teaseAuthor) + ".tease";
				performedActionsIdentification = totalActionsIdentification;
				updateProgress();
				report("Identification complete", PROGRESS);
				downloadNYX();
			} else {
				Elements els = teaseDataDoc.head().getElementsByAttributeValue("name", "viewport");
				if (els.size() > 0) {
					Element body = teaseDataDoc.body();
					teaseTitle = body.attr("data-title");
					teaseAuthor = body.attr("data-author");
					if (teaseAuthor.length() < 1) {
						teaseAuthor = "UnknownName";
					}
					authorID = body.attr("data-author-id");
					mediaFolder = "https://milovana.com/media/get.php?folder=" + authorID + "/" + teaseID + "&name=";
					finalTarget = finalTarget + (cba ? ("\\" + teaseAuthor) : "") + "\\"
							+ PathUtilities.makeCompliantPath(teaseTitle + " - " + teaseAuthor) + ".tease";
					performedActionsIdentification = totalActionsIdentification;
					updateProgress();
					report("Identification complete", PROGRESS);
					downloadEOS();
				} else {
					Element el1 = teaseDataDoc.getElementById("tease_title");
					teaseTitle = el1.ownText();
					teaseAuthor = el1.child(0).child(0).ownText();
					if (teaseAuthor.length() < 1) {
						teaseAuthor = "UnknownName";
					}
					authorID = el1.child(0).child(0).attr("href").replace("webteases/?author=", "");
					finalTarget = finalTarget + (cba ? ("\\" + teaseAuthor) : "") + "\\"
							+ PathUtilities.makeCompliantPath(teaseTitle + " - " + teaseAuthor) + ".tease";
					performedActionsIdentification = totalActionsIdentification;
					updateProgress();
					report("Identification complete", PROGRESS);
					downloadClassic();
				}
			}

		} catch (IOException ioe) {
			report(ioe.getLocalizedMessage(), ERROR);
		}
	}

	boolean flag = false;

	public void stop() {
		flag = true;
	}

	public void createTeaseFile() {
		File targetFile = new File(finalTarget);
		targetFile.getParentFile().mkdir();
		if (targetFile.exists()) {
			int choice = requestInput(targetFile.getName() + " already exists");
			switch (choice) {
			case OVERWRITE:
				targetFile.delete();
				break;
			case RENAME:
				int index = 1;
				String baseName = finalTarget;
				String name;
				do {
					name = baseName.replaceFirst("\\.tease$", " \\(" + index + "\\)\\.tease");
					index++;
				} while (new File(name).exists());
				finalTarget = name;
				break;
			default:
				stop();
				return;
			}
		}
	}

	public static final int CANCEL = -1;
	public static final int OVERWRITE = 0;
	public static final int RENAME = 2;

	public abstract int requestInput(String str);

	TreeMap<String, String> media;
	ArrayList<String> imagesUrl;

	private void downloadClassic() {
		createTeaseFile();
		scriptWeight = 10;
		totalActionsScript = 20;
		totalActionsMedia = 20;
		try {
			boolean end = false;
			String pageURL;
			int id = 0;
			ArrayList<String> pagesContent = new ArrayList<String>();
			imagesUrl = new ArrayList<String>();
			media = new TreeMap<String, String>();
			Document page;
			Element pageContent;
			Element script;
			Element image;
			Element link;
			while (!end) {
				if (flag) {
					return;
				}
				id++;
				pageURL = "https://milovana.com/webteases/showtease.php?id=" + teaseID + "&p=" + id + "#t";
				page = Jsoup.connect(pageURL).get();
				if (page.title().equalsIgnoreCase("Milovana.com - Page not found.")) {
					end = true;
					break;
				}
				pageContent = page.getElementById("tease_content");
				if (!pageContent.getElementsByTag("script").isEmpty()) {
					link = pageContent.getElementsByClass("link").get(0);
					link.remove();
					script = pageContent.getElementsByTag("script").get(0);
					script.remove();
				} else {
					end = true;
				}
				String rawContent = pageContent.html().replaceAll("<br>", "<br/>");
				pagesContent.add(rawContent.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\\\"", "\\\\\"")
						.replaceAll("“", "&quot;").replaceAll("”", "&quot;").replaceAll("’", "&apos;")
						.replaceAll("‘", "&apos;").replaceAll("…", "..."));
				image = page.getElementsByClass("tease_pic").get(0);
				imagesUrl.add(image.attr("src").replace("tb_l/", ""));
				performedActionsScript++;
				if (id >= totalActionsScript) {
					totalActionsScript++;
					totalActionsMedia++;
				}
				updateProgress();
			}
			int pad = (((int) Math.log10(pagesContent.size())) + 1);
			for (int ii = 0; ii < pagesContent.size(); ii++) {
				media.put(String.format("%0" + pad + "d", ii + 1) + ".jpg", imagesUrl.get(ii));
			}
			totalActionsScript = (int) performedActionsScript + 1;
			totalActionsMedia = totalActionsScript - 1;
			report("Completed script reading", PROGRESS);
			updateProgress();
			String scriptText = "";
			scriptText += ("{\"modules\":{\"nyx\":{},\"pcm2\":{},\"audio\":{}},\"pages\":");
			scriptText += ("{" + "\"start" + "\":[{\"nyx.page\":{\"media\":{\"nyx.image\":\"file:"
					+ String.format("%0" + pad + "d", 1) + ".jpg\"},\"text\":\"" + pagesContent.get(0)
					+ "\",\"action\":{\"nyx.buttons\":[{\"label\":\"Continue\",\"commands\":[{\"goto\":{\"target\":\""
					+ 2 + "\"}}]}]}}}]");
			for (int ii = 2; ii <= pagesContent.size(); ii++) {
				if (ii + 1 <= pagesContent.size()) {
					scriptText += ("\"" + ii + "\":[{\"nyx.page\":{\"media\":{\"nyx.image\":\"file:"
							+ String.format("%0" + pad + "d", ii) + ".jpg\"},\"text\":\""
							+ pagesContent.get(ii - 1).replaceAll("\"", "\\\"")
							+ "\",\"action\":{\"nyx.buttons\":[{\"label\":\"Continue\",\"commands\":[{\"goto\":{\"target\":\""
							+ Integer.toString((ii + 1)) + "\"}}]}]}}}]");
				} else {
					scriptText += ("\"" + ii + "\":[{\"nyx.page\":{\"media\":{\"nyx.image\":\"file:"
							+ String.format("%0" + pad + "d", ii) + ".jpg\"},\"text\":\""
							+ pagesContent.get(ii - 1).replaceAll("\"", "\\\"") + "\"}}]");
				}
			}

			scriptText += ("\"\":[{\"nyx.dummy\":[]}]}}");

			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			Path path = Paths.get(finalTarget);
			URI uri = URI.create("jar:" + path.toUri());
			try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
				Path nf = fs.getPath("script.json");
				try (Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
					writer.write(scriptText);
				} catch (Exception e) {
					report(e.getLocalizedMessage(), ERROR);
				}
			} catch (Exception e) {
				report(e.getLocalizedMessage(), ERROR);
			}

			performedActionsScript++;
			updateProgress();
			report("Completed script Creation", PROGRESS);
		} catch (IOException mue) {
			report(mue.getLocalizedMessage(), ERROR);
		}
	}

	private void downloadNYX() {

	}

	private void downloadEOS() {
		createTeaseFile();
		totalActionsScript = 3;
		totalActionsMedia = 20;
		try {
			URL scriptURL;
			scriptURL = new URL("https://milovana.com/webteases/geteosscript.php?id=" + teaseID);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(scriptURL.openStream()))) {
				String script = "";
				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					script += inputLine + "\n";
				}
				boolean nyx = script.contains("\"\":[{\"nyx.dummy\":[]}]");
				TreeMap<String, String> teaseData = new TreeMap<>();
				teaseData.put("teaseID", teaseID);
				teaseData.put("teaseTitle", teaseTitle);
				teaseData.put("teaseAuthor", teaseAuthor);
				teaseData.put("authorID", authorID);
				teaseData.put("nyx", Boolean.toString(nyx));

				report("Completed script reading", PROGRESS);
				performedActionsScript++;
				updateProgress();
				Pattern patt = Pattern.compile("\"files\":\\{(.+?)\\}\\}");
				media = new TreeMap<String, String>();
				TreeMap<String, Long> mediaSize = new TreeMap<String, Long>();
				Matcher mat = patt.matcher(script);
				if (mat.find()) {
					String files = mat.group(1);
					String[] splitStrings = files.split("\\},\"");
					splitStrings[0] = splitStrings[0].substring(1);
					patt = Pattern.compile("\"hash\":\"(.+?)\"");
					Pattern patt2 = Pattern.compile("\"size\":\"(\\d+?)");

					for (String str : splitStrings) {
						String fileName = str.substring(0, str.indexOf('"'));
						String fileExt = fileName.substring(fileName.lastIndexOf('.'));

						mat = patt.matcher(str);

						if (mat.find()) {
							String path = "https://media.milovana.com/timg/" + mat.group(1) + fileExt;
							media.put(fileName, path);
							mat = patt2.matcher(str);
							if (mat.find()) {
								mediaSize.put(path, Long.parseLong(mat.group(1)));
							}
						}
					}
				}

				patt = Pattern.compile("\"galleries\":\\{(.+?\\})\\}");
				mat = patt.matcher(script);
				if (mat.find()) {
					ArrayList<MilovanaGallery> galleries = MilovanaGallery.parseGalleries(mat.group(1));
					if (galleries != null && !galleries.isEmpty()) {
						this.galleries = galleries;
					}
				}
				performedActionsScript++;
				updateProgress();
				report("Completed media indexing", PROGRESS);

				Map<String, String> env = new HashMap<>();
				env.put("create", "true");
				Path path = Paths.get(finalTarget);
				URI uri = URI.create("jar:" + path.toUri());
				try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
					Path nf = fs.getPath("script.json");
					if (nyx) {
						try (Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8,
								StandardOpenOption.CREATE)) {
							writer.write(script);
							performedActionsScript++;
							updateProgress();
						} catch (Exception e) {
							report(e.getLocalizedMessage(), ERROR);
						}
					} else {
						String key = Integer.toHexString(teaseData.hashCode());
						if ((boolean) app.getParameters().get("encrypt")) {
							byte[] bytes = Encryptor.encryptString(script, key);
							try (OutputStream os = Files.newOutputStream(nf, StandardOpenOption.CREATE)) {
								os.write(bytes);
								performedActionsScript++;
								updateProgress();
							} catch (Exception e) {
								report(e.getLocalizedMessage(), ERROR);
							}
						} else {
							try (Writer bw = Files.newBufferedWriter(nf, StandardOpenOption.CREATE)) {
								bw.write(script);
								performedActionsScript++;
								updateProgress();
							} catch (Exception e) {
								report(e.getLocalizedMessage(), ERROR);
							}
						}

					}
					nf = fs.getPath("tease.data");
					try (OutputStream os = Files.newOutputStream(nf, StandardOpenOption.CREATE);
							ObjectOutputStream oos = new ObjectOutputStream(os)) {
						oos.writeObject(teaseData);
						updateProgress();
					} catch (Exception e) {
						report(e.getLocalizedMessage(), ERROR);
					}

				} catch (Exception e) {
					report(e.getLocalizedMessage(), ERROR);
				}

			} catch (IOException e) {
				report(e.getLocalizedMessage(), ERROR);
			} catch (Exception e) {
				report(e.getLocalizedMessage(), ERROR);
			}
		} catch (MalformedURLException mue) {
			report(mue.getLocalizedMessage(), ERROR);
		}

	}

	ArrayList<MilovanaGallery> galleries = null;;

	private void downloadMedia() {
		if (flag) {
			return;
		}
		if (media != null) {
			totalActionsMedia += media.size();
		}

		if (galleries != null) {
			for (MilovanaGallery gallery : galleries) {
				totalActionsMedia += gallery.getEntries().size();
			}
		}
		if (thumbnailURL != null) {
			totalActionsMedia++;
		}

		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		Path path = Paths.get(finalTarget);
		URI uri = URI.create("jar:" + path.toUri());
		try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
			Files.createDirectories(fs.getPath("media"));
			int fails = 0;
			if (thumbnailURL != null) {
				InputStream is = null;
				HttpURLConnection conn;
				URL url;
				try {
					url = new URL(thumbnailURL);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					is = conn.getInputStream();
					try (BufferedInputStream isr = new BufferedInputStream(is);
							OutputStream fos = Files.newOutputStream(fs.getPath("thumbnail.jpg"),
									StandardOpenOption.CREATE)) {
						final byte data[] = new byte[1024];
						int count;
						while ((count = isr.read(data, 0, 1024)) != -1) {
							fos.write(data, 0, count);
						}
						fos.flush();
						performedActionsMedia++;
						updateProgress();
					} catch (IOException e) {
						report(e.getLocalizedMessage(), WARNING);
						e.printStackTrace();
						fails++;
					}
				} catch (Exception e) {
					report(e.getLocalizedMessage(), ERROR);
				}
			}
			if (media != null) {
				for (String str : media.keySet()) {
					if (flag) {
						return;
					}
					fails = 0;
					InputStream is = null;

					for (int ii = 0; ii < maxRetries; ii++) {
						HttpURLConnection conn;
						URL url;
						try {
							if (ii > maxRetries / 2) {
								String temp = media.get(str).replace("/timg/", "/timg/tb_xl/");
								url = new URL(temp);
							} else if (ii > maxRetries - 2) {

								url = new URL("https://milovana.com/media/get.php?folder=" + authorID + "/" + teaseID
										+ "&name=" + str);
							} else {
								url = new URL(media.get(str));
							}
							conn = (HttpURLConnection) url.openConnection();
							conn.setRequestMethod("GET");
							is = conn.getInputStream();

							try (BufferedInputStream isr = new BufferedInputStream(is);
									OutputStream fos = Files.newOutputStream(fs.getPath("media" + File.separator + str),
											StandardOpenOption.CREATE)) {
								final byte data[] = new byte[1024];
								int count;
								while ((count = isr.read(data, 0, 1024)) != -1) {
									fos.write(data, 0, count);
								}
								fos.flush();
								ii = maxRetries;
								performedActionsMedia++;
								updateProgress();
							} catch (IOException e) {
								report(e.getLocalizedMessage(), WARNING);
								e.printStackTrace();
								fails++;
							}
						} catch (MalformedURLException e) {
							report(e.getLocalizedMessage(), ERROR);
							fails = maxRetries;
						} catch (IOException e) {
							report(e.getLocalizedMessage(), WARNING);
							fails++;
						} finally {
							if (is != null) {
								try {
									is.close();
								} catch (Exception e) {
									report(e.getLocalizedMessage(), ERROR);
								}
							}
						}

						if (fails == maxRetries) {
							String msg = "https://milovana.com/media/get.php?folder=" + authorID + "/" + teaseID
									+ "&name=" + str;
							report("Failed downloading: " + msg, ERROR);
						}
					}
				}
			}
			if (galleries != null) {
				for (MilovanaGallery gallery : galleries) {
					if (gallery.getEntries() == null || gallery.getEntries().isEmpty()) {
						continue;
					}
					Files.createDirectories(fs.getPath("media/" + gallery.getID()));
					for (GalleryEntry entry : gallery.getEntries()) {
						if (flag) {
							return;
						}
						String fileUrl = "https://media.milovana.com/timg/" + entry.getHash() + ".jpg";
						String str = gallery.getID() + File.separator + entry.getId() + ".jpg";

						InputStream is = null;
						fails = 0;
						for (int ii = 0; ii < maxRetries; ii++) {
							try {
								HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
								conn.setRequestMethod("GET");
								is = conn.getInputStream();
								try (BufferedInputStream isr = new BufferedInputStream(is);
										OutputStream fos = Files.newOutputStream(
												fs.getPath("media" + File.separator + str),
												StandardOpenOption.CREATE)) {
									final byte data[] = new byte[1024];
									int count;
									while ((count = isr.read(data, 0, 1024)) != -1) {
										fos.write(data, 0, count);
									}
									ii = maxRetries;
									performedActionsMedia++;
									updateProgress();
								} catch (IOException e) {
									report(e.getLocalizedMessage(), WARNING);
									e.printStackTrace();
									fails++;
								}
							} catch (IOException e) {
								report(e.getLocalizedMessage(), WARNING);
								fails++;
								Logger.staticLog("https://milovana.com/media/get.php?folder=" + authorID + "/" + teaseID
										+ "&name=" + entry.getId(), Logger.WARNING);
							} finally {
								if (is != null) {
									try {
										is.close();
									} catch (IOException e) {
										report(e.getLocalizedMessage(), ERROR);
									}
								}
							}
							if (fails == maxRetries) {
								report("Error, reached maximum retries", ERROR);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		totalActionsMedia = (int) performedActionsMedia;
		updateProgress();
		report("Completed media download", PROGRESS);
	}

	public void onCompletion() {

	}

	public TreeMap<File, String> createEntries(File f) {
		return createEntries(f, null);
	}

	public TreeMap<File, String> createEntries(File f, String name) {
		TreeMap<File, String> entries = new TreeMap<>();
		for (File f1 : f.listFiles()) {
			if (f1.isDirectory()) {
				entries.putAll(createEntries(f1, (name == null ? "" : name + File.separator) + f1.getName()));
			} else {
				entries.put(f1, (name == null ? "" : name + File.separator) + f1.getName());
			}
		}
		return entries;
	}

	void deleteDirectoryRecursion(File file) throws IOException {
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteDirectoryRecursion(entry);
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}
}

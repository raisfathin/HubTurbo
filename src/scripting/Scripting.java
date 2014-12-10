package scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.controlsfx.dialog.Dialogs;

public class Scripting {
	
	private String prelude;
	private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	private HashMap<String, String> scriptFiles = new HashMap<>();
	
	public Scripting() {
		prelude = readPrelude();
	}
	
	private ArrayList<Path> getScripts() {

		File directory = new File("scripts");
		if (!directory.exists()) {
			directory.mkdir();
		}

		final ArrayList<Path> result = new ArrayList<Path>();
		try {
			Files.walk(Paths.get("scripts")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)
					&& filePath.getFileName().toString().endsWith(".js")) {
					result.add(filePath);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<String> getScriptNames() {
		List<String> names = getScripts().stream().map(p -> {
				String[] temp = p.toString().split("\\/");
				return temp[temp.length-1].replaceAll("\\..*$", "");
			}).collect(Collectors.toList());
		
		for (int i=0; i<names.size(); i++) {
			scriptFiles.put(names.get(i), getScripts().get(i).toString());
		}
		
		return names;
	}
	
	public static void alert(String message) {
		Dialogs.create().lightweight().title("Alert").message(message).showInformation();
	}
	
	public void run(String scriptName) {
		if (scriptFiles.containsKey(scriptName)) {
			System.out.println("Scripting: running " + scriptName);
			try {
				String script = prelude + readFile(scriptFiles.get(scriptName));
				engine.eval(script);
				System.out.println("Scripting: finished running " + scriptName);
			} catch (Exception e) {
				System.out.println("Scripting: " + scriptName + " failed");
				e.printStackTrace();
			}
		} else {
			System.out.println("Scripting: nothing done");
			// Do nothing
		}
	}
	
	private String readFile(String path) throws IOException {
		File file = new File(path);
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		reader = new BufferedReader(new FileReader(file));
	    String line;
	    while ((line = reader.readLine()) != null) {
	    	sb.append(line);
	    	sb.append("\n");
	    }
	    reader.close();
	    return sb.toString();
	}
	
	private String readPrelude() {
		ClassLoader classLoader = Scripting.class.getClassLoader();
		File file = new File(classLoader.getResource("scripting/Prelude.js").getFile());
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(file));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	sb.append(line);
		    	sb.append("\n");
		    }
		    reader.close();
		    return sb.toString();
		} catch (Exception e) {
			System.out.println("Unable to read Prelude.js");
			e.printStackTrace();
		}
        return "";
	}
	
	public static void main(String[] args) throws IOException {
		
	}
}